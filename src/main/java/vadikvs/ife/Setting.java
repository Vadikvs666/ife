/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

/**
 *
 * @author vadim
 */
public class Setting {
    private String name;
    private String print;
    private String value;

    public Setting(String name, String print, String value) {
        this.name = name;
        this.print = print;
        this.value = value;
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getPrint(){
        return this.print;
    }
    
    public String getValue(){
        return this.value;
    }
    
    public void setValue(String value){
        this.value=value;
    }
}
