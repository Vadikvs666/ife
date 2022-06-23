/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import javafx.scene.control.Alert;
import javafx.application.Platform;

/**
 *
 * @author vadim
 */
public class Utility {

    /**
     * Save file by InputStream need path and filename
     *
     * @param stream
     * @param filename
     * @param path
     * @return
     */
    public static boolean saveFile(InputStream stream, String filename, String path) {
        try {
            File targetFile = new File(path + File.separatorChar + filename);
            FileUtils.copyInputStreamToFile(stream, targetFile);

        } catch (IOException ex) {
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, "Unable to save file", ex);
            return false;
        }
        return true;
    }

    public static String getFileExtensions(File file) {
        String extension = "";
        String fileName = file.getName();
        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    public static String prepareNumeric(String data) {
        String res = "";
        for (int i = 0; i < data.length(); i++) {
            if ((int) data.charAt(i) != 160 && (int) data.charAt(i) != 32) {
                res = res + data.charAt(i);
            }
        }
        return res.replace(",", ".");
    }

    public static String encodeUrl(String url) {
        String uri = "";
        try {
            uri = URLEncoder.encode(url, "utf-8");

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return uri;
    }

    public static File convertXLSToXLSXFile(File file, String converterServer, String tempPath) throws Exception {

        HttpClient httpclient = HttpClientBuilder.create().build();;
        HttpPost post = new HttpPost(converterServer);
        FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("file", fileBody);
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        System.out.println("executing request " + post.getRequestLine());
        HttpResponse response = httpclient.execute(post);
        HttpEntity res = response.getEntity();
        int responseCode = response.getStatusLine().getStatusCode();
        System.out.println("Request Url: " + post.getURI());
        System.out.println("Response Code: " + responseCode);
        String filename = "temp.xlsx";
        String path = tempPath;
        InputStream in = res.getContent();
        Utility.saveFile(in, filename, path);
        File targetFile = new File(path + File.separatorChar + filename);
        return targetFile;
    }
    
    public static void showAlert(String title,String header,String msg) {
    Platform.runLater(new Runnable() {
      public void run() {
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle(title);
          alert.setHeaderText(header);
          alert.setContentText(msg);
          alert.showAndWait();
      }
    });
}
}
