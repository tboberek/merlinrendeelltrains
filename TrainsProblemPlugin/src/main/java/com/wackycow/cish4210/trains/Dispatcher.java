package com.wackycow.cish4210.trains;

public abstract class Dispatcher {

    abstract public void checkMoveTrain(TrainsGraph g, Train t, String stationId);
    
    /**
     * Function to notify the dispatcher that the train move has been completed.
     * 
     * @param g			The graph the train is moving on
     * @param t			The train that has moved
     * @param stationId	The station to which the train moved
     */
    public void notifyMoveTrainComplete(TrainsGraph g, Train t, String stationId) {};
    
    /**
     * Function to notify the dispatcher that it should reset itself (the simulation is
     * going to run again).
     * 
     */
    public void reset () {};

    abstract public String getName();
    
    public String toString() {
        return getName();
    }

    abstract public void initialize();
    
}
