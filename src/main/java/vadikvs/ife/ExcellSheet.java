/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.io.File;

/**
 *
 * @author vadim
 */
public class ExcellSheet {
    
    private String[][] sheet;

    public ExcellSheet(ExcellWorker worker) {
        sheet=new String[255][65000];
        this.sheet=worker.getSheet();
    }
    
    public ExcellSheet() {
        sheet=new String[255][65000];
        
    }

    public void setSheet(String[][] sheet) {
        sheet=new String[255][65000];
        this.sheet = sheet;
    }

    public String getData(int row,int col) {
        String data="";
        data=sheet[row][col];
        return data;
    }
    
    
    
}
