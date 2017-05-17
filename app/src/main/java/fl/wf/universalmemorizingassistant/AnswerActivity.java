package fl.wf.universalmemorizingassistant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.IOException;

import static android.os.Environment.getExternalStorageDirectory;

public class AnswerActivity extends AppCompatActivity {
    private static final String TAG = "FLWFAnswerActivity";

    String appFolderPath = BasicStaticData.appFolderPath;

    boolean answerShowed = false;
    int answerState = 0;

    String bookName;
    int maxTimes;
    int bookIndex;
    File bookFile;
    HSSFWorkbook wb;

    Button yesButton;
    Button noButton;
    TextView hintTextView;
    TextView answerTextView;
    EditText answerEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_answer);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        yesButton = (Button) findViewById(R.id.bt_ans_yes);
        noButton = (Button) findViewById(R.id.bt_ans_no);
        hintTextView = (TextView) findViewById(R.id.tv_ans_hint);
        answerEditText = (EditText) findViewById(R.id.et_ans_ans);
        answerTextView = (TextView) findViewById(R.id.tv_ans_answer);

        // TODO: 2017/5/15 (IUV) find a chart api,add it
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: 2017/5/17 get these three values by reading user data file(.xml)
        bookName = "/UTest.xls";
        maxTimes = 5;
        bookIndex = 1;
        Log.d(TAG, "onCreate: \nbook:" + appFolderPath + bookName + "\nTimes: " + maxTimes + "\nIndex:" + bookIndex);
        bookFile = new File(getExternalStorageDirectory() + appFolderPath + bookName);

        try {
            wb = BookAccessor.openAndValidateBook(bookFile, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }

        showCurrentRowValue(wb);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            BookAccessor.closeAndSaveBook(wb, bookFile);
            // TODO: 2017/5/17   update user data file here
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //used to show the row now at the screen
    void showCurrentRowValue(HSSFWorkbook wb) {
        if (BookAccessor.rowCheck(wb, bookIndex) == BookAccessor.ROW_END) {
            bookIndex = 1;
            while (BookAccessor.rowCheck(wb, bookIndex) == BookAccessor.ROW_INVALID) {
                bookIndex++;
                if (BookAccessor.rowCheck(wb, bookIndex) == BookAccessor.ROW_END) {
                    // TODO: 2017/5/17   do a end this book method here
                    Toast.makeText(this, "EndNow!!!!!!", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            }
        }
        while (BookAccessor.rowCheck(wb, bookIndex) == BookAccessor.ROW_INVALID) {
            bookIndex++;
        }

        HSSFRow row = wb.getSheetAt(0).getRow(bookIndex);
        String hint = row.getCell(0).getStringCellValue();
        String ans = row.getCell(1).getStringCellValue();
        hintTextView.setText(hint);
        answerTextView.setText(ans);
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

    public void onYesClicked(View view) {
        answerState=BookAccessor.ANSWER_RIGHT;
        BookAccessor.updateTimes(wb, bookIndex, maxTimes, answerState);
        UIShowNext();
        bookIndex++;
        showCurrentRowValue(wb);
    }

    public void onNoClicked(View view) {
        if (!answerShowed) {
            UIShowThis();
        } else {
            answerState=BookAccessor.ANSWER_WRONG;
            BookAccessor.updateTimes(wb, bookIndex, maxTimes, answerState);
            UIShowNext();
            bookIndex++;
            showCurrentRowValue(wb);
        }
    }


    //These two methods used to control the show and hide of the components depending on the current state
    void UIShowThis() {
        yesButton.setVisibility(View.VISIBLE);
        noButton.setText("NO");
        answerTextView.setVisibility(View.VISIBLE);
        answerShowed = true;
        answerEditText.setEnabled(false);
    }

    void UIShowNext() {
        yesButton.setVisibility(View.INVISIBLE);
        noButton.setText("TOSEE");
        answerTextView.setVisibility(View.INVISIBLE);
        answerShowed = false;
        answerEditText.setText("");
        answerEditText.setEnabled(true);
    }
}
