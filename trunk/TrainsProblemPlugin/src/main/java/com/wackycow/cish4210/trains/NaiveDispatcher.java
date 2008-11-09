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
		
		if (g.getTrainAtStation(stationId) != null) {
            Object monitor = getMonitor(stationId);
            synchronized(monitor) {
                try {
                    monitor.wait();
                } catch (InterruptedException e1) {
                }
            }
		}
        Object monitor = getMonitor(t.getCurrentStation());
        synchronized(monitor) {
            monitor.notify();
        }
	}

    @Override
    public String getName() {
        return "Naive Dispatcher";
    }

}
