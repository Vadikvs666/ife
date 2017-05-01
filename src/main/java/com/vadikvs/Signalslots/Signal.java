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
public class Signal {
    private SignalSlots ss;
    private Object[] args=new Object[] {};

    public Object[] getArgs() {
        return args;
    }
    public Signal(){
        ss=SignalSlots.getInstance();
    }
    public void emit(){
        ss.emit(this,args);
    }
    public void emit(Object[] arg){
        ss.emit(this,arg);
    }
    public void connect(Slot slot){
        ss.connect(this, slot.getOwner(),slot);
    }
}
