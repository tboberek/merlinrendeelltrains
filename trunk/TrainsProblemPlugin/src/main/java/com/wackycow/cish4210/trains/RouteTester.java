package com.wackycow.cish4210.trains;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import cytoscape.CyNetwork;
import cytoscape.CyNetworkData;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.CyLayouts;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import giny.model.Node;

public class RouteTester extends SelectedNodeAction {

	private static final long serialVersionUID = 1L;

	public RouteTester () {
        super("Run MRTP Routing Tests");
    }
	
    @Override
    public void doAction(List<Node> nodes, CyNetwork network,
            CyNetworkView view, CyAttributes attributes) {

    	// Create our TrainsGraph, giving it a name and a dispatcher
    	TrainsGraph graph = new TrainsGraph ("MRTP Routing Test Graph", 
    			new NaiveDispatcher ());
 
    	// Create a train attribute, and set the visual style of the graph
    	// to display basedon that attribute.
    	attributes.setAttribute("train", "train", "");
    	graph.setVisualStyle ();
    	
    	// Add stations and connections to our graph
    	createStations (graph);
    	
    	// Run Tests on the graph
    	//runSingleTrainTest (graph);
    	
    	// Run multiple train test
    	runThreeTrainTest (graph);
    }
    
    private void runSingleTrainTest (TrainsGraph graph) {
    	// Create a new train
    	Train testTrain = new Train ();
    	
    	// Setup the train
    	setupTrain (testTrain, "Train 1", graph);
    	 	
    	// Make sure we're using the graph we just created
    	graph.setAsActiveGraph();
    	
    	// Attach the train to a thread
    	Thread trainThread = new Thread (testTrain);
    	
    	// Start the train thread
    	trainThread.start ();
    }
    
    private void runThreeTrainTest (TrainsGraph graph) {
    	// Create a new trains
    	Train testTrain1 = new Train ();
    	Train testTrain2 = new Train ();
    	Train testTrain3 = new Train ();
    	
    	// Setup the train
    	setupTrain (testTrain1, "Train001", graph);
    	setupTrain (testTrain2, "Train002", graph);
    	setupTrain (testTrain3, "Train003", graph);
    	 	
    	// Make sure we're using the graph we just created
    	graph.setAsActiveGraph();
    	
    	// Attach the train to a thread
    	Thread trainThread1 = new Thread (testTrain1);
    	Thread trainThread2 = new Thread (testTrain2);
    	Thread trainThread3 = new Thread (testTrain3);
    	
    	// Start the train thread
    	trainThread1.start ();
    	trainThread2.start ();
    	trainThread3.start ();
    }
    
    
   
    private void setupTrain (Train train, String trainID, TrainsGraph graph) {
		// Set the train ID
    	train.setId(trainID);
    	
    	// Attach the graph to the train.
		train.setGraph(graph);
		
		// Generate a new route and set the train to use the
		// route
		List<String> route = graph.generateTrainRoute();
		train.setRoute(route);
		
		// Output the route to a file for examination
		outputRouteToFile (route, "c:\\route-" + trainID + ".txt");

		// Add the train to the graph
		graph.addTrain (train);
    }
   

    private void outputRouteToFile (List<String> route, String filename) {
    	// Catch any exceptions that should occur
    	try {
    		// Create a new file
	    	Writer output = new BufferedWriter (new FileWriter (filename));
	    	
	    	// Print a route header
	    	output.write("Train Route Count:" + route.size() + "\n");
    		
	    	// Print out the route we're going to follow
	    	for (String id : route) {
    			output.write ("|" + id + "|");
    			output.write("\n");
    		}
    		
	    	// Close the file
    		output.close ();
    	}
    	catch (IOException e) {
			// Just dump a stack trace if we get an IO exception
			e.printStackTrace();
		}
    }
    
    //***************** Code stolen from GenerateNetworkAction.java *****************//
	private void createStations(TrainsGraph graph) {
		int size = 10;
		int connections = 30;
		
		Random r = new Random();
		List<String> stations = new ArrayList<String>();
		for (int i=0;i<size;++i) {
		    String name ="Station "+i; 
		    stations.add(name);
		    graph.createStation(name);
		}
        graph.createStation(graph.getEngineHouseId());
        stations.add(graph.getEngineHouseId());

        Set<Set<String>> created = new HashSet<Set<String>>();
        
        // How do we make sure there is at least 1 connection from the engine house?
        for (int i=0; i<connections; ++i) {
			String sourceid = stations.get(r.nextInt(stations.size()));
            String destid = stations.get(r.nextInt(stations.size()));
            while (destid.equals(sourceid) 
                    || created.contains(createSet(destid,sourceid))) {
                destid = stations.get(r.nextInt(stations.size()));
            }
            created.add(createSet(sourceid,destid));
			graph.createConnection(sourceid, destid);
		}
        
		Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
		CyLayouts.getLayout("hierarchical").doLayout();
	}
	
	private Set<String> createSet(String a, String b) {
	    Set<String> result = new HashSet<String>();
	    result.add(a);
	    result.add(b);
	    return result;
	}
}
