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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FLWFMainActivity";

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4289;

    private String myDocPath = "/MyTestDocPath";
    private String userDataFilePath = "/UserData.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!DataChecker.isExternalStorageWritable()) {
            Toast.makeText(this, "ExternalStorageUnavailable", Toast.LENGTH_LONG).show();
            finish();
        }
        getRuntimePermission();

        // 2017/5/5 This create a new folder function may not run at the first time the user grant his app the permission.
        // 2017/5/5 fix this by moving this create function to the "when permitted" part below
        // 2017/5/5 OK,the problem seems to be that even if i have not get the permission,i can still create this folder and file  :|
        // 2017/5/5 The reason may be the minSDK!!!!!!!!!!!!!
        // 2017/5/4 Check if the permission is really needed to manipulate these files.The docs said this is only needed in low SDK versions.
        // 2017/5/4  so may need to add this       android:maxSdkVersion="18"      to the permission in manifest

//         2017/5/5 OK,here may be the result:
//        Only after API 23 i need to request permission at runtime.
//        Only before API 18 i need to request the permission.
//        So i actually don't need so much lines of code at all!
//        --------------------------At least this can be reusable--------------------------

        Log.d(TAG, "onCreate: SSSSSSSSSSSSSSS" + Environment.getExternalStorageDirectory());
        Log.d(TAG, "onCreate: SSSSSSSSSSSSS222222" + getExternalFilesDir(null));
        File anotherTest = new File(Environment.getExternalStorageDirectory() + myDocPath);
        BasicFile.createNewFolder(anotherTest);

        tryToCreateUserDataFile();
    }

    //try to create user data file.If already created,this will not create new file
    void tryToCreateUserDataFile() {
        File testFolder = new File(getExternalFilesDir(null) + myDocPath);
        BasicFile.createNewFolder(testFolder);

        File userDataFile = new File(getExternalFilesDir(null) + userDataFilePath);
        BasicFile.createNewFile(userDataFile);
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
        } else Log.d(TAG, "getRuntimePermission: Permission Already GET");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tryToCreateUserDataFile();
                    File anotherTest = new File(Environment.getExternalStorageDirectory() + myDocPath);
                    BasicFile.createNewFolder(anotherTest);
                } else {
                    Toast.makeText(this, "Need Permission", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }
}