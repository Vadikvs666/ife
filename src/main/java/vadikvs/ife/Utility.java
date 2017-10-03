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
import java.util.logging.Level;
import java.util.logging.Logger;

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
            try (BufferedInputStream in = new BufferedInputStream(stream)) {
                BufferedOutputStream out = null;
                byte[] buf = new byte[65535]; // буфер ввода/вывода
                while (in.read(buf) != -1) {
                    File folder;
                    folder = new File(path);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    out = new BufferedOutputStream(new FileOutputStream(path + File.separatorChar + filename));
                }
                out.write(buf);
                out.close();
            }
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
        System.out.println(String.valueOf((int)' '));
        for( int i=0;i<data.length();i++){
            char f=data.charAt(i);
            System.out.println(String.valueOf((int)f));
           // if(data.charAt(i)!=' '&&data.charAt(i)!=' ')
           if((int)data.charAt(i)!=160&&(int)data.charAt(i)!=32){
                res=res+data.charAt(i);
                System.out.println(res+"|");
            }
        }
        System.out.println(res);
        return res.replace(",", ".");
    }

}
