package com.wackycow.cish4210.trains;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * This class implements the Critical Points Dispatcher: Attempt 1 described
 * in 'A Simulator to Test Solutions to the Merlin-Randell Problem of Trains 
 * Scheduling'
 * 
 * @author T.J. Boberek
 *
 */
public class BoberekCriticalPointDispatcher extends Dispatcher {
	/**
	 * A flag to indicate if this dispatcher has been initialized or not.
	 */
	protected Boolean	m_Initialized = false;
	
	/**
	 * A flag that indicates if there is a critical train
	 */
	private Boolean				m_OnCriticalPoints = new Boolean (false);
	
	/**
	 * The ID of the current critical train (if any)
	 */
	private String				m_CriticalTrain = "";
	
	/**
	 * The list of critical points in the trains routes
	 */
	private ArrayList<String> 	m_CriticalPoints = new ArrayList<String> ();
	
	/**
	 * checkMoveTrain() is called whenever a Train attempts to move.  It first
	 * checks to see if this dispatcher has been initialized (and initializes the
	 * dispatcher if it has not been) and then calls determineTrainMove(), which
	 * will actually block or allow the train to proceed.
	 * 
	 * @param g			The TrainsGraph that is being traversed.
	 * @param t			The Train that is attempting to move.
	 * @param stationID	The ID of the station the Train is trying to reach.
	 */
	@Override
	public void checkMoveTrain(TrainsGraph g, Train t, String stationId) {
		// Synchronize on the initialized variable and check to see if we
		// need to initialize the critical point list.
		synchronized (m_Initialized) {
			if (!m_Initialized) {
				// Determine the routes and identify the necessary critical
				// points for all the trains in the graph
				m_CriticalPoints = getCriticalPoints (g.getTrains (), g.getEngineHouseId ());			
				m_Initialized = true;
			}
		}
		
		// Now the critical point list is definitely initialized.  Determine if 
		// this train can move
		try {
			determineTrainMove (t, stationId);	
		}
		catch (Exception e) {
			// If we hit this, there was an unintended problem, just print the 
			// stack trace
			e.printStackTrace();
		}	
	}
	
	/**
	 * notifyMoveTrainComplete () is called AFTER the train has successfully moved. It checks
	 * to see if the train has moved onto a non-critical point, and if so, removes the train's
	 * 'critical train' status. 
	 * 
	 * @param g			The TrainsGraph that is being traversed.
	 * @param t			The Train that is has moved.
	 * @param stationID	The ID of the station the Train has moved to.
	 */
	synchronized public void notifyMoveTrainComplete(TrainsGraph g, Train t, String stationId)
	{
		// Check to see if we're going to be moving off a critical point.  If we are,
		// then we should notify those who are waiting on a critical point
		if (m_OnCriticalPoints && t.getId () == m_CriticalTrain && 
				!m_CriticalPoints.contains(stationId)) {
			// We're no longer a critical train, so clear the critical train
			// string and set the critical train flag to false.
			m_OnCriticalPoints = false;
			m_CriticalTrain = "";
			
			// Notify anyone waiting to become a critical train.
			notifyAll ();
		}		
	}
	
	/**
	 * determineTrainMove () is called to actually control the movements of the
	 * train.  It will allow the train to move or force it to wait if needed.  
	 * 
	 * @param t			The Train that is attempting to move.
	 * @param stationID	The ID of the station the Train is trying to reach.
	 */
	synchronized private void determineTrainMove (Train t, String stationId) throws Exception {
		// Check to see if this train is moving to a critical point.  If it is not, then
		// just let it go about its business, since it can't bother any other
		// trains.
		if (! m_CriticalPoints.contains(stationId)) {
			return;
		}
		
		// If we get here, then the train is trying to move to a station that is on
		// the critical point list. 
		
		// While someone's on a critical point and it isn't this train,
		// wait until the current train is off the critical points.
		while (m_OnCriticalPoints && t.getId () != m_CriticalTrain) {
			wait ();
		}
		
		// If we're the critical train, then we can go on our merry way.
		if (m_OnCriticalPoints && t.getId () == m_CriticalTrain) {	
			return;
		}
		
		// At this point, we must be moving to a critical point, and we must not
		// be the critical train.  Set the critical points flag and set the ID 
		// of the critical train.
		m_OnCriticalPoints = true;
		m_CriticalTrain = t.getId ();

	}
	
	/**
	 * getCriticalPoints () examines the map the routes of the provided trains and creates
	 * a list of 'critical points': stations that are visited by more than one train.
	 * 
	 * @param trainsList	The list of trains to examine.
	 * @param engineHouseId	The ID of the engine house, since that is never a critical point.
	 * @return				A list of critical points between the provided trains.
	 */
	protected ArrayList<String> getCriticalPoints (Map<String, Train> trainsList, String engineHouseId) {
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

	/**
	 * reset() is called when the dispatcher needs to re-acquire the list of 
	 * critical points.
	 * 
	 */
	public void reset () {
		synchronized (m_Initialized) {
			m_Initialized = false;
		}
	}
	
	/**
	 * getName() returns the name of the current dispatcher.
	 * 
	 * @return The name of the current dispatcher.
	 */
	@Override
	public String getName() {
		return "Boberek Critical Point Dispatcher";
	}

	/**
	 * initialize() does nothing in this dispatcher.
	 */
	@Override
	public void initialize() {
	
	}

}
