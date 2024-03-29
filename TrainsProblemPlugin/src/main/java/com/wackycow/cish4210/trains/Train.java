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

    public void setRoute(List<String> newRoute) {
        route = newRoute;
    }

    private List<String> route = new ArrayList<String>();

    private int position = 0;

    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<String> getRoute() {
        return route;
    }

    public String getCurrentStation() {
        return route.get(position);
    }

    public String getNextStation() {
        if (position < route.size()) {
            return route.get(position+1);
        } else {return "XXXX";}
  
    }

    public String getNext2Station() {
        if (position < (route.size() - 2)) {
            return route.get(position+2);
        } else {return "XXXX";}
    }
    
    public String getPriorStation() {
        if (position > 0) {
           return route.get(position-1);
        } else {return "XXXX";}
    }
    
    
    public int getPosition() {
        return position;
    }

    public void run() {

        // Random generator = new Random();

        for (position = 0; position < route.size() - 1; ++position) {
            if (graph.isStopped()) {
                System.out.println("Stopping train "+getId());
                return;
            }
            // Sleep for some random time
            try {
                // Thread.sleep (Math.abs(generator.nextInt (2000)));
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

            // Debug output
            System.out.println("(" + getId() + ") requesting move " + "from "
                    + getCurrentStation() + " to " + getNextStation());

            // Attempt to move
            graph.moveTrain(this, getNextStation());
            System.out.println("(" + getId() + ") has moved " + "from "
                    + getCurrentStation() + " to " + getNextStation());
            
        }

    }

}
