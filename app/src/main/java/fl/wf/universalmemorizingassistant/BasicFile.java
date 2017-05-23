package fl.wf.universalmemorizingassistant;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


/**
 * Created by WF on 2017/5/4.
 * Used to read and write user data
 */

class BasicFile {

    private static final String TAG = "FLWFBasicFile";

    static final int CREATE_FAILED = 0;
    static final int CREATE_SUCCESS = 1;
    static final int CREATE_ALREADY_EXISTS = 2;

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
                Log.d(TAG, "createNewFile: " + file.getName() + " CREATE_SUCCEEDED");
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
                Log.d(TAG, "createNewFolder: " + file.getName() + " CREATE_SUCCEEDED");
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

        //These may also work ,but seems on way to set the encoding
//          //Here is one way
//        try {
//            FileWriter fileWriter = new FileWriter(fileToWrite, true);
//            fileWriter.write(contentToWrite);
//            fileWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//          //Here is another way
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(fileToWrite, true);
//            fileOutputStream.write(contentToWrite.getBytes());
//            fileOutputStream.flush();
//            fileOutputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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

    //i like this.an universal extension file filter!
    public static class ExtensionFilter implements FilenameFilter {
        private String extension;

        public ExtensionFilter(String extension) {
            this.extension = extension;
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(extension);
        }
    }

    static String[] filesToStrings(File[] files) {
        String[] fileNames = null;
        if (files != null) {
            fileNames = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                fileNames[i] = files[i].getName();
                Log.d(TAG, "filesToStrings: " + fileNames[i]);
            }
        }
        return fileNames;
    }

    public static Intent getExcelFileIntent(File file, Context context) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }
}