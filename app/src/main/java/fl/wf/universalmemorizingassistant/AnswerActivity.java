package fl.wf.universalmemorizingassistant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AnswerActivity extends AppCompatActivity {

    boolean answerShowed = false;

    Button buttonYes;
    Button buttonNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        buttonYes = (Button) findViewById(R.id.bt_ans_yes);
        buttonNo = (Button) findViewById(R.id.bt_ans_no);

        // TODO: 2017/5/15 find a chart api,add it
        // TODO: 2017/5/15 add a "too easy" button
    }

    public void onYesClicked(View view) {
        buttonYes.setVisibility(View.INVISIBLE);
        answerShowed=false;
        // TODO: 2017/5/15 logic need to add here
    }

    public void onNoClicked(View view) {
        if(!answerShowed){
            buttonYes.setVisibility(View.VISIBLE);
        }else{

        }
    }
}
