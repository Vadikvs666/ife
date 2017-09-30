/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author vadim
 */
public class ExcellWorker {

    private File file;
    HSSFWorkbook xlsBook;
    XSSFWorkbook xlsxBook;

    public ExcellWorker(File file) {
        try {
            this.file = file;
            String ext = Utility.getFileExtensions(file);
            switch (ext) {
                case "xls":
                    xlsBook = new HSSFWorkbook(new FileInputStream(file));
                    xlsxBook = null;
                    break;
                case "xlsx":
                    xlsxBook = new XSSFWorkbook(new FileInputStream(file));
                    xlsBook = null;
                    break;
                default:
                    xlsBook = null;
                    xlsxBook = null;
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(ExcellWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

  

    public String getData(int row, int col) {
        String data = "";
        if(xlsBook!=null){
            data = getDatafromXLS(row, col);
        }else{
            data =getDatafromXLSX(row, col);
        }
        return data;
    }
    

    private String getDatafromXLSX(int row, int col) {
        String data = "";
        int index_sheet = xlsxBook.getActiveSheetIndex();
        XSSFSheet myExcelSheet = xlsxBook.getSheetAt(index_sheet);
        XSSFRow rowH = myExcelSheet.getRow(0);
        data = rowH.getCell(0).getStringCellValue();
        return data;
    }

    private String getDatafromXLS(int row, int col) {
        String data = "";
        int index_sheet = xlsBook.getActiveSheetIndex();
        HSSFSheet myExcelSheet = xlsBook.getSheetAt(index_sheet);
        HSSFRow rowH = myExcelSheet.getRow(0);
        data = rowH.getCell(0).getStringCellValue();
        return data;
    }

}
