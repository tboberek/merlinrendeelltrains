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
		action.setPreferredMenu("Plugins");
		Cytoscape.getDesktop().getCyMenus().addAction(action);
        action = new RunSimulator();
        action.setPreferredMenu("Plugins");
        Cytoscape.getDesktop().getCyMenus().addAction(action);
        action = new RunTests();
        action.setPreferredMenu("Plugins");
        Cytoscape.getDesktop().getCyMenus().addAction(action);
	}
	
}
