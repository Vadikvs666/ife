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

/**
 *
 * @author vadim
 */
public class ExcellWorker {
    private File file;

    public ExcellWorker(File file) {
        this.file = file;
    }
    
    public String[][] getSheet(){
        String[][] sheet=new String[255][65000];
        String ext=Utility.getFileExtensions(file);
        switch(ext){
            case "xls":
                    sheet=getSheetfromXLS();
                break;
            case "xlsx":
                    sheet=getSheetfromXLSX();
                break;
            default:
                break;
        }
        return sheet;
    }

    private String getData(int row, int col) {
        try {
            HSSFWorkbook myExcelBook = new HSSFWorkbook(new FileInputStream(file));
            int index_sheet=myExcelBook.getActiveSheetIndex();
            HSSFSheet myExcelSheet =myExcelBook.getSheetAt(index_sheet);
            HSSFRow row = myExcelSheet.getRow(0);
            String name = row.getCell(0).getStringCellValue();
 
            
            myExcelBook.close();
        } catch (FileNotFoundException   ex) {
            Logger.getLogger(ExcellWorker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExcellWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String[][] getSheetfromXLSX() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
