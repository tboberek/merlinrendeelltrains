package com.wackycow.cish4210.trains;

import giny.model.Edge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    List<String> getNodeConnections(String stationId) {
    	List<String> result = new ArrayList<String>();
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

    public boolean claimTrack(Train t, String trackId, int timeout) {
        return false;
    }
    
    public boolean claimStation(Train t, String stationId, int timeout) {
        return false;
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
    }
    
    public Train getTrainAtStation(String stationId) {
        String trainId = Cytoscape.getNodeAttributes().getStringAttribute(stationId, "train");
        return trains.get(trainId);
    }
    
    public String getEngineHouseId() {
        return "Engine_House";
    }
    
    private void doMoveTrain(Train t, String stationId) {
    }
    
}
