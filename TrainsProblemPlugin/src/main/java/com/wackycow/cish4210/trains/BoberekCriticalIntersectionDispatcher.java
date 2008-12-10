package com.wackycow.cish4210.trains;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * This class implements the Critical Points Dispatchers: Attempt 4 described
 * in 'A Simulator to Test Solutions to the Merlin-Randell Problem of Trains 
 * Scheduling'
 * 
 * @author T.J. Boberek
 *
 */
public class BoberekCriticalIntersectionDispatcher extends
		BoberekCriticalPointDispatcher {

	/**
	 * The HashMap with the train IDs as keys, and a HashMap listing
	 * the critical points and the number of train visits as the value.
	 */
	HashMap<String, HashMap<String, Integer>> m_TrainCriticalPoints;
	
	/**
	 * A list of the IDs of the current critical trains.
	 */
	Vector<String> m_CriticalTrains;
	
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
		// need to initialize the critical intersection data.
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
		catch (Exception e) {
			// If we hit this, there was an unintended problem, just print the 
			// stack trace
			e.printStackTrace();
		}	
		
	}
	
	/**
	 * notifyMoveTrainComplete () is called AFTER the train has successfully moved. It updates 
	 * the visit count of the trains critical point list, checks to see if the train should be
	 * removed from the critical point list, and notifies any waiting trains that important 
	 * data may have changed.
	 * 
	 * @param g			The TrainsGraph that is being traversed.
	 * @param t			The Train that is has moved.
	 * @param stationID	The ID of the station the Train has moved to.
	 */
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
				m_CriticalTrains.remove(t.getId ());
			}
			
			// We're a critical train, so our movements could have affected other trains.
			// Notify any waiting trains of the potential situation change.
			notifyAll ();
		}	
	}
	
	/**
	 * determineTrainMove () is called to actually control the movements of the
	 * train.  It will allow the train to move or force it to wait if needed.  
	 * 
	 * @param g			The TrainsGraph that is being traversed.
	 * @param t			The Train that is attempting to move.
	 * @param stationID	The ID of the station the Train is trying to reach.
	 */
	synchronized private void determineTrainMove (TrainsGraph g, Train t, String stationId) throws Exception {
		// If we're not going on a critical point then we can move
		if (!getAllCriticalPoints ().contains(stationId)) {
			return;
		}
		
		// If we're a critical train, we can try to move, but only if we're not going
		// to intersect the route of higher-priority critical train.
		if (m_CriticalTrains.contains(t.getId ())) {
			// If we're going to intersect a higher-priority train we have to wait.
			while (intersectingHigherPrioirityTrains (t.getId (), stationId)) {
				wait ();
			}
			
			// We're not intersecting any higher priority trains, we can move
			return;
		}
		
		// We have to wait until our route does not intersect the critical points of
		// any active critical trains
		while (stationIntersectsActiveCriticalTrains (stationId, t.getId ())) {
			wait ();
		}
		
		// Once we're not intersecting any critical trains, we can become a critical 
		// train ourselves.  Then we can move to our next station.
		if (!m_CriticalTrains.contains (t.getId ())) {
			m_CriticalTrains.add (t.getId ());
		}
	}
	
	/**
	 * intersectingHigherPriorityTrains() takes the given train ID and station ID and 
	 * determines if this train is going to intersect the route of a higher-priority
	 * critical train if it moves to the station.
	 * 
	 * @param trainId	The ID of the train trying to move.
	 * @param stationId	The ID of the station the train is moving to.
	 * @return			true if the train will intersect a higher-priority train, false
	 * 					otherwise.
	 */
	private Boolean intersectingHigherPrioirityTrains (String trainId, String stationId) {
		// Get the priority of the train attempting to move.
		Integer trainPriority = getTrainPriority (trainId);
		
		// Default to assuming we're not intersecting anyone.
		Boolean result = false;
		
		// Go through all the critical trains of a higher priority and check
		// to see if we intersect them at all.
		for (int i = 0; i < trainPriority; i++) {
			if (stationIntersectsCriticalPoints (stationId, m_CriticalTrains.get(i))) {
				// If we find an intersection, set our return value and
				// break immediately.
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * getTrainPriority() returns the priority of a train given its ID (this assumes
	 * that the train is on the critical train list).
	 * 
	 * @param trainId	The ID of the train the caller wants the priority of.
	 * @return			The integer priority of the train.
	 */
	private Integer getTrainPriority (String trainId) {
		Integer priority = 0;
		
		// Iterate through the critical train list
		for (int i = 0; i < m_CriticalTrains.size (); i++) {
			// When we find the critical train that matches, we have
			// the priority
			if (m_CriticalTrains.get(i).equalsIgnoreCase(trainId)){
				// Assign the priority to our return value and break
				priority = i;
				break;
			}
		}
		
		return priority;
	}
	
	/**
	 * stationIntersectsActiveCriticalTrains() returns true if the provided station is on
	 * the critical points of any train except the train with the ID passed in as the
	 * second parameter.
	 * 
	 * @param stationID		The station to look for on any critical trains critical point 
	 * 						lists.
	 * @param trainID		The train to ignore during this search
	 * @return				true if the station is on any critical trains critical point 
	 * 						lists, false otherwise.
	 */
	private Boolean stationIntersectsActiveCriticalTrains (String stationId, String trainId) {
		Boolean intersectionFound = false;
				
		// Go through the critical train list and check to see
		// if the current train's critical points and any active
		// trains critical points intersect
		for (String otherTrain : m_CriticalTrains) {
			// Ignore any critical trains with the ID passed in.
			if (otherTrain != trainId) {
				// Check to see if there is an intersection between this station and
				// the current 'otherTrain'
				intersectionFound = stationIntersectsCriticalPoints (stationId, otherTrain);
			}
			
			// If an intersection was found, we done, so break.
			if (intersectionFound) {
				break;
			}
		}
		
		return intersectionFound;
	}
	
	/**
	 * stationIntersectsCriticalPoints() returns true if the given station is on the 
	 * critical point list of the given train.
	 * 
	 * @param stationId		The station to check for.
	 * @param trainId		The train to check the critical point list of.
	 * @return
	 */
	private Boolean stationIntersectsCriticalPoints (String stationId, String trainId) {
		Boolean intersectionFound = false;
		
		// Get the critical points of the train.
		HashMap<String, Integer> criticalPoints = m_TrainCriticalPoints.get(trainId);
		
		// Check to see if the critical point list contains the provided stationID
		if (criticalPoints.keySet ().contains (stationId)) {
			intersectionFound = true;
		}
				
		return intersectionFound;
	}
	
	/**
	 * updateCriticalPointLists() updates the point list of the given train after is has
	 * left the given station.
	 * 
	 * @param trainId		The ID of the train to modify.
	 * @param stationId		The ID of the station we just left.
	 */
	private void updateCriticalPointLists (String trainId, String stationId) {
		// Check to see if we moved off a critical point and need to update
		if (m_TrainCriticalPoints.get(trainId).containsKey(stationId))
		{
			// Get the current visit count to this station
			Integer visitCount = m_TrainCriticalPoints.get(trainId).get(stationId);
		
			// Decrement the visit count for this station
			visitCount--;
			
			// If the visit count is zero, then remove this station from this critical
			// points list, otherwise update the visit count.
			if (visitCount < 1) {
				m_TrainCriticalPoints.get (trainId).remove(stationId);
			}
			else {
				// Update the critical point list with the new visit count.
				m_TrainCriticalPoints.get(trainId).put(stationId, visitCount);
			}
				
		}
	}
	
	/**
	 * setTrainCriticalPointsHash() sets up the critical points hash table for the
	 * trains.
	 * 
	 * @param trainsList		The list of trains to parse for critical points.
	 * @param engineHouseId		The engine house ID (which is never a critical point).
	 */
	private void setTrainCriticalPointsHash (Map<String, Train> trainsList, String engineHouseId) {
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
	
	/**
	 * getAllCriticalPoints() returns a list of all the critical points of the
	 * current critical trains.
	 * 
	 * @return	A list with all the critical points.  Some critical points may be
	 * 			listed multiple times.
	 */
	private List<String> getAllCriticalPoints () {
		List<String> allCriticalPoints = new ArrayList<String> ();
		
		// For each critical train, get a list of the criticl points and
		// add it to our final list.
		for (String key : m_TrainCriticalPoints.keySet ()) {
			allCriticalPoints.addAll(m_TrainCriticalPoints.get(key).keySet ());
		}
		
		return allCriticalPoints;
	}
	
	/**
	 * getVisitCount () returns the number of times the given critical point
	 * is listed in the provided route.
	 * 
	 * @param criticalPoint		The critical point to search on.
	 * @param route				The route to search.
	 * @return					The number of times criticalPoint appears in
	 * 							route.
	 */
	private Integer getVisitCount (String criticalPoint, List<String> route) {
		Integer visitCount = 0;
		
		for (String station : route) {
			if (station.equalsIgnoreCase(criticalPoint)) {
				visitCount++;
			}
		}
		
		return visitCount;
	}
	
	/**
	 * getName() returns the name of the current dispatcher.
	 * 
	 * @return The name of the current dispatcher.
	 */
	@Override
	public String getName() {
		return "Boberek Critical Intersection Dispatcher";
	}
}
