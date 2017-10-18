/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;
import java.time.LocalDate;
import java.util.Date;
/**
 *
 * @author vadim
 */
public class Ife {
    private String data;
    private String hash;
    private Integer firm;
    private final LocalDate createdAt;
    private Float addition;
    private String docNumber;

    public Ife(String data,  Integer firm,  Float addition, String docNumber) {
        this.data = data;
        this.hash = makeHash(data);
        this.firm = firm;
        this.createdAt = LocalDate.now();
        this.addition = addition;
        this.docNumber = docNumber;
    }
    
    private String makeHash(String data){
        Date date=new Date();
        String temp=data+date.toString();
        return String.valueOf(temp.hashCode());
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        this.hash = makeHash(data);
    }

    public String getHash() {
        return hash;
    }

    public Integer getFirm() {
        return firm;
    }

    public void setFirm(Integer firm) {
        this.firm = firm;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }


    public Float getAddition() {
        return addition;
    }

    public void setAddition(Float addition) {
        this.addition = addition;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }
    
}
