/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vadim
 */
public class BrowserLauncher {

    private String os;

    public BrowserLauncher() {
        String os_name = System.getProperty("os.name").toLowerCase();
        if (os_name.indexOf("win") >= 0) {
            this.os = "win";
        }
        if (os_name.indexOf("mac") >= 0) {
            this.os = "mac";
        }
        if (os_name.indexOf("nix") >= 0 || os_name.indexOf("nux") >= 0) {
            this.os = "nix";
        }
    }

    public void openBrowser(String url) {
        switch(os){
            case  "win":
                runWinBrowser(url);
                break;
            case "nix":
                runNixBrowser(url);
                break;
            case "mac":
                runMacBrowser(url);
                break;
            default:
                break;
        }
    }

    private void runWinBrowser(String url) {
        try {
            Runtime rt = Runtime.getRuntime();
            rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
        } catch (IOException ex) {
            Logger.getLogger(BrowserLauncher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void runMacBrowser(String url) {
        try {
            Runtime rt = Runtime.getRuntime();
            rt.exec("open " + url);
        } catch (IOException ex) {
            Logger.getLogger(BrowserLauncher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void runNixBrowser(String url) {
        try {
            Runtime rt = Runtime.getRuntime();
            StringBuffer cmd = new StringBuffer();
            cmd.append(String.format("%s \"%s\"", "google-chrome", url));
            rt.exec(new String[] { "sh", "-c", cmd.toString() });
        } catch (Exception ex) {
            Logger.getLogger(BrowserLauncher.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

}
