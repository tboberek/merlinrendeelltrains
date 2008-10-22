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
		getTaskMonitor().setPercentCompleted(0);
		int nodesCompleted = 0;
		for(Node node : nodes) {
			if (isCancelled()) break;
			if (node == null) continue;
			String id = node.getIdentifier();

			Object result = null;
			getTaskMonitor().setStatus("Looking up "+ id);
			
			if (isCancelled()) break;

			nodesCompleted++;
			updatePercentage(nodesCompleted, nodes.size());
		}
	}

	
}
