package com.wackycow.cish4210.trains;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedList;

public class MoonDispatcher extends Dispatcher {
	private List<String> criticalNodes = null;
	private List<String> resourceAllocation = null;
	private Map<String,String> arcs = new HashMap<String, String>();
	
	@Override
	public void checkMoveTrain(TrainsGraph g, Train t, String stationId) {
        synchronized(this) {
        	if (this.resourceAllocation == null) 
        		this.resourceAllocation = new ArrayList<String>();

        	if (this.criticalNodes == null) 
            	this.criticalNodes = Collections.synchronizedList(this.getCriticalResources(g.getTrains(), g.getEngineHouseId()));
        }
        
		try {
			checkMoveTrain(t, stationId);	
		}
		catch (IllegalMonitorStateException ex) {
			System.out.println (ex.getMessage());
			ex.printStackTrace();
			
		}
		catch (Exception e) {
			// If we hit this, there was an unintended problem, just print the 
			// stack trace
			e.printStackTrace();
		}	
	}
	
	synchronized public void notifyMoveTrainComplete(TrainsGraph g, Train t, String stationId)
	{
		notify();
	}
	
	synchronized private void checkMoveTrain(Train t, String stationId) throws Exception {
		// Allow trains that are not going to a critical node to continue
		if (!criticalNodes.contains(stationId)){
			//Non-critical node - allow movement
			System.out.println (t.getId () + " is moving to " + stationId + "(non-criticalNode)");
			return;
		}
		
		this.arcs.put(t.getId(),stationId);
		
		//If the station is already allocated, wait
		while (!isSafe(t,stationId)) {
			System.out.println (t.getId () + " is waiting to move to: " + stationId);
			wait ();			
		}

		this.arcs.remove(t.getId());
		this.arcs.put(stationId,t.getId());
		
		String currentStation = t.getCurrentStation();
		return;
	}
	
	private boolean isSafe(Train t, String s)
	{
		System.out.println ("Resource Allocation:");
		System.out.println (this.resourceAllocation);
		System.out.println ("Arcs:");
		System.out.println (this.arcs);
		
		for (int i=0; i < this.resourceAllocation.size(); i++){
			List<String> l = new ArrayList<String>();
			l.add(this.resourceAllocation.get(i));
			if (this.hasCycles(this.resourceAllocation.get(i), l)){
				System.out.println ("Found Cycles");
				return false;
			}
		}
		
		return true;
	}
	
	private boolean hasCycles(String node, List<String> l)
	{
		if (this.arcs.containsKey(node)){
			String n = this.arcs.get(node);
			if (l.contains(n)){
				return true;
			}
			else{
				return hasCycles(n,l);
			}
		}
		return false;
	}
	
	private List<String> getCriticalResources (Map<String, Train> trains, String engineHouse) {
		List<String> criticalResources = new ArrayList<String>();
		List<String> resourceCounter = new ArrayList<String>();
		
		for (Train t : trains.values ()) {
			System.out.println (String.format("Route: %s", t.getId()));
			System.out.println (t.getRoute());
			
			this.resourceAllocation.add(t.getId());
			
			List<String> trainRoute = t.getRoute();
			List<String> distinctResource = new ArrayList<String>();
			
			for (int i=0; i < trainRoute.size(); i++){
				String station = trainRoute.get(i);
				if (!distinctResource.contains(station) && !trainRoute.get(i).equals(engineHouse))
				{
					distinctResource.add(station);
				}
			}
			
			for (int i=0; i < distinctResource.size(); i++){
				String station = distinctResource.get(i);
				if (!resourceCounter.contains(station))
				{
					resourceCounter.add(station);
				}
				else {
					if (!criticalResources.contains(station)){
						criticalResources.add(station);
					}						
				}
			}
		}
		
		System.out.println ("Critical Resources:");
		System.out.println (criticalResources);
		this.resourceAllocation.addAll(criticalResources);
		return criticalResources;
	}

	public void reset () {
        synchronized(this) {
            criticalNodes = null;
            resourceAllocation = null;
        }
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Moon Dispatcher";
	}

	@Override
	public void initialize() {
	}
}