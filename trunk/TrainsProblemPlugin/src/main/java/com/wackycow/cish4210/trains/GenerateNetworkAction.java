package com.wackycow.cish4210.trains;


import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import java.util.Random;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.CytoscapeProgressMonitor;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CyNodeView;
import giny.model.Node;

public class GenerateNetworkAction extends SelectedNodeAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 0L;

	
	GenerateNetworkAction() {
		super("Create MRTP Network");
	}

	@SuppressWarnings("deprecation")
    @Override
	public void doAction(List<Node> nodes, CyNetwork network, CyNetworkView view, CyAttributes attributes) {
		CyNetwork net = Cytoscape.createNetwork("MRTP Network", true);
		Cytoscape.setCurrentNetwork(net.getIdentifier());
        TrainsGraph graph = new TrainsGraph(net);
		createStations(graph);
		createTrainsAndRoutes(graph);
	}

	private void createStations(TrainsGraph graph) {
		int size = 30;
		int connections = 70;
		
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
	}
	
	private Set<String> createSet(String a, String b) {
	    Set<String> result = new HashSet<String>();
	    result.add(a);
	    result.add(b);
	    return result;
	}
	private void createTrainsAndRoutes(TrainsGraph graph) {
		
	}
	
	
}
