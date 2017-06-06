package fl.wf.universalmemorizingassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);

                //launch help activity when first run
                if (getSharedPreferences("isFirstRun", MODE_PRIVATE).getBoolean("isFirstRun", true)) {
                    SharedPreferences firstRunChecker = getSharedPreferences("isFirstRun", MODE_PRIVATE);
                    SharedPreferences.Editor editor = firstRunChecker.edit();
                    editor.putBoolean("isFirstRun", false);
                    editor.apply();

                    Intent helpIntent = new Intent(SplashActivity.this, HelpActivity.class);
                    startActivity(helpIntent);
                }
                finish();
            }
        }, 1000);
    }
}
