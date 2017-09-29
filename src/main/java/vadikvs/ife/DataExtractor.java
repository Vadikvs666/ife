/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vadim
 */
public class DataExtractor {
    
    private AtachmentEntity atach;
    private ParamsEntity param;

    public DataExtractor(AtachmentEntity atach, ParamsEntity param) {
        this.atach = atach;
        this.param = param;
    }
    
    public List<ProductEntity> getProductsFromFile(){
        List<ProductEntity> list=new ArrayList<>();
        atach.saveAtach("/tmp/ife");   
        return list;
    }
    
   
}
