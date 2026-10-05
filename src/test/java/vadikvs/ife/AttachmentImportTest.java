package vadikvs.ife;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.activation.DataHandler;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

public class AttachmentImportTest {
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    @Test public void listsAndSavesUtf8ZipFilesInsideFolders() throws Exception {
        byte[] bytes = zip(StandardCharsets.UTF_8, new String[]{"счета/", "счета/счёт.xls"},
                new byte[][]{new byte[0], workbook(false)});
        MessageEntity message = new MessageEntity(message(attachment("Счета.ZIP", bytes)));
        AttachmentReader.Result result = message.loadAttachments();
        assertTrue(result.warnings.toString(), result.warnings.isEmpty());
        assertEquals(2, result.files.size()); // The original archive plus its file.
        assertEquals("Счета.ZIP/счета/счёт.xls", result.files.get(1).getFilename());
        assertSame(result, message.loadAttachments());
        File saved = result.files.get(1).saveAtach(temporary.newFolder().getPath());
        assertEquals("счёт.xls", saved.getName());
        try (ExcellWorker excel = new ExcellWorker(saved)) {
            assertEquals("Товар", excel.getData(2, 2));
        }
    }

    @Test public void supportsLegacyRussianZipNames() throws Exception {
        byte[] data = "данные".getBytes(StandardCharsets.UTF_8);
        AttachmentReader.Result result = AttachmentReader.read(message(attachment("legacy.zip",
                zip(Charset.forName("IBM866"), new String[]{"счёт.xls"}, new byte[][]{data}))), null);
        assertTrue(result.warnings.toString(), result.warnings.isEmpty());
        File saved = result.files.get(1).saveAtach(temporary.newFolder().getPath());
        assertEquals("счёт.xls", saved.getName());
        assertArrayEquals(data, Files.readAllBytes(saved.toPath()));
    }

    @Test public void findsArchivesInsideNestedMimeAndPreservesDirectAttachments() throws Exception {
        MimeMultipart nested = new MimeMultipart("mixed");
        nested.addBodyPart(attachment("nested.zip", zip(StandardCharsets.UTF_8,
                new String[]{"invoice.xls"}, new byte[][]{new byte[]{1, 2, 3}})));
        MimeBodyPart container = new MimeBodyPart();
        container.setContent(nested);
        AttachmentReader.Result result = AttachmentReader.read(message(container,
                attachment("direct.xlsx", new byte[]{4, 5})), null);
        assertEquals(3, result.files.size());
        File saved = result.files.get(2).saveAtach(temporary.newFolder().getPath());
        assertArrayEquals(new byte[]{4, 5}, Files.readAllBytes(saved.toPath()));
    }

    @Test public void sameBasenamesInDifferentFoldersRemainDistinct() throws Exception {
        AttachmentReader.Result result = AttachmentReader.read(message(attachment("invoices.zip",
                zip(StandardCharsets.UTF_8, new String[]{"a/invoice.xls", "b/invoice.xls"},
                        new byte[][]{{1}, {2}}))), null);
        assertEquals(3, result.files.size());
        assertArrayEquals(new byte[]{1}, Files.readAllBytes(result.files.get(1)
                .saveAtach(temporary.newFolder().getPath()).toPath()));
        assertArrayEquals(new byte[]{2}, Files.readAllBytes(result.files.get(2)
                .saveAtach(temporary.newFolder().getPath()).toPath()));
    }

    @Test public void duplicateMimeFilenamesKeepTheirOwnContents() throws Exception {
        AttachmentReader.Result result = AttachmentReader.read(message(
                attachment("same.xls", new byte[]{1}), attachment("same.xls", new byte[]{2})), null);
        assertArrayEquals(new byte[]{2}, Files.readAllBytes(result.files.get(1)
                .saveAtach(temporary.newFolder().getPath()).toPath()));
    }

    @Test public void corruptZipReportsWarningWithoutHidingOtherAttachments() throws Exception {
        AttachmentReader.Result result = AttachmentReader.read(message(
                attachment("bad.zip", new byte[]{1, 2, 3}), attachment("good.xls", workbook(false))), null);
        assertEquals(1, result.warnings.size());
        assertEquals(2, result.files.size());
        assertEquals("good.xls", result.files.get(1).getFilename());
    }

    @Test public void rejectsTraversalPaths() throws Exception {
        for (String name : new String[]{"../outside.xls", "folder/../../outside.xls",
                "C:\\outside.xls", "/outside.xls", "..\\outside.xls"}) {
            AttachmentReader.Result result = AttachmentReader.read(message(attachment("unsafe.zip",
                    zip(StandardCharsets.UTF_8, new String[]{name}, new byte[][]{{1}}))), null);
            assertEquals(name, 1, result.warnings.size());
            assertEquals(name, 1, result.files.size());
        }
    }

    @Test public void neverOverwritesExistingSavedFile() throws Exception {
        AtachmentEntity item = AttachmentReader.read(message(attachment("same.xls", new byte[]{1})), null)
                .files.get(0);
        File directory = temporary.newFolder();
        File existing = new File(directory, "same.xls");
        Files.write(existing.toPath(), new byte[]{9});
        try {
            item.saveAtach(directory.getPath());
            fail("Expected an existing-file error");
        } catch (IOException expected) {
            assertArrayEquals(new byte[]{9}, Files.readAllBytes(existing.toPath()));
        }
    }

