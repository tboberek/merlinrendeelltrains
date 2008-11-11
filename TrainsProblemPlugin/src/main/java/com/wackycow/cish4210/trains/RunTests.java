package com.wackycow.cish4210.trains;

import giny.model.Node;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;

public class RunTests extends SelectedNodeAction {

    public RunTests() {
        super("Run MRTP Tests");
        
    }

    @Override
    public void doAction(List<Node> nodes, CyNetwork network,
            CyNetworkView view, CyAttributes attributes) {
        TrainsGraph graph = new TrainsGraph(network);
        graph.setDispatcher(new NaiveDispatcher());
        
        System.out.println("Testing TrainsGraph.getNodeConnections()");
        for (Node node : nodes) {
            List<String> connected = graph.getNodeConnections(node.getIdentifier());
            System.out.println(connected);
        }
        
        System.out.println("Testing single train run.");
        
        Train train = new Train();
        train.setGraph(graph);
        train.setId("Thomas");
        List<String> route = train.getRoute();
        route.add(graph.getEngineHouseId());
        for (Node node : nodes) {
            graph.createConnection(route.get(route.size()-1), 
                                   node.getIdentifier());
            route.add(node.getIdentifier());
        }
        graph.createConnection(route.get(route.size()-1), graph.getEngineHouseId());
        route.add(graph.getEngineHouseId());
        
        graph.addTrain(train);
        Thread t = new Thread(train);
        t.start();
    }   
}
