package fl.wf.universalmemorizingassistant;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.IOException;

public class EditActivity extends AppCompatActivity {

    private static final String TAG = "FLWFEditActivity";

    TextView presentEditTextView;
    File bookFile;
    int bookMaxTimes;
    ListView rowsListView;
    String bookName;

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
        bookName = getIntent().getStringExtra("bookName");
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

    public void onAddClicked(View view) {

        @SuppressLint("InflateParams") final View addView = getLayoutInflater().inflate(R.layout.dialog_quick_add, null);
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_add)
                .setView(addView)
                .setPositiveButton(getString(R.string.dialog_button_add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText hintEditText = (EditText) addView.findViewById(R.id.et_dialog_quick_add_hint);
                        String hint = hintEditText.getText().toString();
                        if (hint.equals("")) {
                            Toast.makeText(EditActivity.this, getString(R.string.toast_hint_needed), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        EditText answerEditText = (EditText) addView.findViewById(R.id.et_dialog_quick_add_answer);
                        String answer = answerEditText.getText().toString();
                        if (answer.equals(""))
                            answer = getString(R.string.sheet_no_data);
//                                Log.d(TAG, "onClick: \nhint:" + hint + "\nans:" + answer);
                        Book book = MyFileHandler.getBook(BasicStaticData.appBookDataFile, "/" + bookName);

                        //think this is not likely to happen now...
                        if (book == null) {
//                            Log.e(TAG, "onClick: book Null!!!");
                            book = new Book();
                            book.setMaxTimes(5);
                        }

                        try {
                            HSSFWorkbook wb = new BookHandler(getApplicationContext()).openAndValidateBook(bookFile, book.getMaxTimes());
                            wb = new BookHandler(getApplicationContext()).addNewLineToWorkbook(wb, hint, answer, false);
                            BookHandler.closeAndSaveBook(wb, bookFile);
                            Toast.makeText(EditActivity.this, getString(R.string.toast_added), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        updateList();
                    }
                })
                .setNegativeButton(getString(R.string.dialog_button_cancel), null)
                .show();
    }

    public void onDeleteClicked(View view) {
        if (!isBookSelectedWithToast())
            return;

        Log.d(TAG, "onDeleteClicked: CheckedItemPosition:" + rowsListView.getCheckedItemPosition());
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_delete)
                .setMessage(R.string.dialog_message_delete_row)
                .setPositiveButton(getString(R.string.dialog_button_delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int position = rowsListView.getCheckedItemPosition() + 1;
                        Book book = MyFileHandler.getBook(BasicStaticData.appBookDataFile, "/" + bookName);
                        if (book == null) {
                            book = new Book();
                            book.setMaxTimes(5);
                        }
                        try {
                            HSSFWorkbook wb = new BookHandler(getApplicationContext()).openAndValidateBook(bookFile, book.getMaxTimes());
                            wb = BookHandler.removeLineFromWorkbook(wb, position);
                            BookHandler.closeAndSaveBook(wb, bookFile);
                            Toast.makeText(EditActivity.this, R.string.toast_deleted, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        updateList();
                    }
                })
                .setNegativeButton(getString(R.string.dialog_button_cancel), null)
                .show();
    }

    boolean isBookSelectedWithToast() {
        if (rowsListView.getCheckedItemPosition() == -1) {
            Toast.makeText(this, R.string.toast_need_choose_line, Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }
}
