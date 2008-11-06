package com.wackycow.cish4210.trains;


import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import java.util.Random;

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
		super("Generate MRTP Network");
	}

	@Override
	public void doAction(List<Node> nodes, CyNetwork network, CyNetworkView view, CyAttributes attributes) {
		TrainsGraph graph = new TrainsGraph(network);
		
		getTaskMonitor().setPercentCompleted(0);
		int nodesCompleted = 0;
		createStations(graph);
		createTrainsAndRoutes(graph);
		
	}

	private void createStations(TrainsGraph graph) {
		int size = 10;

		List<String> stations  = new ArrayList<String>();
		for (int i=0; i<size; ++i) {
			String id = "Station "+i;
			System.out.println(id);
			stations.add(id);
			graph.createStation(id);
			getTaskMonitor().setPercentCompleted((int)(((double)i)/size* 100));
		}

		graph.createStation(graph.getEngineHouseId());
		
	}
	
	private void createTrainsAndRoutes(TrainsGraph graph) {
		
	}
	
	
}
