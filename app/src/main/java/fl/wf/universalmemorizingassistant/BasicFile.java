package fl.wf.universalmemorizingassistant;

import android.util.Log;

import java.io.File;
import java.io.IOException;


/**
 * Created by WF on 2017/5/4.
 * Used to read and write user data
 */

public class BasicFile {
// TODO: 2017/5/4 Check if the permission is really needed to manipulate these files.The docs said this is only needed in low SDK versions.
    // TODO: 2017/5/4  so may need to add this       android:maxSdkVersion="18"      to the permission in manifest

    private static final String TAG = "FLWFBasicFile";

    private static final int CREATE_FAILED = 0;
    private static final int CREATE_SUCCESS = 1;
    private static final int CREATE_ALREADY_EXISTS = 2;

    //create a file
    public static int createNewFile(File file) {

        if (!file.exists()) {
            boolean userDataFileCreateResult = false;
            try {
                userDataFileCreateResult = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (userDataFileCreateResult) {
                Log.d(TAG, "onCreate: UserDataFileCreated");
                return CREATE_SUCCESS;
            } else {
                Log.d(TAG, "onCreate: UserDataFileNOOOOOOOOOOOOOTCreated");
                return CREATE_FAILED;
            }
        } else {
            Log.d(TAG, "onCreate: UserDataFileAlreadyExists");
            return CREATE_ALREADY_EXISTS;
        }
    }
}
