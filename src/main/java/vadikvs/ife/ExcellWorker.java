package vadikvs.ife;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/** Reads XLS and XLSX locally without changing the source file. */
public class ExcellWorker implements Closeable {
    private final Workbook workbook;

    public ExcellWorker(File file) throws IOException {
        if (file == null) {
            throw new IOException("Файл Excel не выбран.");
        }
        try {
            workbook = WorkbookFactory.create(file, null, true);
        } catch (IOException | InvalidFormatException | RuntimeException ex) {
            throw new IOException("Не удалось открыть «" + file.getName()
                    + "». Нужен файл Excel XLS или XLSX без пароля.", ex);
        }
    }

    /** Supplier row and column numbers are one-based. */
    public String getData(int row, int col) {
        if (row < 1 || col < 1 || workbook.getNumberOfSheets() == 0) {
            return "";
        }
        Row sourceRow = workbook.getSheetAt(workbook.getActiveSheetIndex()).getRow(row - 1);
        Cell cell = sourceRow == null ? null : sourceRow.getCell(col - 1);
        if (cell == null) {
            return "";
        }
        CellType type = cell.getCellTypeEnum();
        if (type == CellType.FORMULA) {
            // Read the result saved by Excel, without recalculating the document.
            type = cell.getCachedFormulaResultTypeEnum();
        }
        if (type == CellType.STRING) {
            return cell.getStringCellValue();
        }
        if (type == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        }
        return "";
    }

    @Override
    public void close() throws IOException {
        workbook.close();
    }
}