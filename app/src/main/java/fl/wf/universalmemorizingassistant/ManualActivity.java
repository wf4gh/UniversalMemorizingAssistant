package fl.wf.universalmemorizingassistant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class ManualActivity extends AppCompatActivity {

    private static final String TAG = "FLWFHelpActivity";

    String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_manual);
        if(toolbar ==null){
            Log.d(TAG, "onCreate: NULL");
        }else Log.d(TAG, "onCreate: NOT NULL");

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Log.d(TAG, "onCreate: SET TRUE");
        }else Log.d(TAG, "onCreate: NOT Set");

        language = Locale.getDefault().getLanguage();

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

    public void onGenerateExampleFileClicked(View view) {
        File exampleFile = new File(BasicStaticData.absAppFolderPath + "/" + getString(R.string.name_example));
        int createResult = MyFileHandler.createNewFile(exampleFile);
        switch (createResult) {
            case MyFileHandler.CREATE_ALREADY_EXISTS:
                Toast.makeText(this, getString(R.string.toast_example_exists), Toast.LENGTH_SHORT).show();
                break;
            case MyFileHandler.CREATE_FAILED:
                Toast.makeText(this, getString(R.string.toast_create_failed), Toast.LENGTH_SHORT).show();
                break;
            case MyFileHandler.CREATE_SUCCESS:
                HSSFWorkbook wb;
                try {
                    BookHandler bookHandler = new BookHandler(getApplicationContext());
                    wb = bookHandler.createWorkbookWithTitle();
                    wb = bookHandler.addNewLineToWorkbook(wb, getString(R.string.sheet_hint_word), getString(R.string.sheet_ans_word), false);
                    wb = bookHandler.addNewLineToWorkbook(wb, getString(R.string.sheet_hint_sentence), getString(R.string.sheet_ans_sentence), false);
                    if (language.equals("zh"))
                        wb = bookHandler.addNewLineToWorkbook(wb, "normal map", "法线贴图", false);
                    wb = bookHandler.addNewLineToWorkbook(wb, getString(R.string.sheet_hint_question), getString(R.string.sheet_hint_answer), false);
                    // TODO: 2017/6/6  a poem from tagore?
                    //wb = bookHandler.addNewLineToWorkbook(wb, "", "", false);

                    BookHandler.closeAndSaveBook(wb, exampleFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(this, R.string.toast_example_generated, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
