package fl.wf.universalmemorizingassistant;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.R.attr.name;
import static android.os.Environment.getExternalStorageDirectory;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "FLWFSettingsActivity";
    private String[] bookNames;
    private File[] bookFiles;
    File appFolderFile;
    File bookListFile;
    MyFileHandler.ExtensionFilter xlsFilter;

    TextView presentBookTextView;

    ListView booksListView;
    Intent editIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        presentBookTextView = (TextView) findViewById(R.id.tv_settings_present_book);
        booksListView = (ListView) findViewById(R.id.lv_settings_books);

        editIntent = new Intent();

        bookListFile = new File(getExternalStorageDirectory() + BasicStaticData.appFolder + BasicStaticData.appBookDataFileName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appFolderFile = new File(getExternalStorageDirectory() + BasicStaticData.appFolder);
        xlsFilter = new MyFileHandler.ExtensionFilter(".xls");
        updateBookNamesAndUI();
    }

    void updateBookNamesAndUI() {
        bookFiles = appFolderFile.listFiles(xlsFilter);
        bookNames = MyFileHandler.filesToStrings(bookFiles);

        booksListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, bookNames));
        presentBookTextView.setText("PresentBook:" + getPresentBook() + "\nBooks available:");
    }

    void setPresentBook(String bookNameToSet) {
        SharedPreferences presentBook = getSharedPreferences("presentBook", MODE_PRIVATE);
        SharedPreferences.Editor editor = presentBook.edit();
        editor.putString("presentBook", bookNameToSet);
        editor.commit();
    }

    String getPresentBook() {
        SharedPreferences presentBook = getSharedPreferences("presentBook", MODE_PRIVATE);
        return presentBook.getString("presentBook", "choose one!");
    }

    public void onSetThisClicked(View view) {
        int position = booksListView.getCheckedItemPosition();
        setPresentBook(bookNames[position]);
        updateBookNamesAndUI();
    }

    public void onAddBookClicked(final View view) {
        final View addBookView = getLayoutInflater().inflate(R.layout.dialog_add_book, null);

        final Spinner timesSpinner = (Spinner) addBookView.findViewById(R.id.sp_dialog_add_target_times);
        timesSpinner.setSelection(4, true);

//        SpinnerAdapter timesSpinnerAdapter = timesSpinner.getAdapter();

        new AlertDialog.Builder(this)
                .setTitle("Add a new book")
                .setView(addBookView)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText addBookEditText = (EditText) addBookView.findViewById(R.id.et_dialog_add_book);
                        String name = addBookEditText.getText().toString();
                        if (!name.equals("")) {
                            for (int i = 0; i < name.length(); i++) {
                                if (!Character.isLetterOrDigit(name.charAt(i)) &&
                                        !Character.toString(name.charAt(i)).equals("_") &&
                                        !Character.toString(name.charAt(i)).equals("-")) {
                                    Toast.makeText(SettingsActivity.this, "Illegal Character", Toast.LENGTH_SHORT).show();
                                    onAddBookClicked(view);
                                    return;
                                }
                            }
                            File newBookFile = new File(getExternalStorageDirectory() + BasicStaticData.appFolder + "/" + name + ".xls");
                            int createResult = MyFileHandler.createNewFile(newBookFile);
                            switch (createResult) {
                                case MyFileHandler.CREATE_ALREADY_EXISTS:
                                    Toast.makeText(SettingsActivity.this, "Already exists", Toast.LENGTH_SHORT).show();
                                    onAddBookClicked(view);
                                    break;
                                case MyFileHandler.CREATE_FAILED:
                                    Toast.makeText(SettingsActivity.this, "Failed,try again", Toast.LENGTH_SHORT).show();
                                    onAddBookClicked(view);
                                    break;
                                case MyFileHandler.CREATE_SUCCESS:
                                    HSSFWorkbook wb;
                                    try {
                                        wb = BookAccessor.createWorkbook();
                                        BookAccessor.closeAndSaveBook(wb, newBookFile);
                                        int times = timesSpinner.getSelectedItemPosition() + 1;
                                        ArrayList<Book> bookList = MyFileHandler.addBookToList(MyFileHandler.readFromBookDataFile(bookListFile), "/" + newBookFile.getName(), times);
                                        MyFileHandler.writeToBookDataFile(bookList, bookListFile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    Toast.makeText(SettingsActivity.this, "Create succeeded", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            updateBookNamesAndUI();
                        } else {
                            Toast.makeText(SettingsActivity.this, "Enter the name of book", Toast.LENGTH_SHORT).show();
                            // TODO: 2017/5/23  i'm not sure if this FINAL VIEW will cause any problems...
                            onAddBookClicked(view);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void onDeleteBookClicked(View view) {
        if (booksListView.getCheckedItemPosition() == -1) {
            Toast.makeText(this, "Choose a book first", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Confirmation")
                .setMessage("Sure to delete?")
                .setPositiveButton("Sure,delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File fileToDelete = bookFiles[booksListView.getCheckedItemPosition()];
                        boolean deleted = fileToDelete.delete();
                        if (!deleted)
                            Toast.makeText(SettingsActivity.this, "Delete Failed", Toast.LENGTH_SHORT).show();
                        else {
                            updateBookNamesAndUI();
                            ArrayList<Book> bookList = MyFileHandler.deleteBookFromList(MyFileHandler.readFromBookDataFile(bookListFile), "/" + fileToDelete.getName());
                            MyFileHandler.writeToBookDataFile(bookList, bookListFile);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void onEditClicked(View view) {
        if (booksListView.getCheckedItemPosition() == -1) {
            Toast.makeText(this, "Choose a book first", Toast.LENGTH_SHORT).show();
            return;
        }

        editIntent = MyFileHandler.getExcelFileIntent(bookFiles[booksListView.getCheckedItemPosition()], this);
        startActivity(editIntent);
    }

    public void onConfigClicked(final View view) {
        // TODO: 2017/5/23  config here!
        if (booksListView.getCheckedItemPosition() == -1) {
            Toast.makeText(this, "Choose a book first", Toast.LENGTH_SHORT).show();
            return;
        }
        final View setBookView = getLayoutInflater().inflate(R.layout.dialog_config_book, null);
        final Spinner timesSpinner = (Spinner) setBookView.findViewById(R.id.sp_dialog_set_target_times);
        final EditText setBookEditText = (EditText) setBookView.findViewById(R.id.et_dialog_config_book);

        File fileToUpdate = bookFiles[booksListView.getCheckedItemPosition()];
        String bookName = "/" + fileToUpdate.getName();
        setBookEditText.setText(MyFileHandler.getFileNameNoEx(fileToUpdate.getName()));
        ArrayList<Book> bookList = MyFileHandler.readFromBookDataFile(bookListFile);
// FIXME: 2017/5/25 maxTime show still have a bug.
        int times = 5;
        for (Book b : bookList) {
            if (b.getName().equals(bookName))
                times = b.getMaxTimes() - 1;
        }
        timesSpinner.setSelection(times, true);

        new AlertDialog.Builder(this)
                .setTitle("Config book")
                .setView(setBookView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File fileToUpdate = bookFiles[booksListView.getCheckedItemPosition()];
                        String oldName = "/" + fileToUpdate.getName();
                        String name = setBookEditText.getText().toString();
                        if (!name.equals("")) {
                            for (int i = 0; i < name.length(); i++) {
                                if (!Character.isLetterOrDigit(name.charAt(i)) &&
                                        !Character.toString(name.charAt(i)).equals("_") &&
                                        !Character.toString(name.charAt(i)).equals("-")) {
                                    Toast.makeText(SettingsActivity.this, "Illegal Character", Toast.LENGTH_SHORT).show();
                                    onConfigClicked(view);
                                    return;
                                }
                            }
                            for (File f : bookFiles) {
                                if (f.getName().equals(name)) {
                                    Toast.makeText(SettingsActivity.this, "Name Already Used!", Toast.LENGTH_SHORT).show();
                                    onConfigClicked(view);
                                    return;
                                }
                            }
                            //rename bookFile here
                            boolean renamed = fileToUpdate.renameTo(new File(getExternalStorageDirectory() + BasicStaticData.appFolder + "/" + name + ".xls"));
                            if (!renamed) {
                                Toast.makeText(SettingsActivity.this, "Rename Failed", Toast.LENGTH_SHORT).show();
                                onConfigClicked(view);
                                return;
                            }
                        } else name = null;

                        ArrayList<Book> bookList = MyFileHandler.readFromBookDataFile(bookListFile);
                        int newTimes = timesSpinner.getSelectedItemPosition() + 1;
                        CheckBox resetCheckBox = (CheckBox) setBookView.findViewById(R.id.cb_dialog_set_reset);
                        boolean reset = resetCheckBox.isChecked();

                        MyFileHandler.updateBookFromList(bookList, oldName, name, newTimes, reset);
                        MyFileHandler.writeToBookDataFile(bookList, bookListFile);

                        updateBookNamesAndUI();
                        //rename bookFileName in the dataFile here
                        // TODO: 2017/5/25 change here
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

        File bookFileToConfig = bookFiles[booksListView.getCheckedItemPosition()];
    }
}
