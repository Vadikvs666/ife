/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package com.vadikvs.Signalslots;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 *
 * @author vadim
 */
class SignalConnectorStruct {

    private Object executer;

    public Object getExecuter() {
        return executer;
    }

    public void setExecuter(Object executer) {
        this.executer = executer;
    }
    private Signal signal;
    private Slot slot;
    

    public void setSignal(Signal signal) {
        this.signal = signal;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public Signal getSignal() {
        return signal;
    }

    public Slot getSlot() {
        return slot;
    }
}

class Connections {

    private ArrayList<SignalConnectorStruct> connects;

    public void add(SignalConnectorStruct scs) {
        connects.add(scs);
    }

    public void remove(SignalConnectorStruct scs) {
        connects.remove(scs);
    }

    public ArrayList<SignalConnectorStruct> find(Signal signal) {
        ArrayList<SignalConnectorStruct> list = new ArrayList<>();
        for (SignalConnectorStruct entry : connects) {
            if ( entry.getSignal() == signal) {
                list.add(entry);
            }
        }
        return list;

    }

    public Connections() {
        connects = new ArrayList();
    }
}

public class SignalSlots {

    private static SignalSlots instance;
    private Connections connections = null;

    public static synchronized SignalSlots getInstance() {
        if (instance == null) {
            instance = new SignalSlots();

        }
        return instance;
    }

    public void connect(Signal signal,Object executer, Slot slot) {
        if (connections == null) {
            connections = new Connections();
        }
        SignalConnectorStruct connector = new SignalConnectorStruct();
        connector.setSignal(signal);
        connector.setExecuter(executer);
        connector.setSlot(slot);
        System.out.println("Connect signal:  with slot " + slot.get());
        connections.add(connector);
    }

    public void emit(Signal signal, Object[] args) {
        ArrayList<SignalConnectorStruct> conn = new ArrayList();
        if (connections != null) {
            conn = connections.find(signal);
            if (conn != null) {
                System.out.println("emit signal: " + signal.toString() + " goto slots" ); 
                for (SignalConnectorStruct entry : conn) {
                    System.out.println("emit signal: " + signal.toString() + " founded slot "+entry.getSlot().get() );
                    invokeSlot(entry.getSlot(),entry.getExecuter(), args);
                }
            }else{
               System.out.println("emit signal: " + signal.toString() + " no slot founds " ); 
            }
        }else{
            System.out.println("emit signal: " + signal.toString() + "No connects");
        }

    }

    private void invokeSlot( Slot slot,Object executer,Object[] args) {
        try {
            System.out.println("founded slot "+slot.get()+" begin invoke" );
            Task task = new Task() {
                @Override
                protected Object call()  {
                    try{
                        Class[] cArg = new Class[args.length];
                        int i=0;
                        for(Object arg : args){
                            cArg[i]=arg.getClass();
                            i++;
                        }
                        executer.getClass().getDeclaredMethod(slot.get(),cArg).setAccessible(true);
                        executer.getClass().getDeclaredMethod(slot.get(),cArg).invoke(executer, args);
                        String Msg="invoke slot " + slot.get();
                        System.out.println(Msg);
                    
                    }catch(Exception ex){
                        Logger.getLogger(SignalSlots.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   return null; 
                }
            };
            Platform.runLater(task);
        } catch (IllegalArgumentException | SecurityException ex) {
            Logger.getLogger(SignalSlots.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

 
  
}
