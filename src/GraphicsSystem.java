import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class GraphicsSystem extends JFrame {

	private static final long serialVersionUID = 1L;
	private GraphicsSystemGraph graph;

	public GraphicsSystem () {
		setTitle ("Merlin Randall Trains Test");
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		
		graph = new GraphicsSystemGraph ();
		
		getContentPane ().add (graph);
		setSize (1200, 800);
		
		setVisible (true);
	}
	
	public void moveTrain (Train train, String destination) {
		// We may not be in the event dispatch thread, so queue up our data
		final String destinationID 	= destination;
		final String sourceID		= train.getCurrentStation ();
		final Color  trainColor		= train.getColor ();
		
		Runnable r = new Runnable () {
			public void run () {
				graph.colorNode(sourceID, Color.WHITE);
				graph.colorNode(destinationID, trainColor);
			}
		};
		
		SwingUtilities.invokeLater(r);
		
	}
}
