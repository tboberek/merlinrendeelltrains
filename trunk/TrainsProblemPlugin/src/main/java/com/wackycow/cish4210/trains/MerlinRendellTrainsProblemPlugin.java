package com.wackycow.cish4210.trains;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;

/**
 * Hello world!
 *
 */
public class MerlinRendellTrainsProblemPlugin extends CytoscapePlugin {
	public MerlinRendellTrainsProblemPlugin () {
		SelectedNodeAction action = new GenerateNetworkAction();
		action.setPreferredMenu("MRTP");
		Cytoscape.getDesktop().getCyMenus().addAction(action);
        action = new RunSimulator();
        action.setPreferredMenu("MRTP");
        Cytoscape.getDesktop().getCyMenus().addAction(action);
        action = new RunTests();
        action.setPreferredMenu("MRTP");
        Cytoscape.getDesktop().getCyMenus().addAction(action);
        action = new ImportTrainsAction();
        action.setPreferredMenu("MRTP");
        Cytoscape.getDesktop().getCyMenus().addAction(action);
        
        // Add the route testing plugin
        action = new RouteTester();
        action.setPreferredMenu("MRTP");
        Cytoscape.getDesktop().getCyMenus().addAction(action);
	}
	
}
