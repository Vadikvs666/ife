package vadikvs.ife;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/** Uses the existing conversion service's multipart field named "file". */
public final class ExcelConverter {
    private ExcelConverter() { }

    public static File convert(File source, String server, String tempPath) throws IOException {
        if (server == null || server.trim().isEmpty()) {
            throw new IOException("Укажите сервер конвертирования файлов в настройках.");
        }
        RequestConfig config = RequestConfig.custom().setConnectTimeout(15000)
                .setConnectionRequestTimeout(15000).setSocketTimeout(120000).build();
        Path converted = null;
        boolean completed = false;
        try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(config).build()) {
            HttpPost post = new HttpPost(server.trim());
            post.setEntity(MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addPart("file", new FileBody(source, ContentType.DEFAULT_BINARY)).build());
            try (CloseableHttpResponse response = client.execute(post)) {
                int status = response.getStatusLine().getStatusCode();
                if (status < 200 || status >= 300 || response.getEntity() == null) {
                    throw new IOException("Сервер конвертации вернул HTTP " + status + ".");
                }
                Path directory = new File(tempPath).toPath();
                Files.createDirectories(directory);
                converted = Files.createTempFile(directory, "ife-converted-", ".xlsx");
                try (InputStream input = response.getEntity().getContent()) {
                    AttachmentReader.copy(input, converted);
                }
                if (Files.size(converted) == 0) {
                    throw new IOException("Сервер конвертации вернул пустой файл.");
                }
                completed = true;
                return converted.toFile();
            }
        } catch (IllegalArgumentException ex) {
            throw new IOException("Некорректный адрес сервера конвертации.", ex);
        } finally {
            if (!completed && converted != null) {
                Files.deleteIfExists(converted);
            }
        }
    }
}
