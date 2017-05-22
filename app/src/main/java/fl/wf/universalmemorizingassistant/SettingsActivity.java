package fl.wf.universalmemorizingassistant;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

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
}
