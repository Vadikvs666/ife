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
    private StringProperty server;
    private StringProperty serverMail;

    public String getServerMail() {
        return serverMail.get();
    }

    public void setServerMail(String serverMail) {
        this.serverMail.set(serverMail);
        save();
    }

    public String getProtocolMail() {
        return protocolMail.get();
    }

    public void setProtocolMail(String protocolMail) {
        this.protocolMail.set(protocolMail);
        save();
    }

    public String getUserMail() {
        return userMail.get();
    }

    public void setUserMail(String userMail) {
        this.userMail.set(userMail);
        save();
    }

    public String getUserPassword() {
        return userPassword.get();
    }

    public void setUserPassword(String userPassword) {
        this.userPassword.set(userPassword);
        save();
    }

    public String getPortMail() {
        return portMail.get();
    }

    public void setPortMail(String portMail) {
        this.portMail.set(portMail);
        save();
    }
    private StringProperty protocolMail;
    private StringProperty userMail;
    private StringProperty userPassword;
    private StringProperty portMail;
    private Map<String,String> settings;

    public String getPassword() {
        return password.get();
    }

    public String getServer() {
        return server.get();
    }

    public void setServer(String serv) {
        server.set(serv); 
        save();
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
        prefs.put("server", getServer());
        prefs.put("serverMail", getServerMail());
    }

    private Boolean load() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String file = prefs.get("filePath", null);
        String user = prefs.get("user", null);
        String pass = prefs.get("password", null);
        String server = prefs.get("server", null);
        if (file != null && user != null && pass != null&& server!=null) {
            setUser(user);
            setFilePath(file);
            setPassword(pass);
            setServer(server);
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
        server =new SimpleStringProperty("");
    }

}
