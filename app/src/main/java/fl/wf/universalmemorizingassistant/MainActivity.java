package fl.wf.universalmemorizingassistant;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WFMainActivity";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4289;
    private String dirPath;
    private String myDoc = "/UniversalMemorizingAssistant";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!DataChecker.isExternalStorageWritable()) {
            Toast.makeText(this, "ExternalStorageUnavailable", Toast.LENGTH_SHORT).show();
            finish();
        }
        getRuntimePermission();
        Log.d(TAG, "onCreate: 1");

        dirPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath() + myDoc;
        File file = new File(dirPath);// TODO: 2017/5/4 This seems not working
        Log.d(TAG, "onCreate: " + dirPath);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void getRuntimePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(this)
                        .setTitle("TitleHere")
                        .setMessage("Need Permission")
                        .setPositiveButton("OK", null)
                        .show();

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO: 2017/5/4 when permitted,add some check here
                } else {
                    Toast.makeText(this, "Need Permission", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }
}