package com.wackycow.cish4210.trains;

import giny.model.Node;

import java.util.List;

import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;

public class RunSimulator extends SelectedNodeAction {

    public RunSimulator() {
        super("Run MRTP simulator");
    }

    @Override
    public void doAction(List<Node> nodes, CyNetwork network,
            CyNetworkView view, CyAttributes attributes) {
    }
    
    private void runSimulation(Dispatcher dispatcher) {
        // TODO: Implement the simulator.
        
    }

}
