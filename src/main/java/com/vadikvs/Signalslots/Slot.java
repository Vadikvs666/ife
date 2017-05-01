/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vadikvs.Signalslots;



/**
 *
 * @author vadim
 */
public class Slot {
    private String func;
    private SignalSlots ss= SignalSlots.getInstance();
    private Object owner;

    public Object getOwner() {
        return owner;
    }

    public Slot(Object owner,String func){
        this.func=func;
        this.owner=owner;
    }
    public String get(){
        return func;
    }

    public void connect(Signal signal){
        ss.connect(signal, owner,this);
    }
    
   
}
