package com.wackycow.cish4210.trains;

public abstract class Dispatcher {

    abstract public void checkMoveTrain(TrainsGraph g, Train t, String stationId);

    abstract public String getName();
    
    public String toString() {
        return getName();
    }

    abstract public void initialize();
    
}
