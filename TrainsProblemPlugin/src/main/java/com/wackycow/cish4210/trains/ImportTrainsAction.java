package com.wackycow.cish4210.trains;


import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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

public class ImportTrainsAction extends SelectedNodeAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 0L;

	private JFileChooser chooser = new JFileChooser();
	
	
	ImportTrainsAction() {
		super("Import MRTP Trains Routes");
	}

	@SuppressWarnings("deprecation")
    @Override
	public void doAction(List<Node> nodes, CyNetwork network, CyNetworkView view, CyAttributes attributes) {
		if (chooser.showOpenDialog(Cytoscape.getDesktop().getRootPane()) == JFileChooser.APPROVE_OPTION) {
			File chosen = chooser.getSelectedFile();
			CyNetwork net = null;
			try {
				net = Cytoscape.createNetwork(chosen.getName(), true);
				
			} catch (Exception e) {
				net = Cytoscape.getCurrentNetwork();
			}
			Cytoscape.setCurrentNetwork(net.getIdentifier());
	        TrainsGraph graph = new TrainsGraph(net);
	        net.putClientData("TrainsGraph", graph);
			try {
				loadTrains(chosen, graph);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), 
						e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		} else {
			return;
		}
		
	}

	private void loadTrains(File chosen, TrainsGraph graph) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(chosen));
		String line = reader.readLine();
		while (line != null) {
			String[] values = line.split(",");
			if (values.length > 0) {
				System.err.println("Loading train "+line);
				Train t = new Train();
				t.setId(values[0]);
				List<String> route = t.getRoute();
				for (int i=1; i<values.length;++i) {
					route.add(values[i]);
				}
				graph.addTrain(t);
			}
			line = reader.readLine();
		}
		reader.close();
	}

}
