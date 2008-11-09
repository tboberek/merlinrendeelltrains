package com.wackycow.cish4210.trains;

import giny.model.Edge;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.Semantics;

public class TrainsGraph {

    private CyNetwork network;
    private Dispatcher dispatcher;
    private Map<String,Train> trains = new HashMap<String,Train> ();
    
    private Map<String,Object> monitors = new HashMap<String,Object>();
    
    private synchronized Object getMonitor(String stationId) {
        if (!monitors.containsKey(stationId)) {
            monitors.put(stationId, new Object());
        }
        return monitors.get(stationId);
    }
    
    public Map<String, Train> getTrains() {
		return trains;
	}

	public TrainsGraph(CyNetwork network) {
        this.network = network;
    }
 
    public void addTrain(Train train) {
        trains.put(train.getId(), train);
        train.setGraph(this);
    }
    
    public void setDispatcher(Dispatcher disp) {
        this.dispatcher = disp;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }
    
    @SuppressWarnings({ "deprecation", "unchecked" })
    List<String> getNodeConnections(String stationId) {
    	List<String> result = new ArrayList<String>();
    	CyNode node = Cytoscape.getCyNode(stationId, true);
    	List<Edge> edges = (List<Edge>)network.getAdjacentEdgesList(node, true, 
    	                                                            true, true);
    	for (Edge edge : edges) {
    	    System.out.println(edge.getIdentifier());
    	    if (edge.getSource() == node) {
    	        System.out.println(edge.getTarget().getIdentifier());
    	        result.add(edge.getTarget().getIdentifier());
    	    } else if (edge.getTarget() == node) {
                System.out.println(edge.getSource().getIdentifier());
    	        result.add(edge.getSource().getIdentifier());
    	    }
    	}
		return result;
    }
    
    /** 
     * 
     * @param t Train to move from station to station.
     * @param stationId destination of the train.
     * @param trackId the track to travel over.
     */
    public void moveTrain(Train t, String stationId) {
        dispatcher.checkMoveTrain(this, t, stationId);
        doMoveTrain(t, stationId);
    }

    public void createStation(String stationId) {
        CyNode node = Cytoscape.getCyNode(stationId, true);
        network.addNode(node);
    }
    
    public void createGraph(Map<String,List<String>> graph) {
    	for (String node : graph.keySet()) {
    		for (String destination : graph.get(node)) {
        		createConnection(node,destination);
    		}
    	}
    }
    
    public void createConnection(String sourceId, String destinationId) {
        CyNode source = Cytoscape.getCyNode(sourceId, true);
        CyNode destination = Cytoscape.getCyNode(destinationId, true);
        CyEdge edge = Cytoscape.getCyEdge(source, destination, Semantics.INTERACTION, 
                                            "track", true);
        edge.setIdentifier(sourceId+"->"+destinationId);
        Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(),
                "source", sourceId);
        Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(),
                "destination", destinationId);
        System.out.println(edge.getIdentifier());
        network.addNode(source);
        network.addNode(destination);
        network.addEdge(edge);
        updateView();
    }
    
    public Train getTrainAtStation(String stationId) {
        String trainId = Cytoscape.getNodeAttributes().getStringAttribute(stationId, "train");
        return trains.get(trainId);
    }
    
    public String getEngineHouseId() {
        return "Engine_House";
    }
    
    private void updateView() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    // TODO Auto-generated method stub
                    Cytoscape.getCurrentNetworkView().updateView();
                }
            });
        } catch (InterruptedException e) {
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    private synchronized void doMoveTrain(final Train t, final String stationId) {
        System.out.println("Moving from "+t.getCurrentStation()+" to "+stationId);
        if (getTrainAtStation(stationId) != null && !(stationId == getEngineHouseId())) {
            Object mon = getMonitor(stationId);
            synchronized(mon) {
                try {
                    mon.wait();
                } catch (InterruptedException e1) {
                }
            }
        }
        Object mon = getMonitor(t.getCurrentStation());
        synchronized(mon) {
            mon.notify();
        }
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    Cytoscape.getNodeAttributes().setAttribute(stationId, 
                                                               "train",t.getId());
                    Cytoscape.getNodeAttributes().deleteAttribute(t.getCurrentStation(),
                    "train");
                    Cytoscape.getCurrentNetworkView().updateView();
                    Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
                }
            });
        } catch (InterruptedException e) {
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
}
