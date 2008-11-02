
/**
 * 
 * @author TJB
 *
 */
public class Dispatcher {
	
	private GraphicsSystem graphicsSystem;
	
	public Dispatcher (GraphicsSystem initialSystem){
		graphicsSystem = initialSystem;
	}
	
	/**
	 * Called when attempting to move a train to a new station.  This
	 * function will block until it is possible to move the train to 
	 * the requested station
	 * 
	 * @param train			The train requesting the move.
	 * @param destinationID	The ID of the station the train wishes to move to.
	 */
	public void requestMove (Train train, String destinationID) {
		graphicsSystem.moveTrain(train, destinationID);
		
		System.out.println (train.getId () + " (" + train.getColor () + 
				" requesting move to: " + destinationID);
	}
	
	/**
	 * This function is called when a train wishes to provide the 
	 * dispatcher with its route ahead of time.  This allows the
	 * dispatcher to examine the route to find the optimal timing
	 * of train movement.
	 * 
	 * @param trainID
	 * @param route
	 */
	public void provideRoute (long trainID, TrainRoute route) {
	
	}
    
}
