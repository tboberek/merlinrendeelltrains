package com.wackycow.cish4210.trains;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Vector;

public class BoberekCriticalIntersectionDispatcher extends
		BoberekCriticalPointDispatcher {

	HashMap<String, HashMap<String, Integer>> m_TrainCriticalPoints;
	Vector<String> m_CriticalTrains;
	
	@Override
	public void checkMoveTrain(TrainsGraph g, Train t, String stationId) {
		synchronized (m_Initialized) {
			if (!m_Initialized) {
				// Initialize our needed data structures
				m_TrainCriticalPoints = new HashMap<String, HashMap<String, Integer>> ();
				m_CriticalTrains = new Vector<String> ();
				
				// Setup the critical points hash table
				setTrainCriticalPointsHash (g.getTrains (), g.getEngineHouseId());
				
				// We're initialized.
				m_Initialized = true;
			}
		}
		
		// Now the critical point hash is definitely initialized.  Determine if 
		// this train can move
		try {
			determineTrainMove (g, t, stationId);	
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
		// Update the visit counter for the previous station.  Since the internal position for
		// the train has not been updated, we need to get what the train thinks is its current
		// position.
		updateCriticalPointLists (t.getId (), t.getCurrentStation ());
		
		// If we're a critical train, notify all the waiting trains that things have changed and that
		// they might be able to proceed.
		if (m_CriticalTrains.contains(t.getId ())) {
			// Check to see if we can remove this train from the critical trains list
			if (!getAllCriticalPoints ().contains (stationId)) {
				System.out.println ("|| Removing " + t.getId () + " from the critical list.");
				m_CriticalTrains.remove(t.getId ());
			}
			
			//System.out.println ("|| Train " + t.getId () + " sending notify.");
			notifyAll ();
		}	
	}
	
	synchronized private void determineTrainMove (TrainsGraph g, Train t, String stationId) throws Exception {
		// If we're not going on a critical point then we can move
		if (!getAllCriticalPoints ().contains(stationId)) {
			//System.out.println ("|| Train:" + t.getId () + " moving to non-critical point: " + stationId);
			return;
		}
		
		// If we're a critical train, we can try to move, but only if we're not going
		// to intersect the route of another critical train
		if (m_CriticalTrains.contains(t.getId ())) {
			// If we're going to intersect a higher-priority train...we have to wait...
			while (intersectingHigherPrioirityTrains (t.getId (), stationId)) {
				//System.out.println ("|| Critical Train " + t.getId () + " waiting for notify.");
				wait ();
			}
			
			//System.out.println ("|| Critical train:" + t.getId () + " moving to point: " + stationId);
			return;
		}
		
		// If we reach here, we know that we're a non-critical train moving to a critical point
		
		// We have to wait until our route does not intersect the critical points of
		// any active critical trains
		System.out.println ("|| Train " + t.getId () + " checking for intersections.");
		while (stationIntersectsActiveCriticalTrains (stationId, t.getId ())) {
			//System.out.println ("|| Train " + t.getId () + " waiting for notify.");
			wait ();
		}
		
		if (!m_CriticalTrains.contains (t.getId ())) {
			//System.out.println ("|| Adding: " + t.getId () + " to the critical train list.");
			m_CriticalTrains.add (t.getId ());
		}
		
	
	}
	
	private Boolean intersectingHigherPrioirityTrains (String trainId, String stationId) {
		Integer trainPriority = getTrainPriority (trainId);
		Boolean result = false;
		
			// Go through all the critical trains of a higher priority and check
		// to see if we intersect them at all.
		for (int i = 0; i < trainPriority; i++) {
			if (stationIntersectsCriticalPoints (stationId, m_CriticalTrains.get(i))) {
				result = true;
			}
		}
		
		return result;
	}
	
	private Integer getTrainPriority (String trainId) {
		Integer priority = 0;
		//System.out.println ("|| Looking for prioiryt of train: " + trainId);
		//System.out.println ("|| Critical train list size: " + m_CriticalTrains.size ());
		for (int i = 0; i < m_CriticalTrains.size (); i++) {
			if (m_CriticalTrains.get(i).equalsIgnoreCase(trainId)){
				priority = i;
				break;
			}
		}
		
		return priority;
	}
		
	private Boolean trainIntersectsActiveCriticalTrains (String trainId) {
		Boolean intersectionFound = false;
		
		Set<String> criticalPoints = m_TrainCriticalPoints.get(trainId).keySet ();
		
		for (String otherTrain : m_CriticalTrains){
			Set<String> otherCriticalPoints = m_TrainCriticalPoints.get(otherTrain).keySet ();
			
			for (String station : criticalPoints) {
				if (otherCriticalPoints.contains (station)) {
					intersectionFound = true;
					break;
				}	
			}
			
			if (intersectionFound) {
				break;
			}
		}
		
		return intersectionFound;
	}
		
	private Boolean stationIntersectsActiveCriticalTrains (String stationId, String trainId) {
		Boolean intersectionFound = false;
				
		// Go through the critical train list and check to see
		// if the current train's critical points and any active
		// trains critical points intersect
		for (String otherTrain : m_CriticalTrains) {
			if (otherTrain != trainId) {
				intersectionFound = stationIntersectsCriticalPoints (stationId, otherTrain);
			}
			
			if (intersectionFound) {
				break;
			}
		}
		
		return intersectionFound;
	}
	
	private Boolean stationIntersectsCriticalPoints (String stationId, String trainId) {
		Boolean intersectionFound = false;
		
		HashMap<String, Integer> criticalPoints = m_TrainCriticalPoints.get(trainId);
		
		// Loop through both lists looking for an intersection
		if (criticalPoints.keySet ().contains (stationId)) {
			//System.out.println ("|| Intersection found between station: " + stationId + " and critical train " + trainId);
			//System.out.println ("|| Train " + trainId + " route: " + criticalPoints + " :");
			intersectionFound = true;
		}
				
		return intersectionFound;
	}
	
	private void updateCriticalPointLists (String trainId, String stationId) {
		// Check to see if we're on a critical point and need to update
		if (m_TrainCriticalPoints.get(trainId).containsKey(stationId))
		{
			Integer visitCount = m_TrainCriticalPoints.get(trainId).get(stationId);
		
			// Decrement the visit count for this station
			visitCount--;
		
			// If the visit count is zero, then remove this station from this critical
			// points list
			if (visitCount < 1) {
				//System.out.println ("|| Removing station " + stationId + " from critical list for train " + trainId);
				m_TrainCriticalPoints.get (trainId).remove(stationId);
				//System.out.println ("|| Train " + trainId + " route: " + m_TrainCriticalPoints.get (trainId) + " :");
			}
		}
	}
	
	private void setTrainCriticalPointsHash (Map<String, Train> trainsList, 
			String engineHouseId) {
		// Set the list of critical points for the entire graph
		List<String> graphCriticalPoints = getCriticalPoints (trainsList, engineHouseId);
		
		// Now, for each train, go through and find the critical points that
		// the train passes.
		for (Train train : trainsList.values ()) {
			// Get the train route
			List<String> route = train.getRoute ();
			String id = train.getId ();
			
			// Add the train to the critical point hash
			m_TrainCriticalPoints.put (id, new HashMap<String, Integer> ());
			
			// Go through each critical point and see if it's on the train's
			// route
			for (String criticalPoint : graphCriticalPoints) {
				// If the route does contain the critical point, find out
				// how many times we visit that critical point and add it
				// to this trains critical point list.
				if (route.contains (criticalPoint)){
					// Get the number of times this train visits this critical point
					Integer visitCount = getVisitCount (criticalPoint, route);
					
					// Add the critical point data to the list
					m_TrainCriticalPoints.get (id).put(criticalPoint, visitCount);
				}
			}
		}
	}
	
	private List<String> getAllCriticalPoints () {
		List<String> allCriticalPoints = new ArrayList<String> ();
		
		for (String key : m_TrainCriticalPoints.keySet ()) {
			allCriticalPoints.addAll(m_TrainCriticalPoints.get(key).keySet ());
		}
		
		return allCriticalPoints;
	}
	
	private Integer getVisitCount (String criticalPoint, List<String> route) {
		Integer visitCount = 0;
		
		for (String station : route) {
			if (station.equalsIgnoreCase(criticalPoint)) {
				visitCount++;
			}
		}
		
		return visitCount;
	}
	
	
	@Override
	public String getName() {
		return "Boberek Critical Intersection Dispatcher";
	}
}
