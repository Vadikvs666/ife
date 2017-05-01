/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vadikvs.ife;

import java.util.Map;
import java.util.prefs.Preferences;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author vadim
 */
public class Settings {

    private StringProperty filePath;
    private StringProperty user;
    private StringProperty password;
    private Map<String,String> settings;

    public String getPassword() {
        return password.get();
    }

    public String getFilePath() {
        return filePath.get();
    }

    public String getUser() {
        return user.get();
    }

    public void setFilePath(String video) {
        filePath.set(video);
        save();
    }

    public void setPassword(String opt) {
        password.set(opt);
        save();
    }

    public void setUser(String ffmpeg) {
        user.set(ffmpeg);
        save();
    }

    public Settings() {
        init();
        load();
    }

    public void save() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        prefs.put("filePath", getFilePath());
        prefs.put("user", getUser());
        prefs.put("password", getPassword());
    }

    private Boolean load() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String file = prefs.get("filePath", null);
        String user = prefs.get("user", null);
        String pass = prefs.get("password", null);
        if (file != null && user != null && pass != null) {
            setUser(user);
            setFilePath(file);
            setPassword(pass);
            return true;
        } else {
            return false;
        }

    }
    
    public void reset(){
        init();
        save();
    }

    private void init() {
        filePath = new SimpleStringProperty("");
        user = new SimpleStringProperty("");
        password = new SimpleStringProperty("");
    }

}
