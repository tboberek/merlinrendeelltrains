package com.wackycow.cish4210.trains;

import java.util.HashMap;
import java.util.Map;

public class MoonDispatcher extends Dispatcher {

	@Override
	public synchronized void checkMoveTrain(TrainsGraph g, Train t, String stationId) {
		// Has been moved to the TrainsGraph.doMoveTrain itself, and is therefore part of the world that a train has to wait for another train to move to that station.
	}

	public void initialize() {
	    // This method left blank intentionally.
	}
	
    @Override
    public String getName() {
        return "Moon Dispatcher";
    }
}
