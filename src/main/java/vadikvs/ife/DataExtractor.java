/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.io.File;
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

    public List<ProductEntity> getProductsFromFile() {
        List<ProductEntity> list = new ArrayList<>();
        atach.saveAtach("/tmp/ife");
        int start_row = param.getStart_row();
        int max_row = param.getMax_row();
        int count_col = param.getCount_col();
        int summ_col = param.getSumm_col();
        int artikul_col = param.getArtikul_col();
        int name_col = param.getName_col();
        File file = new File("/tmp/ife" + File.pathSeparator + atach.getFilename());
        ExcellWorker excell = new ExcellWorker(file);
        for (int row = start_row; row < max_row; row++) {
            String artikul = excell.getData(row, artikul_col);
            String name = excell.getData(row, name_col);
            String count = excell.getData(row, count_col);
            String summ = excell.getData(row, summ_col);
            System.out.println("artikul: "+artikul+"name :"+name+"count: "+count+"summ: "+summ);
            ProductEntity entity = new ProductEntity(artikul, name, count, summ);
            list.add(entity);
        }
        return list;
    }

}
