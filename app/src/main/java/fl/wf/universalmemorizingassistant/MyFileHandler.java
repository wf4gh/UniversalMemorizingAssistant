package fl.wf.universalmemorizingassistant;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * Created by WF on 2017/5/4.
 * Used to read and write user data
 */

class MyFileHandler {

    private static final String TAG = "FLWFBasicFile";

    static final int CREATE_FAILED = 0;
    static final int CREATE_SUCCESS = 1;
    static final int CREATE_ALREADY_EXISTS = 2;

    static final int UPDATE_NO_CHANGE = -1;

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

//    //This does work.But i can't see this using PC,only through the phone client can i see the file already be written
//    static void writeStringToFile(File fileToWrite, String contentToWrite) {
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(fileToWrite);
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
//            outputStreamWriter.write(contentToWrite);
//            outputStreamWriter.flush();
//            outputStreamWriter.close();
//            fileOutputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    static void appendStringToFile(File fileToWrite, String contentToWrite) {
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(fileToWrite, true);
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
//            outputStreamWriter.write(contentToWrite);
//            outputStreamWriter.flush();
//            outputStreamWriter.close();
//            fileOutputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
////        These may also work ,but seems on way to set the encoding
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
//
//    }

//    static String readStringFromFile(File fileToRead) {
//        try {
//            FileInputStream fileInputStream = new FileInputStream(fileToRead);
//            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
//            char[] input = new char[fileInputStream.available()];
//            inputStreamReader.read(input);
//            inputStreamReader.close();
//            fileInputStream.close();
//            return new String(input);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    //i like this.an universal extension file filter!
    static class ExtensionFilter implements FilenameFilter {
        private String extension;

        ExtensionFilter(String extension) {
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

    static Intent getExcelFileIntent(File file, Context context) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }


    static ArrayList<Book> readFromBookDataFile(File file) {
        ArrayList<Book> bookListFromFile = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            InputSource inputSource = new InputSource(inputStream);
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            BookSaxHandler bookSaxHandler = new BookSaxHandler();
            xmlReader.setContentHandler(bookSaxHandler);
            xmlReader.parse(inputSource);

            bookListFromFile = bookSaxHandler.bookArrayList;

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }


        if (bookListFromFile != null)
            for (Book b : bookListFromFile) {
                Log.d(TAG, "readFromBookDataFile: BookRead:" + b.toString());
            }
        else
            Log.d(TAG, "readFromBookDataFile: bookListNull!!!!!!!!!!!!");

        return bookListFromFile;
    }

    static ArrayList<Book> addBookToList(ArrayList<Book> bookList, String bookName, int maxTimes) {
        Book bookToAdd = new Book();
        bookToAdd.setName(bookName);
        bookToAdd.setMaxTimes(maxTimes);
        bookList.add(bookToAdd);
        return bookList;
    }

    static ArrayList<Book> deleteBookFromList(ArrayList<Book> bookList, String bookName) {
        for (Book b : bookList)
            if (b.getName().equals(bookName)) {
                bookList.remove(b);
                return bookList;
            }
        return bookList;
    }

    static ArrayList<Book> updateBookFromList(ArrayList<Book> bookList, String bookNameToUpdate, String newName, int newMaxTimes, boolean resetProgress) {
        if (bookList == null)
            return null;

        for (Book b : bookList)
            if (b.getName().equals(bookNameToUpdate)) {
                if (newName != null) b.setName(newName);
                if (newMaxTimes != UPDATE_NO_CHANGE) b.setMaxTimes(newMaxTimes);
                if (resetProgress) {
                    b.setIndex(1);
                    b.setRecitedTimes(0);
                }
            }
        return bookList;
    }

    static ArrayList<Book> updateBookFromList(ArrayList<Book> bookList, String bookNameToUpdate, int index, boolean increaseRecitedTimes) {
        if (bookList == null)
            return null;

        for (Book b : bookList)
            if (b.getName().equals(bookNameToUpdate)) {
                if (index != UPDATE_NO_CHANGE) b.setIndex(index);
                if (increaseRecitedTimes) b.setRecitedTimes(b.getRecitedTimes() + 1);
            }
        return bookList;
    }

    static Book getBook(File bookDataFile, String bookName) {
        ArrayList<Book> books = readFromBookDataFile(bookDataFile);
        for (Book b : books) {
            if (b.getName().equals(bookName))
                return b;
        }
        return null;
    }

    static void writeToBookDataFile(ArrayList<Book> bookListToWrite, File userDataFileToWrite) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(userDataFileToWrite);
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlSerializer xmlSerializer = xmlPullParserFactory.newSerializer();
            xmlSerializer.setOutput(fileOutputStream, "UTF-8");
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag(null, "Books");
            for (Book book : bookListToWrite) {
                xmlSerializer.startTag(null, "Book");
//                xmlSerializer.attribute(null, "id", String.valueOf(book.getId()));
                xmlSerializer.startTag(null, "Name");
                xmlSerializer.text(book.getName());
                xmlSerializer.endTag(null, "Name");
                xmlSerializer.startTag(null, "MaxTimes");
                xmlSerializer.text(String.valueOf(book.getMaxTimes()));
                xmlSerializer.endTag(null, "MaxTimes");
                xmlSerializer.startTag(null, "Index");
                xmlSerializer.text(String.valueOf(book.getIndex()));
                xmlSerializer.endTag(null, "Index");
                xmlSerializer.startTag(null, "RecitedTimes");
                xmlSerializer.text(String.valueOf(book.getRecitedTimes()));
                xmlSerializer.endTag(null, "RecitedTimes");
                xmlSerializer.endTag(null, "Book");
            }
            xmlSerializer.endTag(null, "Books");
            xmlSerializer.endDocument();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    static String getFileNameNoEx(String fileName) {
        if ((fileName != null) && (fileName.length() > 0)) {
            int dot = fileName.lastIndexOf('.');
            if ((dot > -1) && (dot < (fileName.length()))) {
                return fileName.substring(0, dot);
            }
        }
        return fileName;
    }
}