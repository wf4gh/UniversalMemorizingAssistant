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

    private static final String TAG = "WFMainActivity";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4289;
//    private static final int MY_PERMISSIONS_REQUEST_MOUNT_UNMOUNT_FILESYSTEMS = 4290;
    private String myDocPath = "/storage/emulated/0/Android/data/fl.wf.universalmemorizingassistant/files/MyTestDocPath";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!DataChecker.isExternalStorageWritable()) {
            Toast.makeText(this, "ExternalStorageUnavailable", Toast.LENGTH_LONG).show();
            finish();
        }
        getRuntimePermission();
// FIXME: 2017/5/5 This create a new folder function may not run at the first time the user grant his app the permission.
        // FIXME: 2017/5/5 fix this by moving this create function to the "when permitted" part below
        File file = new File(myDocPath);
        boolean createResult;
        Log.d(TAG, "onCreate: myDocPath: " + myDocPath);
        Log.d(TAG, "onCreate: AbsolutePath: " + file.getAbsolutePath());
        Log.d(TAG, "onCreate: ParentPath: " + file.getParent());

        createResult = file.mkdirs();

        if (createResult) Log.d(TAG, "onCreate: created");
        else Log.d(TAG, "onCreate: notCreated");

        if (!file.exists()) Log.d(TAG, "onCreate: notExist");
        else Log.d(TAG, "onCreate: exist");
        testPart();
    }

    void testPart() {

        File testFile = new File("/Shanbay");
        if (testFile.exists()) Log.d(TAG, "onCreate: SHANEEEEEEEEEEEEE");
        else Log.d(TAG, "onCreate: NEEEEEEEEEEEEEEe");

        File testFile2 = new File("/kgmusic.ver");
        if (testFile2.exists()) Log.d(TAG, "onCreate: SmmmmmmmmmmmmmEEEEEEEEEEE");
        else Log.d(TAG, "onCreate: NmmmmmmmmmmmmEEEEEEEEEEEEe");

        File testFile3 = this.getExternalFilesDir(null);
        Log.d(TAG, "testPart11: " + testFile3);
        File testFile33 = this.getFilesDir();
        Log.d(TAG, "testPart22: " + testFile3);

        File testFile4 = getExternalCacheDir();
        Log.d(TAG, "testPart:fffffffff " + testFile3);

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
        } else Log.i(TAG, "getRuntimePermission: Permission GET");
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)) {
//
//                new AlertDialog.Builder(this)
//                        .setTitle("TitleHere2")
//                        .setMessage("Need Permission2")
//                        .setPositiveButton("OK2", null)
//                        .show();
//
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},
//                        MY_PERMISSIONS_REQUEST_MOUNT_UNMOUNT_FILESYSTEMS);
//            } else {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},
//                        MY_PERMISSIONS_REQUEST_MOUNT_UNMOUNT_FILESYSTEMS);
//            }
//        } else Log.d(TAG, "getRuntimePermission: Has PERMISSION!!!!!!!!!!!");
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
//            case MY_PERMISSIONS_REQUEST_MOUNT_UNMOUNT_FILESYSTEMS: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                } else {
//                    Toast.makeText(this, "Need Permission2", Toast.LENGTH_LONG).show();
//                    finish();
//                }
//            }
        }
    }
}