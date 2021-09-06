/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vadikvs.ife;

import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import com.vadikvs.Signalslots.Signal;

/**
 *
 * @author vadim
 */
public class Settings {


    private SettingList settings;
    public Signal changed = new Signal();
    

    public Settings() {
        init();
        load();
    }

    public void save() {
        
    }

    private Boolean load() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        for(Setting s : settings.getList()){
            String value =prefs.get(s.getName(), "");
            if(value!=""){
                s.setValue(value);
            }
            else{
                prefs.put(s.getName(),s.getValue());
            }
        }
        return true;

    }

    public void reset() {
        init();
        save();
    }

    public String getValue(String name) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        return prefs.get(name, "");
    }

    public boolean setValue(String name, String value) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (settings.setValue(name, value)) {
            prefs.put(name, value);
            changed.emit();
            return true;
        }
        return false;
    }
    
    public List<Setting>  getSettings(){
        return settings.getList();
    }

    private void init() {
        settings = new SettingList();
        Setting db = new Setting("database", "База данных: ", "");
        settings.add(db);
        Setting sever = new Setting("server", "Сервер: ", "");
        settings.add(sever);
        Setting user = new Setting("user", "Имя пользователя: ", "");
        settings.add(user);
        Setting pass = new Setting("password", "Пароль: ", "");
        settings.add(pass);
        Setting protokol = new Setting("protocol", "Почтовый протокол: ", "imap");
        settings.add(protokol);
        Setting serverMAil = new Setting("serverMail", "Почтовый сервер: ", "");
        settings.add(serverMAil);   
        Setting userMAil = new Setting("userMail", "Имя пользователя: ", "");
        settings.add(userMAil); 
        Setting portMAil = new Setting("portMail", "Порт: ", "993");
        settings.add(portMAil);
        Setting passMAil = new Setting("passMail", "Пароль: ", "");
        settings.add(passMAil); 
        Setting countMAil = new Setting("countMail", "Количество сообщений: ", "100");
        settings.add(countMAil);
        Setting  tempPath= new Setting("tempPath", "Временныя папка: ", "temp");
        settings.add(tempPath);
        Setting  browser= new Setting("browser", "Путь к браузеру: ", "firefox");
        settings.add(browser);
        Setting  addtition= new Setting("addition", "Наценка: ", "30");
        settings.add(addtition);
        Setting  converterServer= new Setting("converterServer", "сервер конвертирования файлов: ", "http://convert.hoz.center/converttoxlxs");
        settings.add(converterServer);

    }

}
