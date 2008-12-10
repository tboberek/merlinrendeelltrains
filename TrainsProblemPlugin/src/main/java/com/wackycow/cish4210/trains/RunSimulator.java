package com.wackycow.cish4210.trains;

import giny.model.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;


import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;

public class RunSimulator extends SelectedNodeAction {

	private Object[] dispatchers = new Object[] {
			new NaiveDispatcher(), new JoesDispatcher(),
                        new Joes2ndDispatcher(),
            new OneAtATimeSchedulingDispatcher(),
            new McCuskerDispatcher(),
            new BoberekCriticalPointDispatcher (),
            new BoberekCriticalIntersectionDispatcher (),
            new MoonDispatcher ()
	};
	
    public RunSimulator() {
        super("Run MRTP simulator");
    }

    private TrainsGraph lastGraph = null;
    private List<Thread> threads = new ArrayList<Thread>();
    
    @Override
    public void doAction(List<Node> nodes, CyNetwork network,
            CyNetworkView view, CyAttributes attributes) {
        if (lastGraph != null) {
            lastGraph.stop();
            for (Thread t : threads) {
                if (t.isAlive()) t.interrupt();
            }
            threads.clear();
        }
    	Dispatcher disp = (Dispatcher)JOptionPane.showInputDialog(Cytoscape.getDesktop().getRootPane(), 
    			"Select a Dispatcher", "Select Dispatcher", JOptionPane.PLAIN_MESSAGE, null, dispatchers, dispatchers[0]);
    	if (network.getClientData("TrainsGraph") != null) {
    		TrainsGraph graph = (TrainsGraph)network.getClientData("TrainsGraph");
    		graph.reset();
    		disp.reset ();
    		graph.setDispatcher(disp);
    		disp.initialize();
    		lastGraph = graph;
    		
    		for (Train train : graph.getTrains().values()) {
    			Thread t = new Thread(train);
    			t.start();
    			threads.add(t);
    		}
    	}
    }
}
