import java.awt.Color;
import java.util.Random;

public class Train extends Thread{

    private TrainRoute route = new TrainRoute();
    private int position = 0;
    private Dispatcher dispatcher;
    private Color color = Color.RED;
    
    public Train (Dispatcher initialDispatcher, Color initialColor) {
    	dispatcher = initialDispatcher;
    	route = generateRandomRoute ();
    	color = initialColor;
    }
    
    public Train (Dispatcher initialDispatcher, TrainRoute initialRoute){
    	dispatcher = initialDispatcher;
    	route = initialRoute;
    }
    
    public void run () {
    	Random generator = new Random ();
    	
    	// While we still have a route...
    	while (position < route.size () - 1)
    	{
    		// Sleep for some random time
			try {
				Thread.sleep (Math.abs(generator.nextInt (2000)));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		// Request that the dispatcher move us to the next
    		// station
    		dispatcher.requestMove(this, route.get(position + 1));
    		
    		// We're at the next position
    		position++;
    	}
    	
    }
    
    public void setRoute (TrainRoute newRoute) {
    	route = newRoute;
    }

    public TrainRoute getRoute() {
        return route;
    }

    public String getCurrentStation() {
        return route.get(position);
    }
    
    public String getNextStation() {
        return route.get(position+1);
    }

    public int getPosition() {
        return position;
    }
    
    private TrainRoute generateRandomRoute () {
    	TrainRoute randomRoute = new TrainRoute ();
    	
    	randomRoute.add("A");
    	randomRoute.add("B");
    	randomRoute.add("C");
    	
    	return randomRoute;
    }

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}


}
