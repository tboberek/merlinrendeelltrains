package com.wackycow.cish4210.trains;

import giny.model.Node;

import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;

public class RunSimulator extends SelectedNodeAction {

	private Object[] dispatchers = new Object[] {
			new NaiveDispatcher(),
	};
	
    public RunSimulator() {
        super("Run MRTP simulator");
    }

    @Override
    public void doAction(List<Node> nodes, CyNetwork network,
            CyNetworkView view, CyAttributes attributes) {
    	Dispatcher disp = (Dispatcher)JOptionPane.showInputDialog(Cytoscape.getDesktop().getRootPane(), 
    			"Select a Dispatcher", "Select Dispatcher", JOptionPane.PLAIN_MESSAGE, null, dispatchers, dispatchers[0]);
    	if (network.getClientData("TrainsGraph") != null) {
    		TrainsGraph graph = (TrainsGraph)network.getClientData("TrainsGraph");
    		graph.setDispatcher(disp);
    		for (Train train : graph.getTrains().values()) {
    			Thread t = new Thread(train);
    			t.start();
    		}
    	}
    }
}
