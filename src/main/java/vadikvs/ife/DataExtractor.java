/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.mail.MessagingException;

/**
 *
 * @author vadim
 */
public class DataExtractor {

    private AtachmentEntity atach;
    private ParamsEntity param;
    private File file;

    public DataExtractor(AtachmentEntity atach, ParamsEntity param) {
        this.atach = atach;
        this.param = param;
    }

    public DataExtractor(File file, ParamsEntity param) {
        this.file = file;
        this.param = param;
    }

    public List<ProductEntity> getProductsFromFile(String tempPath, String converterServer) {
        List<ProductEntity> list = new ArrayList<>();
        int start_row = param.getStart_row();
        int max_row = param.getMax_row();
        int count_col = param.getCount_col();
        int summ_col = param.getSumm_col();
        int artikul_col = param.getArtikul_col();
        int name_col = param.getName_col();
        if (this.file == null) {
            this.file = getFileFromAtach(atach, tempPath);
        }
        ExcellWorker excell = new ExcellWorker(this.file, converterServer, tempPath);
        for (int row = start_row; row < max_row; row++) {
            String artikul = excell.getData(row, artikul_col);
            String name = excell.getData(row, name_col);
            String count = excell.getData(row, count_col);
            String summ = excell.getData(row, summ_col);
            if (count != "" && name != "" && summ != "") {

                System.out.println("artikul: " + artikul + " name :" + name + " count: " + count + " summ: " + summ);
                try {
                    String preparedSumm = Utility.prepareNumeric(summ);

                    ProductEntity entity = new ProductEntity(artikul, name,
                            Utility.prepareNumeric(count),
                            preparedSumm);
                    list.add(entity);
                } catch (Exception ex) {
                    Utility.showAlert("Информация", "Строка в число проверьте параметры!", ex.toString());
                }
            } else {
                // ;
            }
        }
        return list;
    }

    private File getFileFromAtach(AtachmentEntity atach, String tempPath) {
        atach.saveAtach(tempPath);
        File file = new File(tempPath + File.separatorChar + atach.getFilename());
        return file;
    }

}
