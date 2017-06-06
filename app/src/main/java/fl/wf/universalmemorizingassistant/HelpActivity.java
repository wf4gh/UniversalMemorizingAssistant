package fl.wf.universalmemorizingassistant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static android.R.attr.name;
import static fl.wf.universalmemorizingassistant.MyFileHandler.createNewFile;

public class HelpActivity extends AppCompatActivity {

    private static final String TAG = "FLWFHelpActivity";

    String language;

    String forWords;
    String forSentenses;
    String forTechnicalTerminology;
    String forQueestion;
    String forReciting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        language = Locale.getDefault().getLanguage();

        if (!language.equals("zh")) {
            Log.d(TAG, "onCreate: ");
        }
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
                    wb = bookHandler.addNewLineToWorkbook(wb, "", "", false);
                    // TODO: 2017/6/6  add lines here
                    BookHandler.closeAndSaveBook(wb, exampleFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(this, R.string.toast_example_generated, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
