package com.wackycow.cish4210.trains;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class BoberekCriticalPointDispatcher extends Dispatcher {
	private Boolean m_Initialized = false;
	private Boolean m_OnCriticalPoints = new Boolean (false);
	
	private String	m_CriticalTrain = "";
	
	private ArrayList<String> m_CriticalPoints = new ArrayList<String> ();
	
	@Override
	public void checkMoveTrain(TrainsGraph g, Train t, String stationId) {
		// Synchronize on the initialized variable and check to see if we
		// need to initialize the critical point list.
		synchronized (m_Initialized) {
			if (!m_Initialized) {
				// Determine the routes and identify the necessary critical
				// points for all the trains in the graph
				m_CriticalPoints = getCriticalPoints (g.getTrains (), g.getEngineHouseId ());
				System.out.println ("---- Critical Points ----");
				System.out.println (m_CriticalPoints);
				System.out.println ("-------------------------");
				
				//
				for (Train train : g.getTrains ().values ())
				{
					System.out.println ("---- Path for: " + train.getId () + "----");
					System.out.println (train.getRoute ());
					System.out.println ("-------------------------");					
				}
				
				m_Initialized = true;
			}
		}
		
		// Now the critical point list is definitely initialized.  Determine if 
		// this train can move
		try {
			determineTrainMove (t, stationId);	
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
		// Check to see if we're going to be moving off a critical point.  If we are,
		// then we should notify those who are waiting on a critical point
		if (m_OnCriticalPoints && t.getId () == m_CriticalTrain && 
				!m_CriticalPoints.contains(stationId)) {
			System.out.println ("DEBUG: Train: " + t.getId () + " is moving off a critical point to: " + stationId);
			m_OnCriticalPoints = false;
			m_CriticalTrain = "";
			notify ();
		}
		else {
			System.out.println ("DEBUG: Train: " + t.getId () + " is moving to another critical point: " + stationId);	
		}		
	}
	
	synchronized private void determineTrainMove (Train t, String stationId) throws Exception {
		// Check to see if this train is on a critical point.  If it is not, then
		// just let it go about its business, since it can't bother any other
		// trains
		if (! m_CriticalPoints.contains(stationId) && t.getId () != m_CriticalTrain) {
			// The train is moving to a critical point, and is not the critical train,
			// so we can return immediately
			System.out.println ("DEBUG: Non-critical train:" + t.getId () + " moving to non-critical point: " + stationId);
			return;
		}
		
		// If we get here, then the train is trying to move to a station that is on
		// the critical point list. 
		// While someone's on a critical point and it isn't this train,
		// wait until the current train is off the critical points.
		while (m_OnCriticalPoints && t.getId () != m_CriticalTrain) {
			// If we're on the critical points, and this train is not the 
			// train currently on a critical point, force this train to wait
			// on the 
			System.out.println ("DEBUG!!!!!: Train: " + t.getId () + " is waiting to move to: " + stationId);
			wait ();
			System.out.println ("DEBUG: Train: " + t.getId () + " is able to move to: " + stationId);
		}
		
		// If we're the train on the critical points, then we can go on our
		// merry way.
		if (m_OnCriticalPoints && t.getId () == m_CriticalTrain) {	
			return;
		}
		else {
			// At this point, we must be moving to a critical point, and we must not
			// be in the middle of a critical point move.  Set the critical points
			// flag and set the ID of the critical train.
			System.out.println ("DEBUG: Train: " + t.getId () + " is moving to a critical point: " + stationId);
			m_OnCriticalPoints = true;
			m_CriticalTrain = t.getId ();
		}
	}
	
	public ArrayList<String> getCriticalPoints (Map<String, Train> trainsList, String engineHouseId) {
		// This map will count the number of times a station is visited
		Map<String, Integer> stationCount = new HashMap<String, Integer> ();
		
		// This list will contain the "critical points" -- stations visited
		// by more than one train.
		ArrayList<String> criticalPoints = new ArrayList<String> ();
		
		// Go through each train on the graph
		for (Train train : trainsList.values ()) {
			// Get the route for this train
			List<String> route = train.getRoute ();
			
			// Create a hashset that will be used to eliminate duplicates
			// in the route
			HashSet<String> routeSet = new HashSet<String> ();
			
			// Eliminate any duplicates in the current route by copying it
			// into a hash set (we don't want to count stations visited twice
			// by a single train as critical points).  Also, remove the engine
			// house from the critical point list at this time
			for (String stationId : route) {
				if (stationId != engineHouseId && 
					stationId.compareToIgnoreCase("engine_house") != 0) {
					routeSet.add(stationId);
				}
			}
			
			// Go through each station on the graph, either adding
			// it to the map of stations, or incrementing the number
			// of trains that visit the station.
			for (String stationId : routeSet) {
				if (stationCount.containsKey((stationId))) {
					// We've seen this station before, so increment the
					// visit count
					Integer count = stationCount.get(stationId) + 1;
					stationCount.put(stationId, count);
				}
				else {
					// We haven't seen this station before, add it to the
					// count list
					stationCount.put(stationId, 1);
				}
			}
		}
		
		// Go through the generated map and get any stations that are visited
		// more than once.
		for (String stationId : stationCount.keySet()) {
			// Add the station to our critical points list iff
			// the station is visited by more than one train
			if (stationCount.get(stationId) > 1){
				criticalPoints.add (stationId);
			}
		}
		
		return criticalPoints;
	}

	public void reset () {
		synchronized (m_Initialized) {
			m_Initialized = false;
		}
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Boberek Critical Point Dispatcher";
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

}
