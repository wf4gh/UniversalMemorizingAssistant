package fl.wf.universalmemorizingassistant;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import static android.os.Environment.getExternalStorageDirectory;
import static fl.wf.universalmemorizingassistant.BasicFile.*;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FLWFMainActivity";

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4289;

    private String myDocPath = "/U_Memorizing";
    private String myUserDataFileName = "/UserBooksData.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check if the storage is writable
        if (!DataChecker.isExternalStorageWritable()) {
            Toast.makeText(this, "ExternalStorageUnavailable", Toast.LENGTH_LONG).show();
            finish();
        }
        getRuntimePermission();
        initializingUserData(myDocPath, myUserDataFileName);
        File userDataFile = new File(getExternalStorageDirectory() + myDocPath + myUserDataFileName);
//        writeStringToFile(userDataFile, "Here is another string to write.\n");
//        appendStringToFile(userDataFile, "Here is a test content to append");
        Log.d(TAG, "onCreate: Read from file:\n" + readStringFromFile(userDataFile));
    }

    //try to create user data folder and file.If already created,this will not create new file
    void initializingUserData(String appDataFolder, String userDataFileName) {
        File appDataFolderPath = new File(getExternalStorageDirectory() + appDataFolder);
        createNewFolder(appDataFolderPath);

        File userDataFile = new File(getExternalStorageDirectory() + appDataFolder + userDataFileName);
        createNewFile(userDataFile);
    }

    void getRuntimePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getRuntimePermission: Permission NOT GET");
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
        } else Log.d(TAG, "getRuntimePermission: Permission Already GET");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializingUserData(myDocPath, myUserDataFileName);
                } else {
                    Toast.makeText(this, "Need Permission", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }
}