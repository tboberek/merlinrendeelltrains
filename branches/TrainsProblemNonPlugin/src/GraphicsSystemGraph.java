import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

public class GraphicsSystemGraph extends PCanvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, Integer> nodeIDToNumber = new HashMap<String, Integer>();
	private Integer nodeCount = 0;
	
	private PLayer nodeLayer;
	private PLayer edgeLayer;
	
	public GraphicsSystemGraph () {
		nodeLayer = getLayer ();
		edgeLayer = new PLayer ();
	
		getRoot().addChild (edgeLayer);
		getCamera().addLayer(0, edgeLayer);
		
		createTestGraph ();
		}
		
	public void createTestGraph () {

		ArrayList<String> bConnections = new ArrayList<String> ();
		ArrayList<String> cConnections = new ArrayList<String> ();
		ArrayList<String> dConnections = new ArrayList<String> ();
		ArrayList<String> eConnections = new ArrayList<String> ();
		ArrayList<String> fConnections = new ArrayList<String> ();
		
		bConnections.add ("A");
		
		cConnections.add ("B");
		cConnections.add ("A");
		
		dConnections.add ("B");
		dConnections.add ("C");
		dConnections.add ("A");
		
		eConnections.add ("A");
		eConnections.add ("B");
		eConnections.add ("C");
		eConnections.add ("D");
		
		fConnections.add ("A");
		fConnections.add ("B");
		fConnections.add ("C");
		fConnections.add ("D");
		fConnections.add ("E");
		
		addNode ("A");
		addNode ("B", 0, 500, bConnections);
		addNode ("C", 500, 0, cConnections);
		addNode ("D", 1000, 0, dConnections);
		addNode ("E", 0, 700, eConnections);
		addNode ("F", 500, 700, fConnections);
	}
	
	public Integer addNode (String nodeID){
		return addNode (nodeID, nodeCount * 30, nodeCount * 30);
	}
	
	public Integer addNode (String nodeID, Integer xPos, Integer yPos){
		nodeIDToNumber.put (nodeID, nodeCount++);
			
		PPath node = PPath.createEllipse (xPos, yPos, 50, 50);
		nodeLayer.addChild (node);
		
		return nodeCount - 1;
	}
	
	public Integer addNode (String nodeID, ArrayList<String> connections) {
		return addNode (nodeID, nodeCount * 30, nodeCount * 30, connections);
	}
	
	public Integer addNode (String nodeID, Integer xPos, Integer yPos,
			ArrayList<String> connections) {
		Integer newNodeID = addNode (nodeID, xPos, yPos);
		
		for (String connection : connections) {
			addEdge (newNodeID, nodeIDToNumber.get(connection));
		}
		
		return newNodeID;
	}
	
	public void colorNode (String nodeID, Color color){
		PNode node = nodeLayer.getChild (nodeIDToNumber.get (nodeID));
		node.setPaint(color);	
	}
	
	private void addEdge (Integer source, Integer destination) {
		PNode srcNode = nodeLayer.getChild (source);
		PNode dstNode = nodeLayer.getChild (destination);
		
		PPath edge = new PPath ();

		edgeLayer.addChild (edge);
		
		Point2D start 	= srcNode.getFullBoundsReference ().getCenter2D ();
		Point2D end		= dstNode.getFullBoundsReference ().getCenter2D ();
		
		edge.reset ();
		
		edge.moveTo((float)start.getX (), (float)start.getY());
		edge.lineTo((float)end.getX (), (float)end.getY());
		
	}

}
