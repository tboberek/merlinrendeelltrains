import java.awt.Color;

public class SimulationTester {
	public static void main(String[] args) {
		// Create a graphics system to display the trains moving
		GraphicsSystem graphicsSystem = new GraphicsSystem ();
		
		// Create a dispatcher to control the train movements and link
		// it up to the graphics system
		Dispatcher theDispatcher = new Dispatcher (graphicsSystem);
		
		// Create a series of test trains
		Train trainOne = setupTrainOne (theDispatcher);
		Train trainTwo = setupTrainTwo (theDispatcher);
		Train trainThree = setupTrainThree (theDispatcher);
		
		// Startup all the trains
		trainOne.start ();
		trainTwo.start ();
		trainThree.start ();
	}

	private static Train setupTrainOne (Dispatcher theDispatcher){
		Train train = new Train (theDispatcher, Color.RED);
		
		TrainRoute route = new TrainRoute ();
		route.add("B");
		route.add("D");
		route.add("B");
		route.add("D");
		route.add("B");
		route.add("D");
		route.add("B");
		route.add("D");
		
		train.setRoute(route);
		
		return train;
	}
	
	private static Train setupTrainTwo (Dispatcher theDispatcher){
		Train train = new Train (theDispatcher, Color.BLUE);
		
		TrainRoute route = new TrainRoute ();
		route.add("C");
		route.add("F");
		route.add("D");
		route.add("A");
		route.add("B");
		route.add("F");
		
		train.setRoute(route);
		
		return train;
		
	}
	
	private static Train setupTrainThree (Dispatcher theDispatcher){
		Train train = new Train (theDispatcher, Color.GREEN);
		
		TrainRoute route = new TrainRoute ();
		route.add("A");
		route.add("F");
		route.add("D");
		route.add("E");
		route.add("A");
		route.add("C");
		route.add("B");
		route.add("A");
		
		train.setRoute(route);
		
		return train;
	}
}

