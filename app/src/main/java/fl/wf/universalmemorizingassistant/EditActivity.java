package fl.wf.universalmemorizingassistant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.IOException;

public class EditActivity extends AppCompatActivity {

    TextView presentEditTextView;
    File bookFile;
    int bookMaxTimes;
    ListView rowsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_edit);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        presentEditTextView = (TextView) findViewById(R.id.tv_edit_present_book);
        rowsListView = (ListView) findViewById(R.id.lv_edit_rows);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String bookName = getIntent().getStringExtra("bookName");
        String toShow = getString(R.string.text_present_editing_book) + bookName;
        presentEditTextView.setText(toShow);

        String bookPath = BasicStaticData.absAppFolderPath + "/" + bookName;
        bookFile = new File(bookPath);

        String bookNameWithPath = "/" + bookName;
        Book presentBook = MyFileHandler.getBook(BasicStaticData.appBookDataFile, bookNameWithPath);
        if (presentBook != null) {
            bookMaxTimes = presentBook.getMaxTimes();
        } else {
            finish();
        }

        updateList();
    }

    void updateList() {
        String[] rows = null;
        try {
            HSSFWorkbook wb = new BookHandler(this).openAndValidateBook(bookFile, bookMaxTimes);
            rows = BookHandler.workbookToStrings(wb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (rows != null)
            rowsListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, rows));
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
