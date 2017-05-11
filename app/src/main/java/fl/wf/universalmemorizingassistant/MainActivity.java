package fl.wf.universalmemorizingassistant;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static android.os.Environment.getExternalStorageDirectory;
import static fl.wf.universalmemorizingassistant.BasicFile.appendStringToFile;
import static fl.wf.universalmemorizingassistant.BasicFile.createNewFile;
import static fl.wf.universalmemorizingassistant.BasicFile.createNewFolder;
import static fl.wf.universalmemorizingassistant.BasicFile.readStringFromFile;
import static fl.wf.universalmemorizingassistant.BasicFile.writeStringToFile;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FLWFMainActivity";

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4289;

    private String myDocPath = "/U_Memorizing";
    private String myUserDataFileName = "/测试文件.xml";

    private ArrayList<Book> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check if the storage is writable
        if (!DataChecker.isExternalStorageWritable()) {
            Toast.makeText(this, "ExternalStorageUnavailable", Toast.LENGTH_LONG).show();
            finish();
        }
        getRuntimePermission();
        initializingUserData(myDocPath, myUserDataFileName);
        File userDataFile = new File(getExternalStorageDirectory() + myDocPath + myUserDataFileName);
        File userDataFileShower = new File(getExternalStorageDirectory() + myDocPath + "/显示测试文件.xml");
//        testCommonFileManipulation(userDataFile);
        bookList = new ArrayList<>();
        bookList = readFromUserDataFile(userDataFile);
        writeToUserDataFile(bookList, userDataFileShower);
        File workBookFileToRead = new File(getExternalStorageDirectory() + myDocPath + "/读取测试用表.xls");
        File aNewFileToWriteTo = new File(getExternalStorageDirectory() + myDocPath + "/写入新表.xls");
        createNewFile(aNewFileToWriteTo);

        //Create and write a file succeeded,but only for xls file.xlsx still can't be created correctly
//        try {
//            File workBookFileToCreate = new File(getExternalStorageDirectory() + myDocPath + "/测试生成表格.xls");
//            HSSFReadWrite.testCreateSampleSheet(workBookFileToCreate.getAbsolutePath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        File workBookFileToCreate = new File(getExternalStorageDirectory() + myDocPath + "/测试生成表格.xls");
        try {
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(workBookFileToRead));

            HSSFSheet s = wb.createSheet();
            wb.setSheetName(0, "HSSF Test");
            HSSFRow row = s.createRow(0);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("aaa");
            FileOutputStream out = new FileOutputStream(workBookFileToCreate);
            wb.write(out);
            out.close();
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void writeToUserDataFile(ArrayList<Book> bookListToWrite, File userDataFileToWrite) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(userDataFileToWrite);
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlSerializer xmlSerializer = xmlPullParserFactory.newSerializer();
            xmlSerializer.setOutput(fileOutputStream, "UTF-8");
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag(null, "Books");
            for (Book book : bookListToWrite) {
                xmlSerializer.startTag(null, "Book");
                xmlSerializer.attribute(null, "id", String.valueOf(book.getId()));
                xmlSerializer.startTag(null, "Name");
                xmlSerializer.text(book.getName());
                xmlSerializer.endTag(null, "Name");
                xmlSerializer.startTag(null, "Rank");
                xmlSerializer.text(String.valueOf(book.getRank()));
                xmlSerializer.endTag(null, "Rank");
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

    ArrayList<Book> readFromUserDataFile(File file) {
        try {
            InputStream inputStream = new FileInputStream(file);
            InputSource inputSource = new InputSource(inputStream);
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            BookSaxHandler bookSaxHandler = new BookSaxHandler(bookList);
            xmlReader.setContentHandler(bookSaxHandler);
            xmlReader.parse(inputSource);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        String result = "";
        for (Book b : bookList) {
            result += b.toString();
        }
        Log.d(TAG, "onCreate: " + result);
        return bookList;
    }

    void testCommonFileManipulation(File file) {
        writeStringToFile(file, "Here is another string to write.\n");
        appendStringToFile(file, "Here is a test content to append");
        Log.d(TAG, "onCreate: Read from file:\n" + readStringFromFile(file));
    }

    //try to create user data folder and file.If already created,this will not create new file
    void initializingUserData(String appDataFolder, String userDataFileName) {
        File appDataFolderPath = new File(getExternalStorageDirectory() + appDataFolder);
        createNewFolder(appDataFolderPath);

        File userDataFile = new File(getExternalStorageDirectory() + appDataFolder + userDataFileName);
        createNewFile(userDataFile);

        // TODO: 2017/5/9 when all done,this file will be no longer needed  
        File userDataFileShower = new File(getExternalStorageDirectory() + appDataFolder + "/显示测试文件.xml");
        createNewFile(userDataFileShower);
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
                    initializingUserData(myDocPath, myUserDataFileName);
                } else {
                    Toast.makeText(this, "Need Permission", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }
}