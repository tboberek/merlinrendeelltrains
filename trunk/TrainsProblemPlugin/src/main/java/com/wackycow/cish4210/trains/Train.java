package com.wackycow.cish4210.trains;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Train implements Runnable {

	// TODO: Create Route
	
	private TrainsGraph graph;
	
    public TrainsGraph getGraph() {
		return graph;
	}

	public void setGraph(TrainsGraph graph) {
		this.graph = graph;
	}

	private List<String> route = new ArrayList<String>();
    
    private int position = 0;
    
    private String id;

    public String getId() {
        // TODO Auto-generated method stub
        return id;
    }

    public List<String> getRoute() {
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

	public void run() {

		Random generator = new Random();
		for (position = 0; position < route.size(); ++position) {
    		// Sleep for some random time
			try {
				Thread.sleep (Math.abs(generator.nextInt (2000)));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			graph.moveTrain(this, getNextStation());
		}
		
	}
    
    
}
