package fl.wf.universalmemorizingassistant;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static fl.wf.universalmemorizingassistant.MyFileHandler.createNewFile;
import static fl.wf.universalmemorizingassistant.MyFileHandler.createNewFolder;
import static fl.wf.universalmemorizingassistant.MyFileHandler.readFromBookDataFile;
import static fl.wf.universalmemorizingassistant.MyFileHandler.writeToBookDataFile;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FLWFMainActivity";

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4289;

    File[] bookFiles;
    File presentBookFile;
    String presentBookName = "";
    Intent settingsActivityIntent;
    TextView infoTextView;

    // TODO: 2017/5/23   add viewer page as help
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);

        //Check if the storage is writable
        if (!MyFileHandler.isExternalStorageWritable()) {
            Toast.makeText(this, R.string.toast_storage_unavailable, Toast.LENGTH_LONG).show();
            finish();
        }
        getRuntimePermission();

        settingsActivityIntent = new Intent(this, SettingsActivity.class);
        infoTextView = (TextView) findViewById(R.id.tv_main_info);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initializingUserData();
        String textToShow = getTextToShow();
        infoTextView.setText(textToShow);
    }

    String getTextToShow() {
        String line1L = getString(R.string.main_info_line1_left);
        String line1R = getString(R.string.main_info_line1_right);
        String line2L = getString(R.string.main_info_line2_left);
        int length;
        if (bookFiles != null)
            length = bookFiles.length;
        else
            length = 0;
        String line1 = line1L + " " + length + " " + line1R;

        String line2 = line2L + " " + getPresentBook();

        return line1 + "\n\n" + line2;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                presentBookName = getPresentBook();
                presentBookFile = new File(BasicStaticData.absAppFolderPath + "/" + presentBookName);
                if (presentBookName.equals(getString(R.string.present_book_undefined)) | presentBookName.equals("") | !presentBookFile.exists()) {
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.dialog_title_no_present_book))
                            .setMessage(getString(R.string.dialog_message_no_present_book))
                            .setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(settingsActivityIntent);
                                }
                            })
                            .setNegativeButton(getString(R.string.dialog_button_cancel), null)
                            .show();
                    break;
                }
                @SuppressLint("InflateParams") final View quickAddView = getLayoutInflater().inflate(R.layout.dialog_quick_add, null);
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.dialog_title_quick_add))
                        .setView(quickAddView)
                        .setPositiveButton(getString(R.string.dialog_button_add), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText hintEditText = (EditText) quickAddView.findViewById(R.id.et_dialog_quick_add_hint);
                                String hint = hintEditText.getText().toString();
                                if (hint.equals("")) {
                                    Toast.makeText(MainActivity.this, getString(R.string.toast_hint_needed), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                EditText answerEditText = (EditText) quickAddView.findViewById(R.id.et_dialog_quick_add_answer);
                                String answer = answerEditText.getText().toString();
                                if (answer.equals(""))
                                    answer = getString(R.string.sheet_no_data);
//                                Log.d(TAG, "onClick: \nhint:" + hint + "\nans:" + answer);
                                Book book = MyFileHandler.getBook(BasicStaticData.appBookDataFile, "/" + presentBookName);

                                //think this is not likely to happen now...
                                if (book == null) {
                                    Log.e(TAG, "onClick: book Null!!!");
                                    book = new Book();
                                    book.setMaxTimes(5);
                                }

                                try {
                                    HSSFWorkbook wb = new BookHandler(getApplicationContext()).openAndValidateBook(presentBookFile, book.getMaxTimes());
                                    wb = new BookHandler(getApplicationContext()).addNewLineToWorkbook(wb, hint, answer, false);
                                    BookHandler.closeAndSaveBook(wb, presentBookFile);
                                    Toast.makeText(MainActivity.this, getString(R.string.toast_added), Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_button_cancel), null)
                        .show();
                break;

            case R.id.menu_manual:
                Intent intent = new Intent(this, ManualActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_about:
                String versionName = getString(R.string.text_version);
                try {
                    versionName = versionName + getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    versionName = versionName + getString(R.string.text_fetch_fail);
                }
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.menu_about))
                        .setMessage(getString(R.string.app_name) + "\n" + versionName + "\n")
//                        .setPositiveButton(getString(R.string.dialog_button_ok), null)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //try to create user data folder and file.If already created,this will not create new file

    void initializingUserData() {
        //create app folder
        File appFolderFile = BasicStaticData.appFolderFile;
        createNewFolder(appFolderFile);


        //create or update app book data file
        File bookDataFile = BasicStaticData.appBookDataFile;
        int bookDataFileCreateState = createNewFile(bookDataFile);

        ArrayList<Book> bookListFromScanner = new ArrayList<>();
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
                for (File bookFile : bookFiles) {
                    Book book = new Book();
                    book.setName("/" + bookFile.getName());
                    book.setIndex(1);
                    book.setMaxTimes(5);
                    book.setRecitedTimes(0);
                    bookListFromScanner.add(book);
                }
            } else Log.d(TAG, "onCreate: NULL bookFiles");
            writeToBookDataFile(bookListFromScanner, bookDataFile);
        } else if (bookDataFileCreateState == MyFileHandler.CREATE_ALREADY_EXISTS) {
            //Update: when book data file already exists, do this
            ArrayList<Book> bookListToWrite = new ArrayList<>();
            if (bookFiles != null) {
                for (File bookFile : bookFiles) {
                    Book book = new Book();
                    book.setName("/" + bookFile.getName());
                    book.setIndex(1);
                    book.setMaxTimes(5);
                    book.setRecitedTimes(0);
                    bookListFromScanner.add(book);
                }
            } else Log.d(TAG, "onCreate: NULL bookFiles");
            ArrayList<Book> bookListFromFile;
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

    /* TODO-suspend: 2017/5/26   can enter the app even the permission is not granted, in app explain why the permission is needed.
     ------Before request of permission accepted, the user can't actually use this app */

    //A failed test about getRuntimePermission
//    void getRuntimePermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            new AlertDialog.Builder(this)
//                    .setTitle("TitleHere")
//                    .setMessage("Need Permission")
//                    .setPositiveButton("Got it", null)
//                    .setCancelable(false)
//                    .show();
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
//        }
//    }

    void getRuntimePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getRuntimePermission: Permission NOT GET");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.dialog_title_permission))
                        .setMessage(getString(R.string.dialog_message_permission))
                        .setPositiveButton(getString(R.string.dialog_button_got_it), null)
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
                    Toast.makeText(this, getString(R.string.toast_need_permission), Toast.LENGTH_LONG).show();
                    finish();
//                    new AlertDialog.Builder(this)
//                            .setTitle("TitleHere")
//                            .setMessage("Need Permission")
//                            .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    finish();
//                                }
//                            })
//                            .setNegativeButton("Set", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    // : 2017/5/26 jump to setting here
//                                }
//                            })
//                            .show();
                }
            }
        }
    }

    public void onStartClicked(View view) {
        presentBookName = getPresentBook();
        presentBookFile = new File(BasicStaticData.absAppFolderPath + "/" + presentBookName);
        if (presentBookName.equals(getString(R.string.present_book_undefined)) | presentBookName.equals("") | !presentBookFile.exists()) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_title_no_present_book))
                    .setMessage(getString(R.string.dialog_message_no_present_book))
                    .setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(settingsActivityIntent);
                        }
                    })
                    .setNegativeButton(getString(R.string.dialog_button_cancel), null)
                    .show();
        } else {
            Intent intent = new Intent(this, AnswerActivity.class);
            startActivity(intent);
        }
    }

    public void onSettingsClicked(View view) {
        startActivity(settingsActivityIntent);
    }

    String getPresentBook() {
        SharedPreferences presentBook = getSharedPreferences("presentBook", MODE_PRIVATE);
        return presentBook.getString("presentBook", getString(R.string.present_book_undefined));
    }
}