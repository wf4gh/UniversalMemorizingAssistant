package fl.wf.universalmemorizingassistant;

import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.apache.poi.ss.usermodel.CellType.BLANK;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

/**
 * Created by WF on 2017/5/11.
 * Used to control the data in a certain book
 * Usually invoked when the user is reciting
 */

public class BookAccessor {
    private static final String TAG = "FLWFBookAccessor";

    static final int ANSWER_RIGHT = 1;
    static final int ANSWER_WRONG = -1;
    static final int ANSWER_TOO_EASY = -10;
    static final int ANSWER_NOT_ANSWER = 0;

    static final int ROW_VALID = 1;
    static final int ROW_INVALID = 0;
    static final int ROW_END = -1;

    //When the book is opened the first time,call this method to pre-process
    static HSSFWorkbook openAndValidateBook(File bookFileToOpen, int maxTimes) throws IOException {
        FileInputStream fis = new FileInputStream(bookFileToOpen);
        HSSFWorkbook wb = new HSSFWorkbook(fis);
        fis.close();

        HSSFSheet sheet = wb.getSheetAt(0);
        if (sheet == null) {
            // TODO: 2017/5/23  change this name
            wb.createSheet("SheetName");
            Log.d(TAG, "openAndValidateBook: sheetnull");
        } else
            Log.d(TAG, "openAndValidateBook: sheetnotnull");

        Log.d(TAG, "openAndValidateBook: sheet.getLastRowNum(): " + sheet.getLastRowNum());
        Log.d(TAG, "openAndValidateBook: sheet.getPhysicalNumberOfRows(): " + sheet.getPhysicalNumberOfRows());

        //This loop is used to remove all the empty rows in the sheet
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            if (sheet.getRow(i) == null) {
                Log.d(TAG, "openAndValidateBook: NULL:" + i);
                sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
                i--;
                continue;
            }

            if (sheet.getRow(i).getLastCellNum() == -1) {
                sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
                i--;
            }
        }

        //This part is used to set the dataType and staff the blank cells
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            HSSFRow r = sheet.getRow(i);
            HSSFCell c0 = r.getCell(0);
            if (c0 == null) {
                c0 = r.createCell(0);
                c0.setCellValue("NoData");
            } else {
                c0.setCellType(STRING);
                if (c0.getStringCellValue().equals(""))
                    c0.setCellValue("NoData");
            }

            HSSFCell c1 = sheet.getRow(i).getCell(1);
            if (c1 == null) {
                c1 = r.createCell(1);
                c1.setCellValue("NoData");
            } else {
                c1.setCellType(STRING);
                if (c0.getStringCellValue().equals(""))
                    c0.setCellValue("NoData");
            }

            HSSFCell c2 = sheet.getRow(i).getCell(2);
            if (c2 == null) {
                c2 = r.createCell(2);
                c2.setCellValue(maxTimes);
            } else if (c2.getCellTypeEnum() == STRING) {
                try {
                    int value = Integer.parseInt(c2.getStringCellValue());
                    c2.setCellValue(value);
                } catch (Exception e) {
                    c2.setCellValue(maxTimes);
                }
            } else if (c2.getCellTypeEnum() != NUMERIC) {
                c2.setCellValue(maxTimes);
            } //else c2.setCellValue((int) c2.getNumericCellValue());
        }

//        Log.d(TAG, "openAndValidateBook: sheet.getLastRowNum(): " + sheet.getLastRowNum());
//        Log.d(TAG, "openAndValidateBook: sheet.getPhysicalNumberOfRows(): " + sheet.getPhysicalNumberOfRows());

        return wb;
    }

    static HSSFWorkbook createWorkbook() {
        HSSFWorkbook wb = new HSSFWorkbook();
        wb.createSheet("SheetName");
        Log.d(TAG, "openAndValidateBook: sheetCreated");
        return wb;
    }

    //for every row that is about to be loaded to show on the screen,call this
    static int rowCheck(HSSFWorkbook workbookToUse, int indexOfRow) {
        HSSFRow row = workbookToUse.getSheetAt(0).getRow(indexOfRow);
        if (row == null) return ROW_END;
        HSSFCell cell = row.getCell(2);
        // FIXME: 2017/5/17 if the row==null can correctly know that the row is null, this line is unnecessary. But this makes it work......
        if (cell == null) return ROW_END;
        if (cell.getNumericCellValue() == 0) return ROW_INVALID;
        else return ROW_VALID;
    }

    //after getting answer from the user,call this to compare it(if needed)
    static boolean compareAnswer(String userAnswer, HSSFWorkbook workbookToUse, int indexOfRow) {
        return workbookToUse.getSheetAt(0).getRow(indexOfRow).getCell(1).getStringCellValue().equals(userAnswer);
    }

    //after getting the answer state of the user answer,call this
    static void updateTimes(HSSFWorkbook workbookToUse, int indexOfRow, int maxTimes, int answerState) {
        HSSFRow rowToUpdate = workbookToUse.getSheetAt(0).getRow(indexOfRow);
        HSSFCell timesCell = rowToUpdate.getCell(2);

        //new valid row set left times
        if (timesCell.getCellTypeEnum() == BLANK)
            timesCell.setCellValue(maxTimes);

        int timesNow = (int) timesCell.getNumericCellValue();
        switch (answerState) {
            case ANSWER_RIGHT:
                timesCell.setCellValue(timesNow - 1);
                break;
            case ANSWER_WRONG:
                if (timesNow + 1 <= maxTimes)
                    timesCell.setCellValue(timesNow + 1);
                break;
            case ANSWER_TOO_EASY:
                timesCell.setCellValue(0);
                break;
            case ANSWER_NOT_ANSWER:
                break;
            default:
                break;
        }
    }

    static void setAllRowsToMaxTimes(HSSFWorkbook usedWorkbook, int maxTimes) {
        HSSFSheet sheet = usedWorkbook.getSheetAt(0);

        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            HSSFRow r = sheet.getRow(i);
            HSSFCell c2 = sheet.getRow(i).getCell(2);
            if (c2 == null) {
                c2 = r.createCell(2);
                c2.setCellValue(maxTimes);
            } else {
                c2.setCellValue(maxTimes);
            }
        }
    }

    static void closeAndSaveBook(HSSFWorkbook usedWorkbook, File bookFileToUpdate) throws IOException {
        FileOutputStream out = new FileOutputStream(bookFileToUpdate);
        usedWorkbook.write(out);
        out.close();
        usedWorkbook.close();
    }
}