/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

/**
 *
 * @author vadim
 */
public class XLXSConverter {

    public static File convertFile(File file, String server) throws Exception {

        HttpClient httpclient = HttpClientBuilder.create().build();;
        HttpPost post = new HttpPost(server);
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
        System.out.println("Headers: " + response.getStatusLine().getReasonPhrase());
        String filename = "temp.xlsx";
        String path="temp";
        InputStream in = res.getContent();
        Utility.saveFile(in, filename, path);
        File targetFile = new File(path + File.separatorChar+filename);
        System.out.println("File Download Completed!!!");
        return targetFile;
    }

}
