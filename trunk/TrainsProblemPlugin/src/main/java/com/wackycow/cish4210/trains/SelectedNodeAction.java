package com.wackycow.cish4210.trains;

import giny.model.Node;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import ding.view.DGraphView;
import ding.view.ViewChangeEdit;


public abstract class SelectedNodeAction extends CytoscapeAction implements Task {

	protected boolean cancelled = false;
	protected TaskMonitor taskMonitor; 


	public SelectedNodeAction(String name) {
		super(name);
	}

	public static List<Node> getNodes() {
		CyNetwork network = Cytoscape.getCurrentNetwork();
	
		List<Node> nodes = new LinkedList<Node>(network.getSelectedNodes());
		
		if (nodes.size() == 0) {
			for(int i=0; i < network.getNodeCount(); ++i) {
				nodes.add(network.getNode(i));
			}
		}
	
		return nodes;
	}

	abstract public void doAction(List<Node> nodes, CyNetwork network, 
								CyNetworkView view, CyAttributes attributes);
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		new Thread() {
			public void run() {
				JTaskConfig config = new JTaskConfig();
				config.setAutoDispose(true);
				config.setModal(false);
				config.displayCancelButton(true);
				config.displayStatus(true);
				TaskManager.executeTask(SelectedNodeAction.this, config);
			}
		}.start();
	}

	protected void updatePercentage(int completed, int total) {
		double percentage = completed * 100.0d;
		getTaskMonitor().setPercentCompleted((int) (percentage/total));
	}
	
	protected synchronized boolean isCancelled() {
		return cancelled;
	}
	
	public String getTitle() {
		return getName();
	}

	/* (non-Javadoc)
	 * @see cytoscape.task.Task#halt()
	 */
	public synchronized void halt() {
		cancelled = true;
	}

	/* (non-Javadoc)
	 * @see cytoscape.task.Task#run()
	 */
	public void run() {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		final CyNetworkView view = Cytoscape.getCurrentNetworkView();
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		if (network == null || view == null || attributes == null) return;

		// set up the edit
		ViewChangeEdit undoableEdit = new ViewChangeEdit((DGraphView)view, getName());

		doAction(getNodes(), network, view, attributes);
		
		if (isCancelled()) undoableEdit.undo();
		// post the edit 
		else undoableEdit.post();

		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				view.updateView();
			}
		});

	}

	/* (non-Javadoc)
	 * @see cytoscape.task.Task#setTaskMonitor(cytoscape.task.TaskMonitor)
	 */
	public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	/**
	 * @return the taskMonitor
	 */
	public TaskMonitor getTaskMonitor() {
		return taskMonitor;
	}
	
	

}
