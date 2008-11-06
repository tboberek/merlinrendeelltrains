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
	public void checkMoveTrain(TrainsGraph g, Train t, String stationId) {
		
		if (g.getTrainAtStation(stationId) != null) {
			try {
				getMonitor(stationId).wait();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		getMonitor(t.getCurrentStation()).notify();
	}

}