    @Test public void importsXlsThroughServerAndXlsxLocallyFromZip() throws Exception {
        for (boolean xlsx : new boolean[]{false, true}) {
            String name = xlsx ? "invoice.xlsx" : "invoice.xls";
            AtachmentEntity item = AttachmentReader.read(message(attachment("invoices.zip",
                    zip(StandardCharsets.UTF_8, new String[]{name}, new byte[][]{workbook(xlsx)}))), null)
                    .files.get(1);
            File directory = temporary.newFolder();
            List<ProductEntity> products;
            try (ConverterServer server = new ConverterServer(200, workbook(true))) {
                products = new DataExtractor(item, params()).getProductsFromFile(directory.getPath(), server.url());
                assertEquals(xlsx ? 0 : 1, server.requests.get());
                if (!xlsx) {
                    assertTrue(server.request.contains("name=\"file\""));
                    assertTrue(server.request.contains("filename=\"invoice.xls\""));
                }
            }
            assertEquals(1, products.size());
            assertEquals("00123", products.get(0).getArticul());
            assertEquals("Товар", products.get(0).getName());
            assertEquals(2, products.get(0).getCount());
            assertEquals(125.25f, products.get(0).getPrice(), 0.001f);
            assertEquals(0, directory.list().length);
        }
    }

    @Test public void readsLocalWorkbooksAndCachedFormulasWithoutModifyingFiles() throws Exception {
        for (boolean xlsx : new boolean[]{false, true}) {
            byte[] bytes = workbook(xlsx);
            File file = temporary.newFile(xlsx ? "test.XLSX" : "test.XLS");
            Files.write(file.toPath(), bytes);
            try (ExcellWorker excel = new ExcellWorker(file)) {
                assertEquals("250.5", excel.getData(2, 4));
                assertEquals("", excel.getData(100, 1));
                assertEquals("", excel.getData(2, 10));
            }
            assertArrayEquals(bytes, Files.readAllBytes(file.toPath()));
            File directory = temporary.newFolder();
            try (ConverterServer server = new ConverterServer(200, workbook(true))) {
                assertEquals(1, new DataExtractor(file, params())
                        .getProductsFromFile(directory.getPath(), server.url()).size());
                assertEquals(xlsx ? 0 : 1, server.requests.get());
            }
            assertArrayEquals(bytes, Files.readAllBytes(file.toPath()));
            assertEquals(0, directory.list().length);
        }
    }

    @Test public void invalidExcelAbortsImportAndCleansExtractedFile() throws Exception {
        AtachmentEntity item = AttachmentReader.read(message(attachment("bad.zip",
                zip(StandardCharsets.UTF_8, new String[]{"bad.xlsx"}, new byte[][]{{1, 2, 3}}))), null)
                .files.get(1);
        File directory = temporary.newFolder();
        try {
            new DataExtractor(item, params()).getProductsFromFile(directory.getPath(), "http://127.0.0.1:1/not-used");
            fail("Invalid Excel must not become a successful empty import");
        } catch (IOException expected) {
            assertTrue(expected.getMessage().contains("bad.xlsx"));
            assertEquals(0, directory.list().length);
        }
    }

    @Test public void sendsDisguisedXlsToServerWithoutTryingLocalParser() throws Exception {
        File source = temporary.newFile("legacy.xls");
        Files.write(source.toPath(), "<html><table>supplier data</table></html>".getBytes(StandardCharsets.UTF_8));
        File directory = temporary.newFolder();
        try (ConverterServer server = new ConverterServer(200, workbook(true))) {
            assertEquals(1, new DataExtractor(source, params())
                    .getProductsFromFile(directory.getPath(), server.url()).size());
            assertEquals(1, server.requests.get());
            assertTrue(server.request.contains("<html><table>supplier data</table></html>"));
        }
        assertEquals(0, directory.list().length);
    }

    @Test public void conversionErrorsAbortZipImportAndCleanTemporaryFiles() throws Exception {
        AtachmentEntity item = AttachmentReader.read(message(attachment("invoices.zip",
                zip(StandardCharsets.UTF_8, new String[]{"invoice.xls"}, new byte[][]{workbook(false)}))), null)
                .files.get(1);
        for (int status : new int[]{500, 200}) {
            File directory = temporary.newFolder();
            // A 200 response containing HTML is not an Excel workbook either.
            try (ConverterServer server = new ConverterServer(status, "<html>error</html>".getBytes(StandardCharsets.UTF_8))) {
                try {
                    new DataExtractor(item, params()).getProductsFromFile(directory.getPath(), server.url());
                    fail("Expected import failure");
                } catch (IOException expected) {
                    assertEquals(1, server.requests.get());
                    assertEquals(0, directory.list().length);
                }
            }
        }
    }

