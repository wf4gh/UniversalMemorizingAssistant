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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.IOException;

import static android.os.Environment.getExternalStorageDirectory;

public class AnswerActivity extends AppCompatActivity {
    private static final String TAG = "FLWFAnswerActivity";

    String appFolderPath = BasicStaticData.appFolderPath;

    boolean answerShowed = false;

    Button yesButton;
    Button noButton;
    TextView hintTextView;
    TextView answerTextView;
    EditText answerEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        String bookName = getIntent().getStringExtra("bookName");
        // TODO: 2017/5/16 get bookTimes from the intent
        Log.d(TAG, "onCreate: " + appFolderPath + bookName);
        File bookFile = new File(getExternalStorageDirectory() + appFolderPath + bookName);

        // TODO: 2017/5/16 this should be moved to the bottom of this method
        try {
            HSSFWorkbook wb = BookAccessor.openAndValidateBook(bookFile, 5);
            // TODO: 2017/5/12 add a lot of things here
            BookAccessor.closeBookAndSave(wb, bookFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onYesClicked(View view) {
        showNext();
        // TODO: 2017/5/15 logic need to add here
    }

    public void onNoClicked(View view) {
        if (!answerShowed) {
            showThis();
        } else {
            showNext();
            // TODO: 2017/5/16 logic add here
        }
    }


    //These two methods used to control the show and hide of the components depending on the current state
    void showThis() {
        yesButton.setVisibility(View.VISIBLE);
        noButton.setText("NO");
        answerTextView.setVisibility(View.VISIBLE);
        answerShowed = true;
        answerEditText.setEnabled(false);
    }

    void showNext() {
        yesButton.setVisibility(View.INVISIBLE);
        noButton.setText("TOSEE");
        answerTextView.setVisibility(View.INVISIBLE);
        answerShowed = false;
        answerEditText.setText("");
        answerEditText.setEnabled(true);
    }
}
