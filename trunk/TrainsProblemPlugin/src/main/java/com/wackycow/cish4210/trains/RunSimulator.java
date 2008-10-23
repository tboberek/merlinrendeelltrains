package com.wackycow.cish4210.trains;

import giny.model.Node;

import java.util.List;

import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;

public class RunSimulator extends SelectedNodeAction {

    public RunSimulator(String name) {
        super("Run simulator using the "+name +" algorithm.");
    }

    @Override
    public void doAction(List<Node> nodes, CyNetwork network,
            CyNetworkView view, CyAttributes attributes) {
        // TODO Auto-generated method stub

    }

}
