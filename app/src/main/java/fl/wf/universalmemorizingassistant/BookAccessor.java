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

    // TODO: 2017/5/11 move the rowCheck part to this function,and make a wholly check and validate here
    static HSSFWorkbook openAndValidateBook(File bookFileToOpen, int maxTimes) throws IOException {
        // TODO: 2017/5/11 fix the invalid data format
        // TODO: 2017/5/11 staff the leftTimes cell if it's blank
        FileInputStream fis = new FileInputStream(bookFileToOpen);
        HSSFWorkbook wb = new HSSFWorkbook(fis);
        fis.close();

        HSSFSheet sheet = wb.getSheetAt(0);

        Log.d(TAG, "openAndValidateBook: sheet.getLastRowNum(): " + sheet.getLastRowNum());
        Log.d(TAG, "openAndValidateBook: sheet.getPhysicalNumberOfRows(): " + sheet.getPhysicalNumberOfRows());

        //This loop is used to remove all the empty rows in the sheet
        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            if (sheet.getRow(i) == null) {
                sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
                i--;
                continue;
            }
            if (sheet.getRow(i).getLastCellNum() == -1) {
                sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
                i--;
            }
        }

        //This part is used to set the dataType of columns
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            HSSFRow r = sheet.getRow(i);
            HSSFCell c0 = r.getCell(0);
            if (c0 == null) {
                c0 = r.createCell(0);
                c0.setCellValue("NoData");
            }
            HSSFCell c1 = sheet.getRow(i).getCell(1);
            if (c1 == null) {
                c1 = r.createCell(1);
                c1.setCellValue("NoData");
            }
            HSSFCell c2 = sheet.getRow(i).getCell(2);
            if (c2 == null) {
                c2 = r.createCell(2);
                c2.setCellValue(maxTimes);
            }
        }

        Log.d(TAG, "openAndValidateBook: sheet.getLastRowNum(): " + sheet.getLastRowNum());
        Log.d(TAG, "openAndValidateBook: sheet.getPhysicalNumberOfRows(): " + sheet.getPhysicalNumberOfRows());

        return wb;
    }

    static int rowCheck(HSSFWorkbook workbookToUse, int indexOfRow) {
        // TODO: 2017/5/11 to see what will happen  when the cell is blank
        HSSFRow row = workbookToUse.getSheetAt(0).getRow(indexOfRow);
        if (row == null) return ROW_END;
        HSSFCell cell = row.getCell(2);
        int timesLeft = 0;
        if (cell == null) return ROW_VALID;
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                timesLeft = (int) cell.getNumericCellValue();
                break;
            case STRING:
                timesLeft = Integer.valueOf(cell.getStringCellValue());
                cell.setCellType(NUMERIC);
                cell.setCellValue(timesLeft);
                break;
            default:
                break;
        }
        if (timesLeft == 0) return ROW_INVALID;
        else return ROW_VALID;
    }

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

    static void closeBookAndSave(HSSFWorkbook usedWorkbook, File bookFileToUpdate) throws IOException {
        FileOutputStream out = new FileOutputStream(bookFileToUpdate);
        usedWorkbook.write(out);
        out.close();
        usedWorkbook.close();
    }
}