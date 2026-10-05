package vadikvs.ife;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeUtility;

/** Lists ZIP contents without unpacking every file. */
public final class AttachmentReader {
    private static final long MAX_BYTES = 100L * 1024 * 1024;
    private static final int MAX_ENTRIES = 10000;
    // ZIP's UTF-8 flag overrides this fallback for older Windows/DOS archives.
    private static final Charset ZIP_CHARSET = Charset.forName("IBM866");

    public static final class Result {
        public final List<AtachmentEntity> files = new ArrayList<>();
        public final List<String> warnings = new ArrayList<>();
    }

    private AttachmentReader() { }

    public static Result read(Part message, MessageEntity owner) throws IOException, MessagingException {
        Result result = new Result();
        collect(message, owner, result, 0);
        return result;
    }

    private static void collect(Part part, MessageEntity owner, Result result, int depth)
            throws IOException, MessagingException {
        if (depth > 30) {
            throw new IOException("Слишком много уровней вложенности письма.");
        }
        String rawName = part.getFileName();
        if (rawName != null) {
            String name = MimeUtility.decodeText(rawName);
            result.files.add(new AtachmentEntity(part, owner, name, null, -1));
            if (name.toLowerCase(Locale.ROOT).endsWith(".zip")) {
                try {
                    result.files.addAll(listZip(part, owner, name));
                } catch (IOException | MessagingException | RuntimeException ex) {
                    result.warnings.add("Архив «" + name + "» не открыт: " + ex.getMessage());
                }
            }
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                collect(multipart.getBodyPart(i), owner, result, depth + 1);
            }
        }
    }

    private static List<AtachmentEntity> listZip(Part part, MessageEntity owner, String name)
            throws IOException, MessagingException {
        Path archive = downloadZip(part);
        try (ZipFile zip = new ZipFile(archive.toFile(), ZIP_CHARSET)) {
            List<AtachmentEntity> files = new ArrayList<>();
            Enumeration<? extends ZipEntry> entries = zip.entries();
            int index = 0;
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (++index > MAX_ENTRIES) {
                    throw new IOException("В архиве больше " + MAX_ENTRIES + " записей.");
                }
                if (!entry.isDirectory()) {
                    safeFilename(entry.getName());
                    if (entry.getSize() > MAX_BYTES) {
                        throw new IOException("Файл в архиве больше 100 МБ.");
                    }
                    files.add(new AtachmentEntity(part, owner, name, entry.getName(), index - 1));
                }
            }
            return files;
        } finally {
            Files.deleteIfExists(archive);
        }
    }

    static void extract(Part part, int entryIndex, String entryName, Path destination)
            throws IOException, MessagingException {
        Path archive = downloadZip(part);
        try (ZipFile zip = new ZipFile(archive.toFile(), ZIP_CHARSET)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            int index = 0;
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (index++ == entryIndex && entry.getName().equals(entryName)) {
                    try (InputStream input = zip.getInputStream(entry)) {
                        copy(input, destination);
                    }
                    return;
                }
            }
            throw new IOException("Выбранный файл больше не найден в архиве.");
        } finally {
            Files.deleteIfExists(archive);
        }
    }

    private static Path downloadZip(Part part) throws IOException, MessagingException {
        Path archive = Files.createTempFile("ife-attachment-", ".zip");
        try (InputStream input = part.getInputStream()) {
            copy(input, archive);
            return archive;
        } catch (IOException | MessagingException | RuntimeException ex) {
            Files.deleteIfExists(archive);
            throw ex;
        }
    }

    static void copy(InputStream input, Path destination) throws IOException {
        try (OutputStream output = Files.newOutputStream(destination)) {
            byte[] buffer = new byte[8192];
            long total = 0;
            int count;
            while ((count = input.read(buffer)) != -1) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new IOException("Загрузка отменена.");
                }
                total += count;
                if (total > MAX_BYTES) {
                    throw new IOException("Размер файла превышает 100 МБ.");
                }
                output.write(buffer, 0, count);
            }
        }
    }

    static String safeFilename(String name) throws IOException {
        String normalized = name.replace('\\', '/');
        String[] segments = normalized.split("/", -1);
        if (normalized.startsWith("/") || normalized.contains(":")) {
            throw new IOException("Недопустимый путь файла: " + name);
        }
        for (String segment : segments) {
            if (segment.equals("..") || segment.indexOf('\0') >= 0) {
                throw new IOException("Недопустимый путь файла: " + name);
            }
        }
        String leaf = segments[segments.length - 1];
        if (leaf.isEmpty() || leaf.equals(".")) {
            throw new IOException("Пустое имя файла.");
        }
        return leaf;
    }
}
