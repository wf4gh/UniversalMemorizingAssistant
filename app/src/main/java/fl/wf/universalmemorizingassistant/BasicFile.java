package fl.wf.universalmemorizingassistant;

import android.util.Log;

import java.io.File;
import java.io.IOException;


/**
 * Created by WF on 2017/5/4.
 * Used to read and write user data
 */

class BasicFile {

    private static final String TAG = "FLWFBasicFile";

    private static final int CREATE_FAILED = 0;
    private static final int CREATE_SUCCESS = 1;
    private static final int CREATE_ALREADY_EXISTS = 2;

    //create a file
    static int createNewFile(File file) {

        if (!file.exists()) {
            boolean createNewFileResult = false;
            try {
                createNewFileResult = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (createNewFileResult) {
                Log.d(TAG, "createNewFile: " + file.getName() + " CREATE_SUCCESS");
                return CREATE_SUCCESS;
            } else {
                Log.w(TAG, "createNewFile: " + file.getName() + " CREATE_FAILED");
                return CREATE_FAILED;
            }
        } else {
            Log.d(TAG, "createNewFile: " + file.getName() + " CREATE_ALREADY_EXISTS");
            return CREATE_ALREADY_EXISTS;
        }
    }

    //create a new folder
    static int createNewFolder(File file) {
        if (!file.exists()) {
            boolean folderCreateResult = file.mkdirs();
            if (folderCreateResult) {
                Log.d(TAG, "createNewFolder: " + file.getName() + " CREATE_SUCCESS");
                return CREATE_SUCCESS;
            } else {
                Log.w(TAG, "createNewFolder: " + file.getName() + " CREATE_FAILED");
                return CREATE_FAILED;
            }
        } else {
            Log.d(TAG, "createNewFolder: " + file.getName() + " CREATE_ALREADY_EXISTS");
            return CREATE_ALREADY_EXISTS;
        }
    }
}