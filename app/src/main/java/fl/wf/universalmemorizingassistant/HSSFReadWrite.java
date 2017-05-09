package fl.wf.universalmemorizingassistant;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

final class HSSFReadWrite {

    /**
     * creates an {@link HSSFWorkbook} with the specified OS filename.
     */
    public static HSSFWorkbook readFile(String filename) throws IOException {
        FileInputStream fis = new FileInputStream(filename);
        try {
            return new HSSFWorkbook(fis);		// NOSONAR - should not be closed here
        } finally {
            fis.close();
        }
    }

    /**
     * given a filename this outputs a sample sheet with just a set of
     * rows/cells.
     */
    static void testCreateSampleSheet(String outputFilename) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        try {
            HSSFSheet s = wb.createSheet();
            wb.setSheetName(0, "HSSF Test");
            int rownum;
            for (rownum = 0; rownum < 300; rownum++) {
                HSSFRow r = s.createRow(rownum);
                for (int cellnum = 0; cellnum < 50; cellnum += 2) {
                    HSSFCell c = r.createCell(cellnum);
                    c.setCellValue(rownum * 10000 + cellnum
                            + (((double) rownum / 1000) + ((double) cellnum / 10000)));
                    c = r.createCell(cellnum + 1);
                    c.setCellValue(new HSSFRichTextString("TEST"));
                }
            }
            FileOutputStream out = new FileOutputStream(outputFilename);
            try {
                wb.write(out);
            } finally {
                out.close();
            }
        } finally {
            wb.close();
        }
    }

    static void testCreateSampleXLSXSheet(String outputFilename) throws IOException {
        // FIXME: 2017/5/9 http://poi.apache.org/faq.html#faq-N101E6
        XSSFWorkbook wb = new XSSFWorkbook();
        try {
            XSSFSheet s = wb.createSheet();
            wb.setSheetName(0, "XSSF Test");
            int rownum;
            for (rownum = 0; rownum < 300; rownum++) {
                XSSFRow r = s.createRow(rownum);
                for (int cellnum = 0; cellnum < 50; cellnum += 2) {
                    XSSFCell c = r.createCell(cellnum);
                    c.setCellValue(rownum * 10000 + cellnum
                            + (((double) rownum / 1000) + ((double) cellnum / 10000)));
                    c = r.createCell(cellnum + 1);
                    c.setCellValue(new HSSFRichTextString("TEST"));
                }
            }
            FileOutputStream out = new FileOutputStream(outputFilename);
            try {
                wb.write(out);
            } finally {
                out.close();
            }
        } finally {
            wb.close();
        }
    }
}