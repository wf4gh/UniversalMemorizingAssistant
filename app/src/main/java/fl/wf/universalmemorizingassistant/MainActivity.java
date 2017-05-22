package fl.wf.universalmemorizingassistant;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static android.os.Environment.getExternalStorageDirectory;
import static fl.wf.universalmemorizingassistant.BasicFile.createNewFile;
import static fl.wf.universalmemorizingassistant.BasicFile.createNewFolder;

public class MainActivity extends AppCompatActivity {
    // TODO: 2017/5/22   use sharedPreferences to save presentBook
    private static final String TAG = "FLWFMainActivity";

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4289;

    private String appFolder = BasicStaticData.appFolder;
    private String appBookDataFileName = BasicStaticData.appBookDataFileName;
    File appBookDataFile;
    File[] bookFiles;

    private ArrayList<Book> bookListFromScanner;
    private ArrayList<Book> bookListFromFile;
    private ArrayList<Book> bookListToWrite;

    //identify the book using now
//    int idIndex = 0;
    String presentBookName = "";
    //send these four variables to AnswerActivity using intent
    String bookName;
    int bookMaxTimes;
    int bookIndex;
    int bookRecitedTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);

        //Check if the storage is writable
        if (!DataChecker.isExternalStorageWritable()) {
            Toast.makeText(this, "ExternalStorageUnavailable", Toast.LENGTH_LONG).show();
            finish();
        }
        getRuntimePermission();
        initializingUserData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    void writeNewBookDataFile(ArrayList<Book> bookListToWrite, File userDataFileToWrite) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(userDataFileToWrite);
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlSerializer xmlSerializer = xmlPullParserFactory.newSerializer();
            xmlSerializer.setOutput(fileOutputStream, "UTF-8");
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag(null, "Books");
            xmlSerializer.attribute(null, "presentBookName", presentBookName);
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

    void changePresentBook(File userDataFileToWrite,String presentBookName) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(userDataFileToWrite);
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlSerializer xmlSerializer = xmlPullParserFactory.newSerializer();
            xmlSerializer.setOutput(fileOutputStream, "UTF-8");
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag(null, "Books");
            xmlSerializer.attribute(null, "presentBookName", presentBookName);
            xmlSerializer.endTag(null, "Books");
            xmlSerializer.endDocument();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    ArrayList<Book> readFromBookDataFile(File file) {
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
            presentBookName = BookSaxHandler.presentBookName;

        } catch (ParserConfigurationException | SAXException | IOException e) {
            Log.d(TAG, "readFromBookDataFile: Exception");
            e.printStackTrace();
        }

        //This clip of code is just used to show bookInfo to the log
        String result = "";
        for (Book b : bookListFromFile) {
            result += b.toString();
        }
        Log.d(TAG, "onCreate: " + result);
        return bookListFromFile;
    }

    //try to create user data folder and file.If already created,this will not create new file
    void initializingUserData() {
        //create app folder
        File appFolderFile = new File(getExternalStorageDirectory() + appFolder);
        createNewFolder(appFolderFile);


        //create or update app book data file
        File bookDataFile = new File(getExternalStorageDirectory() + appFolder + appBookDataFileName);
        int bookDataFileCreateState = createNewFile(bookDataFile);

        // TODO: 2017/5/18 add an example .xls file here

        bookListFromScanner = new ArrayList<>();
        BasicFile.ExtensionFilter xlsFilter = new BasicFile.ExtensionFilter(".xls");
        bookFiles = appFolderFile.listFiles(xlsFilter);

        if (bookDataFileCreateState == BasicFile.CREATE_SUCCESS) {
            //Create: when the book data file is firstly created, do this
            if (bookFiles != null) {
                for (int i = 0; i < bookFiles.length; i++) {
                    Book book = new Book();
                    // TODO: 2017/5/18   try using name as id ,cus name naturally diffs from each other
//                    book.setId(i + 1);
                    book.setName("/" + bookFiles[i].getName());
                    book.setIndex(1);
                    book.setMaxTimes(5);
                    book.setRecitedTimes(0);
                    bookListFromScanner.add(book);
                }
            } else Log.d(TAG, "onCreate: NULL bookFiles");
            writeNewBookDataFile(bookListFromScanner, bookDataFile);
        } else if (bookDataFileCreateState == BasicFile.CREATE_ALREADY_EXISTS) {
            //Update: when book data file already exists, do this
            Log.d(TAG, "initializingUserData: 1");
            bookListToWrite = new ArrayList<>();
            if (bookFiles != null) {
                for (int i = 0; i < bookFiles.length; i++) {
                    Book book = new Book();
//                    book.setId(i + 1);
                    book.setName("/" + bookFiles[i].getName());
                    book.setIndex(1);
                    book.setMaxTimes(5);
                    book.setRecitedTimes(0);
                    bookListFromScanner.add(book);
                }
            } else Log.d(TAG, "onCreate: NULL bookFiles");
            // TODO: 2017/5/18 get a bookListFromDataFile and compare, process
            bookListFromFile = new ArrayList<>();
            Log.d(TAG, "initializingUserData: 2");
            bookListFromFile = readFromBookDataFile(bookDataFile);
            Log.d(TAG, "initializingUserData: 3");
            for (Book book : bookListFromScanner) {
                book.tag = true;
                for (Book savedBook : bookListFromFile) {
                    if (book.getName().equals(savedBook.getName())) {
                        book.tag = false;
                        bookListToWrite.add(savedBook);
                    }
                }
                if (book.tag)
                    bookListToWrite.add(book);
            }
            writeNewBookDataFile(bookListToWrite, bookDataFile);
        }
//        changePresentBook(bookDataFile,"testName");
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
                    initializingUserData();
                } else {
                    Toast.makeText(this, "Need Permission", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    public void onStartClicked(View view) {
        File presentBookFile = new File(getExternalStorageDirectory() + appFolder + presentBookName);
        if (presentBookName.equals("") | !presentBookFile.exists()) {
            // TODO: 2017/5/19   ask the user to choose a book as current book
            // TODO: 2017/5/19   process the condition when the presentBookName-file is not existing (when File=...==null)
            new AlertDialog.Builder(this)
                    .setTitle("TitleHere")
                    .setMessage("book not chosen or present book invalid.Choose a book")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            return;
        }
        Intent intent = new Intent(this, AnswerActivity.class);
        intent.putExtra("bookName", bookName);
        intent.putExtra("bookMaxTimes", bookMaxTimes);
        intent.putExtra("bookIndex", bookIndex);
        intent.putExtra("bookRecitedTimes", bookRecitedTimes);
        startActivity(intent);
    }

    public void onSettingsClicked(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        String[] fileNames=BasicFile.filesToStrings(bookFiles);
        intent.putExtra("bookNames",fileNames);
        startActivity(intent);
    }
}