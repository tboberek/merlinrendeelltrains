package com.wackycow.cish4210.trains;

import giny.model.Edge;

import java.util.HashMap;
import java.util.Map;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributesUtils;

public class TrainsGraph {

    private CyNetwork network;
    private Dispatcher dispatcher;
    private Map<String,Train> trains = new HashMap<String,Train> ();
    

    public TrainsGraph(CyNetwork network) {
        this.network = network;
    }
 
    public void addTrain(Train train) {
        trains.put(train.getId(), train);
    }
    
    public void setDispatcher(Dispatcher disp) {
        this.dispatcher = disp;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }
    
    public void moveTrain(Train t, String stationId, String trackId) {
        if (dispatcher.checkMoveTrain(t, stationId, trackId)) {
            doMoveTrain(t, stationId, trackId);
        }
    }

    public boolean claimTrack(Train t, String trackId, int timeout) {
        return false;
    }
    
    public boolean claimStation(Train t, String stationId, int timeout) {
        return false;
    }

    public void createStation(String stationId) {
        
        CyNode node = Cytoscape.getCyNode(stationId, true);
        
    }
    
    public void createConnection(String sourceId, String destinationId) {
        CyNode source = Cytoscape.getCyNode(sourceId, true);
        CyNode destination = Cytoscape.getCyNode(destinationId, true);
        CyEdge edge = Cytoscape.getCyEdge(source, destination, "name", 
                                            sourceId+"_"+destinationId, true);
        edge.setIdentifier(sourceId+"_"+destinationId);
        Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(),
                                                   "type", "track");
    }
    
    public Train getTrainAtStation(String stationId) {
        String trainId = Cytoscape.getNodeAttributes().getStringAttribute(stationId, "train");
        return trains.get(trainId);
    }
    
    public String getEngineHouseId() {
        return "Engine_House";
    }
    
    private void doMoveTrain(Train t, String stationId, String trackId) {
    }
    
}
