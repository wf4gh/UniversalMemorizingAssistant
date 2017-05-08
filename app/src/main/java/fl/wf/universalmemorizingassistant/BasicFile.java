package fl.wf.universalmemorizingassistant;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;


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

    //This does work.But i can't see this using PC,only through the phone client can i see the file already be written
    static void writeStringToFile(File fileToWrite, String contentToWrite) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileToWrite);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
            outputStreamWriter.write(contentToWrite);
            outputStreamWriter.flush();
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void appendStringToFile(File fileToWrite, String contentToWrite) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileToWrite, true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
            outputStreamWriter.write(contentToWrite);
            outputStreamWriter.flush();
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String readStringFromFile(File fileToRead) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileToRead);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            char[] input = new char[fileInputStream.available()];
            inputStreamReader.read(input);
            inputStreamReader.close();
            fileInputStream.close();
            return new String(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}