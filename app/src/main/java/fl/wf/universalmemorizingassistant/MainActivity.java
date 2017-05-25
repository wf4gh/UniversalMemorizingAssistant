package fl.wf.universalmemorizingassistant;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.io.File;
import java.util.ArrayList;

import static android.os.Environment.getExternalStorageDirectory;
import static fl.wf.universalmemorizingassistant.MyFileHandler.createNewFile;
import static fl.wf.universalmemorizingassistant.MyFileHandler.createNewFolder;
import static fl.wf.universalmemorizingassistant.MyFileHandler.readFromBookDataFile;
import static fl.wf.universalmemorizingassistant.MyFileHandler.writeToBookDataFile;

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


    Intent settingsActivityIntent;

    // TODO: 2017/5/23   add viewer page as help
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
        presentBookName = getPresentBook();
        settingsActivityIntent = new Intent(this, SettingsActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializingUserData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    //try to create user data folder and file.If already created,this will not create new file
    void initializingUserData() {
        //create app folder
        File appFolderFile = new File(getExternalStorageDirectory() + appFolder);
        createNewFolder(appFolderFile);


        //create or update app book data file
        File bookDataFile = new File(getExternalStorageDirectory() + appFolder + appBookDataFileName);
        int bookDataFileCreateState = createNewFile(bookDataFile);

        bookListFromScanner = new ArrayList<>();
        MyFileHandler.ExtensionFilter xlsFilter = new MyFileHandler.ExtensionFilter(".xls");
        bookFiles = appFolderFile.listFiles(xlsFilter);

        //make bookFiles not null;
        if (bookFiles == null) {
            // TODO: 2017/5/22 create an example xls file
            bookFiles = appFolderFile.listFiles(xlsFilter);
        }

        if (bookDataFileCreateState == MyFileHandler.CREATE_SUCCESS) {
            //Create: when the book data file is firstly created, do this
            if (bookFiles != null) {
                for (int i = 0; i < bookFiles.length; i++) {
                    Book book = new Book();
                    book.setName("/" + bookFiles[i].getName());
                    book.setIndex(1);
                    book.setMaxTimes(5);
                    book.setRecitedTimes(0);
                    bookListFromScanner.add(book);
                }
            } else Log.d(TAG, "onCreate: NULL bookFiles");
            writeToBookDataFile(bookListFromScanner, bookDataFile);
        } else if (bookDataFileCreateState == MyFileHandler.CREATE_ALREADY_EXISTS) {
            //Update: when book data file already exists, do this
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
            bookListFromFile = new ArrayList<>();
            bookListFromFile = readFromBookDataFile(bookDataFile);
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
            writeToBookDataFile(bookListToWrite, bookDataFile);
        }
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
        presentBookName = getPresentBook();
        Log.d(TAG, "onStartClicked: presentBookName: " + presentBookName);
        File presentBookFile = new File(getExternalStorageDirectory() + appFolder + "/" + presentBookName);
        if (presentBookName.equals("choose one!") | presentBookName.equals("") | !presentBookFile.exists()) {
            new AlertDialog.Builder(this)
                    .setTitle("TitleHere")
                    .setMessage("book not chosen or present book invalid.Choose a book")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(settingsActivityIntent);
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
        startActivity(settingsActivityIntent);
    }

    String getPresentBook() {
        SharedPreferences presentBook = getSharedPreferences("presentBook", MODE_PRIVATE);
        return presentBook.getString("presentBook", "choose one!");
    }
}