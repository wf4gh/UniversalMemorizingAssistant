package fl.wf.universalmemorizingassistant;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_settings);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        presentBookTextView = (TextView) findViewById(R.id.tv_settings_present_book);
        booksListView = (ListView) findViewById(R.id.lv_settings_books);
        editIntent = new Intent();
        bookListFile = BasicStaticData.appBookDataFile;
    }

    @Override
    protected void onResume() {
        super.onResume();
        appFolderFile = BasicStaticData.appFolderFile;
        xlsFilter = new MyFileHandler.ExtensionFilter(".xls");
        updateBookNamesAndUI();
    }

    void updateBookNamesAndUI() {
        bookFiles = appFolderFile.listFiles(xlsFilter);
        bookNames = MyFileHandler.filesToStrings(bookFiles);

        booksListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, bookNames));
        presentBookTextView.setText(getString(R.string.text_present_book) + getPresentBook() + "\n\n" + getString(R.string.text_available_book));
    }

    void setPresentBook(String bookNameToSet) {
        SharedPreferences presentBook = getSharedPreferences("presentBook", MODE_PRIVATE);
        SharedPreferences.Editor editor = presentBook.edit();
        editor.putString("presentBook", bookNameToSet);
        editor.apply();
    }

    String getPresentBook() {
        SharedPreferences presentBook = getSharedPreferences("presentBook", MODE_PRIVATE);
        return presentBook.getString("presentBook", getString(R.string.present_book_undefined));
    }

    public void onSetThisClicked(View view) {
        if (!isBookSelectedWithToast())
            return;

        int position = booksListView.getCheckedItemPosition();
        setPresentBook(bookNames[position]);
        updateBookNamesAndUI();
    }

    public void onAddBookClicked(final View view) {
        @SuppressLint("InflateParams") final View addBookView = getLayoutInflater().inflate(R.layout.dialog_add_book, null);

        final Spinner timesSpinner = (Spinner) addBookView.findViewById(R.id.sp_dialog_add_target_times);
        timesSpinner.setSelection(4, true);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title_create_book))
                .setView(addBookView)
                .setPositiveButton(getString(R.string.dialog_button_create), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText addBookEditText = (EditText) addBookView.findViewById(R.id.et_dialog_add_book);
                        String name = addBookEditText.getText().toString();
                        if (!name.equals("")) {
                            for (int i = 0; i < name.length(); i++) {
                                if (!Character.isLetterOrDigit(name.charAt(i)) &&
                                        !Character.toString(name.charAt(i)).equals("_") &&
                                        !Character.toString(name.charAt(i)).equals("-") &&
                                        !Character.toString(name.charAt(i)).equals(" ")) {
                                    Toast.makeText(SettingsActivity.this, getString(R.string.toast_illegal_character), Toast.LENGTH_SHORT).show();
                                    onAddBookClicked(view);
                                    return;
                                }
                            }
                            File newBookFile = new File(BasicStaticData.absAppFolderPath + "/" + name + ".xls");
                            int createResult = MyFileHandler.createNewFile(newBookFile);
                            switch (createResult) {
                                case MyFileHandler.CREATE_ALREADY_EXISTS:
                                    Toast.makeText(SettingsActivity.this, getString(R.string.toast_already_exists), Toast.LENGTH_SHORT).show();
                                    onAddBookClicked(view);
                                    break;
                                case MyFileHandler.CREATE_FAILED:
                                    Toast.makeText(SettingsActivity.this, getString(R.string.toast_create_failed), Toast.LENGTH_SHORT).show();
                                    onAddBookClicked(view);
                                    break;
                                case MyFileHandler.CREATE_SUCCESS:
                                    HSSFWorkbook wb;
                                    try {
                                        wb = BookHandler.createWorkbookWithTitle();
                                        BookHandler.closeAndSaveBook(wb, newBookFile);
                                        int times = timesSpinner.getSelectedItemPosition() + 1;
                                        ArrayList<Book> bookList = MyFileHandler.addBookToList(MyFileHandler.readFromBookDataFile(bookListFile), "/" + newBookFile.getName(), times);
                                        MyFileHandler.writeToBookDataFile(bookList, bookListFile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    Toast.makeText(SettingsActivity.this, getString(R.string.toast_created), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            updateBookNamesAndUI();
                        } else {
                            Toast.makeText(SettingsActivity.this, getString(R.string.toast_book_need_name), Toast.LENGTH_SHORT).show();
                            // i'm not sure if this FINAL VIEW will cause any problems...
                            onAddBookClicked(view);
                        }
                    }
                })
                .setNegativeButton(getString(R.string.dialog_button_cancel), null)
                .show();
    }

    boolean isBookSelectedWithToast() {
        if (booksListView.getCheckedItemPosition() == -1) {
            Toast.makeText(this, getString(R.string.toast_book_not_chosen), Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }

    public void onDeleteBookClicked(View view) {
        if (!isBookSelectedWithToast())
            return;

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title_delete))
                .setMessage(getString(R.string.dialog_message_delete) + "\n\n" + bookFiles[booksListView.getCheckedItemPosition()].getName())
                .setPositiveButton(getString(R.string.dialog_button_delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File fileToDelete = bookFiles[booksListView.getCheckedItemPosition()];
                        boolean deleted = fileToDelete.delete();
                        if (!deleted)
                            Toast.makeText(SettingsActivity.this, getString(R.string.toast_delete_fail), Toast.LENGTH_SHORT).show();
                        else {
                            updateBookNamesAndUI();
                            ArrayList<Book> bookList = MyFileHandler.deleteBookFromList(MyFileHandler.readFromBookDataFile(bookListFile), "/" + fileToDelete.getName());
                            MyFileHandler.writeToBookDataFile(bookList, bookListFile);
                        }
                    }
                })
                .setNegativeButton(getString(R.string.dialog_button_cancel), null)
                .show();
    }

    public void onEditClicked(View view) {
        if (!isBookSelectedWithToast())
            return;

        editIntent = MyFileHandler.getExcelFileIntent(bookFiles[booksListView.getCheckedItemPosition()], this);
        try {
            startActivity(editIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.toast_no_app_found_to_open), Toast.LENGTH_SHORT).show();
            // TODO: 2017/5/26  maybe there should be a popup here to ask the player to choose an app?
        }
    }

    public void onConfigClicked(final View view) {
        if (!isBookSelectedWithToast())
            return;

        @SuppressLint("InflateParams") final View setBookView = getLayoutInflater().inflate(R.layout.dialog_config_book, null);
        final Spinner timesSpinner = (Spinner) setBookView.findViewById(R.id.sp_dialog_set_target_times);
        final EditText setBookEditText = (EditText) setBookView.findViewById(R.id.et_dialog_config_book);


        Log.d(TAG, "onClick: length of bookFiles: " + bookFiles.length);
        Log.d(TAG, "onClick: checked position:" + booksListView.getCheckedItemPosition());

        File fileToUpdate = bookFiles[booksListView.getCheckedItemPosition()];
        String bookName = "/" + fileToUpdate.getName();
        setBookEditText.setText(MyFileHandler.getFileNameNoEx(fileToUpdate.getName()));
        ArrayList<Book> bookList = MyFileHandler.readFromBookDataFile(bookListFile);

        int times = 4;
        for (Book b : bookList) {
            if (b.getName().equals(bookName)) {
                times = b.getMaxTimes() - 1;
            }
        }
        timesSpinner.setSelection(times, true);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title_config))
                .setView(setBookView)
                .setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
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
                                    Toast.makeText(SettingsActivity.this, getString(R.string.toast_illegal_character), Toast.LENGTH_SHORT).show();
                                    onConfigClicked(view);
                                    return;
                                }
                            }
                            name = "/" + name + ".xls";
                            if (name.equals(oldName))
                                name = null;
                            else {
                                //rename bookFile here
                                boolean renamed = fileToUpdate.renameTo(new File(BasicStaticData.absAppFolderPath + name));
                                if (!renamed) {
                                    Toast.makeText(SettingsActivity.this, getString(R.string.toast_rename_fail), Toast.LENGTH_SHORT).show();
                                    onConfigClicked(view);
                                    return;
                                }
                            }
                        } else name = null;

                        ArrayList<Book> bookList = MyFileHandler.readFromBookDataFile(bookListFile);
                        int newTimes = timesSpinner.getSelectedItemPosition() + 1;
                        CheckBox resetCheckBox = (CheckBox) setBookView.findViewById(R.id.cb_dialog_set_reset);
                        boolean reset = resetCheckBox.isChecked();

                        bookList = MyFileHandler.updateBookFromList(bookList, oldName, name, newTimes, reset);
                        MyFileHandler.writeToBookDataFile(bookList, bookListFile);

                        updateBookNamesAndUI();
                    }
                })
                .setNegativeButton(getString(R.string.dialog_button_cancel), null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
