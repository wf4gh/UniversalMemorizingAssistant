package fl.wf.universalmemorizingassistant;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.os.Environment.getExternalStorageDirectory;

public class AnswerActivity extends AppCompatActivity {
    private static final String TAG = "FLWFAnswerActivity";

    String appFolderPath = BasicStaticData.appFolder;

    boolean answerShowed = false;
    int answerState = 0;

    String bookName;
    int bookMaxTimes;
    int bookIndex;
    int bookRecitedTimes;
    File bookFile;
    HSSFWorkbook wb;

    Button yesButton;
    Button noButton;
    TextView hintTextView;
    TextView answerTextView;
    EditText answerEditText;
    Intent settingsActivityIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        settingsActivityIntent = new Intent(this, SettingsActivity.class);


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
        bookName = "/" + getPresentBook();
        Book presentBook = MyFileHandler.getBook(BasicStaticData.appBookDataFile, bookName);
        if (presentBook != null) {
            bookMaxTimes = presentBook.getMaxTimes();
            bookIndex = presentBook.getIndex();
            bookRecitedTimes = presentBook.getRecitedTimes();
        } else {
            finish();
        }
//        Log.d(TAG, "onCreate: \nBook: " + appFolderPath + bookName + "\nTimes: "
//                + bookMaxTimes + "\nIndex: " + bookIndex + "\nRecitedTimes: " + bookRecitedTimes);
        bookFile = new File(BasicStaticData.absAppFolderPath + bookName);

        try {
            wb = BookHandler.openAndValidateBook(bookFile, bookMaxTimes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (wb == null || wb.getSheetAt(0) == null || wb.getSheetAt(0).getLastRowNum() < 1) {
            new AlertDialog.Builder(this)
                    .setTitle("TitleHere")
                    .setMessage("This book is empty or Invalid,add rows or choose another!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(settingsActivityIntent);
                            finish();
                        }
                    })
                    .show();
        } else {
            showCurrentRowValue();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wb != null) {
            try {
                BookHandler.closeAndSaveBook(wb, bookFile);
                //update user book data file here
                ArrayList<Book> bookArrayList = MyFileHandler.readFromBookDataFile(BasicStaticData.appBookDataFile);
                bookArrayList = MyFileHandler.updateBookFromList(bookArrayList, bookName, bookIndex, false);
                MyFileHandler.writeToBookDataFile(bookArrayList, BasicStaticData.appBookDataFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //used to show the row now at the screen
    void showCurrentRowValue() {

        while (BookHandler.rowCheck(wb, bookIndex) == BookHandler.ROW_INVALID) {
            bookIndex++;
        }
        //this does look redundant...
        if (BookHandler.rowCheck(wb, bookIndex) == BookHandler.ROW_END) {
            bookIndex = 1;
            while (BookHandler.rowCheck(wb, bookIndex) == BookHandler.ROW_INVALID) {
                bookIndex++;
                if (BookHandler.rowCheck(wb, bookIndex) == BookHandler.ROW_END) {
                    new AlertDialog.Builder(this)
                            .setTitle("ThisFinished")
                            .setMessage("One more time?")
                            .setPositiveButton("OK,one more time", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //update user book data file here
                                    ArrayList<Book> bookArrayList = MyFileHandler.readFromBookDataFile(BasicStaticData.appBookDataFile);
                                    bookArrayList = MyFileHandler.updateBookFromList(bookArrayList, bookName, 1, true);
                                    MyFileHandler.writeToBookDataFile(bookArrayList, BasicStaticData.appBookDataFile);

                                    BookHandler.setAllRowsToMaxTimes(wb, bookMaxTimes);
                                    try {
                                        BookHandler.closeAndSaveBook(wb, bookFile);
                                        wb = BookHandler.openAndValidateBook(bookFile, bookMaxTimes);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    bookIndex = 1;
                                    showCurrentRowValue();
                                }
                            })
                            .setNegativeButton("Back to start", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                    return;
                }
            }
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
        answerState = BookHandler.ANSWER_RIGHT;
        BookHandler.updateTimes(wb, bookIndex, bookMaxTimes, answerState);
        UIShowNext();
        bookIndex++;
        showCurrentRowValue();
    }

    public void onNoClicked(View view) {
        if (!answerShowed) {
            UIShowThis();
        } else {
            answerState = BookHandler.ANSWER_WRONG;
            BookHandler.updateTimes(wb, bookIndex, bookMaxTimes, answerState);
            UIShowNext();
            bookIndex++;
            showCurrentRowValue();
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

    String getPresentBook() {
        SharedPreferences presentBook = getSharedPreferences("presentBook", MODE_PRIVATE);
        return presentBook.getString("presentBook", "choose one!");
    }
}
