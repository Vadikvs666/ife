/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vadikvs.ife;

/**
 *
 * @author vadim
 */
public class ParamsEntity {

    private int start_row;
    private int max_row;
    private int count_col;
    private int summ_col;
    private int artikul_col;
    private FirmEntity firm;

    public FirmEntity getFirm() {
        return firm;
    }

    public void setFirm(FirmEntity firm) {
        this.firm = firm;
    }

    
    public ParamsEntity(int start_row, int max_row, int count_col, int summ_col, int artikul_col) {
        this.start_row = start_row;
        this.max_row = max_row;
        this.count_col = count_col;
        this.summ_col = summ_col;
        this.artikul_col = artikul_col;
    }
    
    public ParamsEntity(String start_row, String max_row, String count_col, String summ_col, String artikul_col) {
        this.start_row = Integer.valueOf(start_row);
        this.max_row = Integer.valueOf(max_row);
        this.count_col = Integer.valueOf(count_col);
        this.summ_col = Integer.valueOf(summ_col);
        this.artikul_col = Integer.valueOf(artikul_col);
    }
    
    public int getStart_row() {
        return start_row;
    }
    

    public String getStart_rowString() {
        return String.valueOf(start_row);
    }

    public void setStart_row(int start_row) {
        this.start_row = start_row;
    }

    public int getMax_row() {
        return max_row;
    }
    
    public String getMax_rowString() {
        return String.valueOf(max_row);
    }

    public void setMax_row(int max_row) {
        this.max_row = max_row;
    }

    public int getCount_col() {
        return count_col;
    }
    
    public String getCount_colString() {
        return String.valueOf(count_col);
    }

    public void setCount_col(int count_col) {
        this.count_col = count_col;
    }

    public int getSumm_col() {
        return summ_col;
    }
    
    public String getSumm_colString() {
        return String.valueOf(summ_col);
    }

    public void setSumm_col(int summ_col) {
        this.summ_col = summ_col;
    }

    public int getArtikul_col() {
        return artikul_col;
    }
    
    public String getArtikul_colString() {
        return String.valueOf(artikul_col);
    }

    public void setArtikul_col(int artikul_col) {
        this.artikul_col = artikul_col;
    }
}
