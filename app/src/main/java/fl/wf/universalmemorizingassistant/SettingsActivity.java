package fl.wf.universalmemorizingassistant;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Objects;

import static android.R.attr.name;
import static android.os.Environment.getExternalStorageDirectory;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "FLWFSettingsActivity";
    private String[] bookNames;
    private File[] bookFiles;
    File appFolderFile;
    BasicFile.ExtensionFilter xlsFilter;

    TextView presentBookTextView;

    ListView booksListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        presentBookTextView = (TextView) findViewById(R.id.tv_settings_present_book);
        booksListView = (ListView) findViewById(R.id.lv_settings_books);
    }

    @Override
    protected void onResume() {
        super.onResume();

        appFolderFile = new File(getExternalStorageDirectory() + BasicStaticData.appFolder);
        xlsFilter = new BasicFile.ExtensionFilter(".xls");
        updateBookNamesAndUI();
    }

    void updateBookNamesAndUI() {
        bookFiles = appFolderFile.listFiles(xlsFilter);
        bookNames = BasicFile.filesToStrings(bookFiles);

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
        new AlertDialog.Builder(this)
                .setTitle("Add a new book")
                .setView(addBookView)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText addBookEditText = (EditText) addBookView.findViewById(R.id.et_dialog_add_book);
                        String name = addBookEditText.getText().toString();
                        if (!name.equals("")) {
                            // TODO: 2017/5/23 create a new xls here
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
                        else
                            updateBookNamesAndUI();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
