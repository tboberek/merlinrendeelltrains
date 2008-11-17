package com.wackycow.cish4210.trains;

import giny.model.Edge;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.SwingUtilities;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.Semantics;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

public class TrainsGraph {

    private CyNetwork network;
    private Dispatcher dispatcher;
    private Map<String,Train> trains = new HashMap<String,Train> ();
    
    private Map<String,Object> monitors = new HashMap<String,Object>();

	public TrainsGraph(CyNetwork network) {
        this.network = network;
    }
	
	/**
	 * Generates a Cytoscape network and attaches to it.  
	 * 
	 * @param id			The id to use for the network
	 * @param Dispatcher	The dispatcher to use to schedule the trains
	 */
	@SuppressWarnings("deprecation")
	public TrainsGraph (String id, Dispatcher newDispatcher) {
		// Create the new cytoscape network
		network = Cytoscape.createNetwork(id, true);
		dispatcher = newDispatcher;
		
		// Attach this graph to the network we just created
		// The putClientData function is depreciated, but I 
		// couldn't figure out the correct replacement
		network.putClientData("TrainsGraph", this);
	}
    
    private synchronized Object getMonitor(String stationId) {
        if (!monitors.containsKey(stationId)) {
            monitors.put(stationId, new Object());
        }
        return monitors.get(stationId);
    }
    
    public Map<String, Train> getTrains() {
		return trains;
	}
 
    public void addTrain(Train train) {
        trains.put(train.getId(), train);
        train.setGraph(this);
        String lastStation = null;
        for (String station : train.getRoute()) {
            if (lastStation != null) {
                createConnection(lastStation, station);
            }
            lastStation = station;
        }
    }
    
    public void setDispatcher(Dispatcher disp) {
        this.dispatcher = disp;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }
    
    /**
     * Returns the id of the network backing this graph.
     * 
     * @return	The id of the network backing the graph.
     */
    public String getId () {
    	return network.getIdentifier ();
    }
    
    @SuppressWarnings({ "deprecation", "unchecked" })
    List<String> getNodeConnections(String stationId) {
    	List<String> result = new ArrayList<String>();
    	CyNode node = Cytoscape.getCyNode(stationId, true);
    	List<Edge> edges = (List<Edge>)network.getAdjacentEdgesList(node, true, 
    	                                                            true, true);
    	if (edges == null) return result;
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
    
    public void setAsActiveGraph () {
    	Cytoscape.firePropertyChange (CytoscapeDesktop.NETWORK_VIEW_FOCUS, null,
    			network.getIdentifier ());
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
        if (getNodeConnections(sourceId).contains(destinationId) 
                || getNodeConnections(destinationId).contains(sourceId))
            return;
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
        Cytoscape.getNodeAttributes().setAttribute(stationId,"train",t.getId());
        Cytoscape.getNodeAttributes().deleteAttribute(t.getCurrentStation(),"train");
        Cytoscape.getCurrentNetworkView().updateView();
        Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
        
    }
    
    /**
     * Returns the total number of nodes in the graph
     * 
     * @return The total number of nodes in the graph
     */
    public Integer getStationCount () {
    	return network.getNodeCount ();
    }
      
    /**
     * Returns a randomly generated TrainRoute that is valid in the current
     * graph.  The TrainRoute starts and ends at the engine house
     * 
     * @return			A TrainRoute valid on this graph
     */
    public List<String> generateTrainRoute () {
    	// Create an empty train route
    	List<String> newRoute = new ArrayList<String> ();
    	
    	// Create a random number generator
    	Random random = new Random ();
    	
    	// Get the total number of nodes in this graph.
    	Integer minSpan = random.nextInt (getStationCount ()) + 1;

    	// All trains start out at the engine house
    	String currentNode = getEngineHouseId ();
    	   	
    	// While we either have not visited enough stations or are
    	// not back at the engine house, continue
    	while (0 < minSpan-- || getEngineHouseId () != currentNode)
    	{
    		// Add the current node to our route
    		newRoute.add(currentNode);
    		
    		// Get the list of connections from the current station
    		List<String> destinations = getNodeConnections (currentNode);
    		
    		// Try to guess a good next destination.  If we have not visited
    		// enough stations, then the engine house is not a valid destination
    		do {
    			// Randomly select a new destination
    			Integer destinationGuess = random.nextInt (destinations.size ());
    		
    			// Select a new destination at random.
    			currentNode = destinations.get (destinationGuess);
    		} while (minSpan > 0 && getEngineHouseId () == currentNode);
    	}
    	
    	// Our last node should be the engine house, too
    	newRoute.add (getEngineHouseId ());
    	
    	return newRoute;
    }
    
    /**
     * Sets the visual style of the graph so that the train system displays
     * correctly.  The Train attribute must be a part of the nodes.
     */
    public void setVisualStyle () {
    	// Create a node label visual property
    	VisualPropertyType nodeProp = VisualPropertyType.NODE_LABEL;
    	
    	// Get the default value for the label in the current style
    	Object labelDefaultValue = 
    		nodeProp.getDefault(Cytoscape.getVisualMappingManager ().getVisualStyle ());
    	
    	// Create a new pass-through mapping for the node.
    	PassThroughMapping pm = 
    		new PassThroughMapping (labelDefaultValue, ObjectMapping.NODE_MAPPING);
    	
    	// Set the controlling attribute for the node label to "train" (it had better
    	// exist...)
    	pm.setControllingAttributeName ("train", network, false);
    	
    	// Create a calculator based on these settings
    	BasicCalculator calc = new BasicCalculator ("Train Calc", pm, VisualPropertyType.NODE_LABEL);
    	
    	// Set the node appearance calculator to the newly-created calculator
    	Cytoscape.getVisualMappingManager ().getVisualStyle ().getNodeAppearanceCalculator ().setCalculator(calc);
    }
    
}
