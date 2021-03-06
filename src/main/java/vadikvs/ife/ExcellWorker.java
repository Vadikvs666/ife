/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author vadim
 */
public class ExcellWorker {

    private File file;
    HSSFWorkbook xlsBook = null;
    XSSFWorkbook xlsxBook = null;
    Workbook jxlsBook = null;

    public ExcellWorker(File file, String converterServer, String tempPath) {
        try {
            this.file = file;
            String ext = Utility.getFileExtensions(file);
            System.out.println(ext);
            switch (ext) {
                case "xlsx":
                    xlsxBook = new XSSFWorkbook(new FileInputStream(file));
                    xlsBook = null;
                    break;
                default:
                    this.file = Utility.convertXLSToXLSXFile(file, converterServer, tempPath);
                    System.out.println(this.file.getAbsolutePath());
                    xlsxBook = new XSSFWorkbook(new FileInputStream(this.file));
                    xlsBook = null;
                    break;
            }
        } catch (IOException ex) {

            Logger.getLogger(ExcellWorker.class.getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(ExcellWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getData(int row, int col) {
        String data = "";
        if (jxlsBook != null) {
            data = getDatafromJXLS(row, col);
        } else if (xlsxBook != null) {
            data = getDatafromXLSX(row, col);
        } else if (xlsBook != null) {
            data = getDatafromXLS(row, col);
        }
        return data;
    }

    private String getDatafromXLSX(int row, int col) {
        String data = "";
        int index_sheet = xlsxBook.getActiveSheetIndex();
        XSSFSheet myExcelSheet = xlsxBook.getSheetAt(index_sheet);
        XSSFRow rowH = myExcelSheet.getRow(row - 1);
        if (rowH != null) {
            XSSFCell cell = rowH.getCell(col - 1);
            if (cell != null) {
                if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
                    data = cell.getStringCellValue();
                }
                if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
                    data = String.valueOf(cell.getNumericCellValue());
                }
            }
        }
        return data;
    }

    private String getDatafromXLS(int row, int col) {
        String data = "";
        int index_sheet = xlsBook.getActiveSheetIndex();
        HSSFSheet myExcelSheet = xlsBook.getSheetAt(index_sheet);
        HSSFRow rowH = myExcelSheet.getRow(row - 1);
        if (rowH != null) {
            HSSFCell cell = rowH.getCell(col - 1);
            if (cell != null) {
                if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                    data = cell.getStringCellValue();
                }
                if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                    data = String.valueOf(cell.getNumericCellValue());
                }
            }
        }
        return data;
    }

    private String getDatafromJXLS(int row, int col) {
        String data = "";
        Sheet sheet = jxlsBook.getSheet(0);
        if (row - 1 < sheet.getRows()) {
            if (col - 1 < sheet.getColumns()) {
                Cell cell = sheet.getCell(col - 1, row - 1);
                data = cell.getContents();
            }
        }
        return data;
    }

}
