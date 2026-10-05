package vadikvs.ife;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.mail.MessagingException;
import javax.mail.Part;

/** A MIME attachment or a specific file inside an attached ZIP. */
public class AtachmentEntity {
    private final Part part;
    private final MessageEntity message;
    private final String attachmentName;
    private final String entryName;
    private final int entryIndex;

    AtachmentEntity(Part part, MessageEntity message, String attachmentName,
            String entryName, int entryIndex) {
        this.part = part;
        this.message = message;
        this.attachmentName = attachmentName;
        this.entryName = entryName;
        this.entryIndex = entryIndex;
    }

    public String getFilename() {
        return entryName == null ? attachmentName : attachmentName + " → " + entryName;
    }

    public Integer getMessageId() {
        return message.getId();
    }

    public MessageEntity getMessage() {
        return message;
    }

    public File saveAtach(String path) throws IOException {
        String name = AttachmentReader.safeFilename(entryName == null ? attachmentName : entryName);
        Path directory = new File(path).toPath();
        Files.createDirectories(directory);
        Path destination = directory.resolve(name);
        // Refuse to silently overwrite a different attachment with the same name.
        Files.createFile(destination);
        try {
            if (entryName == null) {
                try (InputStream input = part.getInputStream()) {
                    AttachmentReader.copy(input, destination);
                }
            } else {
                AttachmentReader.extract(part, entryIndex, entryName, destination);
            }
            return destination.toFile();
        } catch (IOException | MessagingException | RuntimeException ex) {
            Files.deleteIfExists(destination);
            throw new IOException("Не удалось сохранить «" + getFilename() + "»: " + ex.getMessage(), ex);
        }
    }

    @Override
    public String toString() {
        return getFilename();
    }
}