    @Test public void emptyConversionResponseIsRejected() throws Exception {
        File source = temporary.newFile("empty-response.xls");
        Files.write(source.toPath(), new byte[]{1});
        File directory = temporary.newFolder();
        try (ConverterServer server = new ConverterServer(200, new byte[0])) {
            try {
                new DataExtractor(source, params()).getProductsFromFile(directory.getPath(), server.url());
                fail("Expected empty-response error");
            } catch (IOException expected) {
                assertEquals(0, directory.list().length);
            }
        }
    }

    @Test public void skipsInvalidNumbersAndContinuesWithFollowingRows() throws Exception {
        byte[] data;
        try (Workbook book = new XSSFWorkbook();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = book.createSheet("Invoice");
            for (int i = 0; i < 4; i++) {
                Row row = sheet.createRow(i);
                row.createCell(0).setCellValue("item-" + i);
                row.createCell(1).setCellValue("Товар " + i);
                row.createCell(2).setCellValue(2);
                row.createCell(3).setCellValue(100);
            }
            sheet.getRow(1).getCell(2).setCellValue("не число");
            sheet.getRow(2).getCell(3).setCellValue("не число");
            book.write(output);
            data = output.toByteArray();
        }
        for (boolean xlsx : new boolean[]{true, false}) {
            File source = temporary.newFile(xlsx ? "rows.xlsx" : "rows.xls");
            Files.write(source.toPath(), xlsx ? data : workbook(false));
            File directory = temporary.newFolder();
            try (ConverterServer server = new ConverterServer(200, data)) {
                ParamsEntity settings = new ParamsEntity(1, 5, 3, 4, 1, 2, 1, 1, 1);
                List<ProductEntity> products = new DataExtractor(source, settings)
                        .getProductsFromFile(directory.getPath(), server.url());
                assertEquals(2, products.size());
                assertEquals("item-0", products.get(0).getArticul());
                assertEquals("item-3", products.get(1).getArticul());
                assertEquals(2, products.get(1).getCount());
                assertEquals(50f, products.get(1).getPrice(), 0.001f);
                assertEquals(xlsx ? 0 : 1, server.requests.get());
                assertEquals(0, directory.list().length);
            }
        }
    }

    private static final class ConverterServer implements AutoCloseable {
        private final HttpServer server;
        final AtomicInteger requests = new AtomicInteger();
        volatile String request = "";

        ConverterServer(int status, byte[] response) throws IOException {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/converttoxlxs", exchange -> {
                ByteArrayOutputStream input = new ByteArrayOutputStream();
                byte[] buffer = new byte[8192];
                int count;
                while ((count = exchange.getRequestBody().read(buffer)) != -1) {
                    input.write(buffer, 0, count);
                }
                request = new String(input.toByteArray(), StandardCharsets.ISO_8859_1);
                requests.incrementAndGet();
                exchange.sendResponseHeaders(status, response.length == 0 ? -1 : response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            });
            server.start();
        }

        String url() {
            return "http://127.0.0.1:" + server.getAddress().getPort() + "/converttoxlxs";
        }

        @Override public void close() {
            server.stop(0);
        }
    }
    private static ParamsEntity params() {
        return new ParamsEntity(2, 4, 3, 4, 1, 2, 1, 1, 1);
    }

    private static byte[] workbook(boolean xlsx) throws Exception {
        try (Workbook book = xlsx ? new XSSFWorkbook() : new HSSFWorkbook();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            book.createSheet("Ignored");
            Sheet sheet = book.createSheet("Invoice");
            book.setActiveSheet(1);
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("00123");
            row.createCell(1).setCellValue("Товар");
            row.createCell(2).setCellValue(2);
            row.createCell(3).setCellFormula("C2*125.25");
            book.getCreationHelper().createFormulaEvaluator().evaluateAll();
            book.write(output);
            return output.toByteArray();
        }
    }

    private static byte[] zip(Charset charset, String[] names, byte[][] contents) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(bytes, charset)) {
            for (int i = 0; i < names.length; i++) {
                zip.putNextEntry(new ZipEntry(names[i]));
                zip.write(contents[i]);
                zip.closeEntry();
            }
        }
        return bytes.toByteArray();
    }

    private static MimeBodyPart attachment(String name, byte[] bytes) throws Exception {
        MimeBodyPart part = new MimeBodyPart();
        part.setDataHandler(new DataHandler(new ByteArrayDataSource(bytes, "application/octet-stream")));
        part.setFileName(name);
        return part;
    }

    private static MimeMessage message(MimeBodyPart... attachments) throws Exception {
        Session session = Session.getInstance(new Properties());
        MimeMessage message = new MimeMessage(session);
        MimeMultipart multipart = new MimeMultipart();
        MimeBodyPart body = new MimeBodyPart();
        body.setText("Тест", "UTF-8");
        multipart.addBodyPart(body);
        for (MimeBodyPart part : attachments) {
            multipart.addBodyPart(part);
        }
        message.setContent(multipart);
        message.saveChanges();
        // Reparse a real MIME message, exercising attachment transfer encodings.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        message.writeTo(output);
        return new MimeMessage(session, new ByteArrayInputStream(output.toByteArray()));
    }
}
