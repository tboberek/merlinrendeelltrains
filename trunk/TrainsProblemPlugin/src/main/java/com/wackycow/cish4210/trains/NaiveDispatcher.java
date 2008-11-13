package com.wackycow.cish4210.trains;

import java.util.HashMap;
import java.util.Map;

public class NaiveDispatcher extends Dispatcher {

	private Map<String,Object> monitors = new HashMap<String,Object>();
	
	private synchronized Object getMonitor(String stationId) {
		if (!monitors.containsKey(stationId)) {
			monitors.put(stationId, new Object());
		}
		return monitors.get(stationId);
	}
	
	@Override
	public synchronized void checkMoveTrain(TrainsGraph g, Train t, String stationId) {
		// Has been moved to the TrainsGraph.doMoveTrain itself, and is therefore part of the world that a train has to wait for another train to move to that station.
	}

    @Override
    public String getName() {
        return "Naive Dispatcher";
    }

}
