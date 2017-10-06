/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author vadim
 */
public class SettingList {

    private List<Setting> list;

    public String getValue(String name) {
        for (Setting set : list) {
            if (set.getName() == null ? name == null : set.getName().equals(name)) {
                return set.getValue();
            }
        }
        return null;
    }
    private static final Logger LOG = Logger.getLogger(SettingList.class.getName());

    public SettingList() {
        list = new ArrayList<Setting>();
    }

    public boolean add(Setting set) {
        for (Setting s : list) {
            if (s.getName() == set.getName()) {
                return false;
            }
        }
        return list.add(set);
    }

    public boolean exist(String name) {
        for (Setting s : list) {
            if (s.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    private Setting getSetting(String name){
        for (Setting s : list) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }
    
    public boolean setValue(String name , String value){
        Setting s= getSetting(name);
        if(s!=null){
            s.setValue(value);
            return true;
        }
        return false;
    }
    
    public List<Setting> getList(){
        return list;
    }
}
