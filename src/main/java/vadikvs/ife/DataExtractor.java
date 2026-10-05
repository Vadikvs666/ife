package vadikvs.ife;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DataExtractor {
    private final AtachmentEntity atach;
    private final ParamsEntity param;
    private final File file;

    public DataExtractor(AtachmentEntity atach, ParamsEntity param) {
        this.atach = atach;
        this.param = param;
        this.file = null;
    }

    public DataExtractor(File file, ParamsEntity param) {
        this.file = file;
        this.param = param;
        this.atach = null;
    }

    public List<ProductEntity> getProductsFromFile(String tempPath, String converterServer) throws IOException {
        if (param == null) {
            throw new IOException("Сначала выберите фирму и параметры импорта.");
        }
        File source = file;
        Path temporaryDirectory = null;
        File converted = null;
        try {
            if (source == null) {
                if (atach == null) {
                    throw new IOException("Файл не выбран.");
                }
                Path root = new File(tempPath).toPath();
                Files.createDirectories(root);
                temporaryDirectory = Files.createTempDirectory(root, "ife-import-");
                source = atach.saveAtach(temporaryDirectory.toString());
            }
            List<ProductEntity> list = new ArrayList<>();
            File excelFile = source;
            // Preserve the original import route: only XLSX is read directly.
            if (!Utility.getFileExtensions(source).equalsIgnoreCase("xlsx")) {
                converted = ExcelConverter.convert(source, converterServer, tempPath);
                excelFile = converted;
            }
            try (ExcellWorker excell = new ExcellWorker(excelFile)) {
                for (int row = param.getStart_row(); row < param.getMax_row(); row++) {
                    String artikul = excell.getData(row, param.getArtikul_col());
                    String name = excell.getData(row, param.getName_col());
                    String count = excell.getData(row, param.getCount_col());
                    String summ = excell.getData(row, param.getSumm_col());
                    if (!count.isEmpty() && !name.isEmpty() && !summ.isEmpty()) {
                        try {
                            list.add(new ProductEntity(artikul, name,
                                    Utility.prepareNumeric(count), Utility.prepareNumeric(summ)));
                        } catch (NumberFormatException ex) {
                            throw new IOException("Не удалось прочитать числа в строке " + row
                                    + " файла «" + source.getName() + "». Проверьте параметры импорта.", ex);
                        }
                    }
                }
            }
            return list;
        } finally {
            if (converted != null) {
                Files.deleteIfExists(converted.toPath());
            }
            if (temporaryDirectory != null) {
                if (source != null) {
                    Files.deleteIfExists(source.toPath());
                }
                Files.deleteIfExists(temporaryDirectory);
            }
        }
    }
}
