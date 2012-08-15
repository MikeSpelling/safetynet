/************************************************
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		SafetyNet
 *
 *	Author: 		Miohael Angus
 *
 *	Description: 	A GUI environment originally designed for the purposes of
 *					testing the ImagePlayer module - now adapted for testing the
 *					LocalMouseMonitor. 3 coloured zones are drawn on screen for
 *					use as zone areas for the mouse monitor.
 ***************************************************/

package cfss.org.LocalMouseMonitor;

import java.awt.Color;
import java.awt.Container;
import javax.swing.JPanel;
import cfss.org.LocalMouseMonitor.LocalMouseMonitor;
import cfss.org.LocalMouseMonitor.MouseMonitorListener;
import cfss.org.LocalMouseMonitor.LocalMouseMonitor.Zone;
import cfss.org.flags.Debug;
import cfss.org.safetynet.gui.Gui;

@SuppressWarnings ("serial")

/**
 * Class to provide testing funcitonality to mouse monitor.
 *
 * @author Mike Angus
 */
public class LMMTestPanel extends JPanel implements MouseMonitorListener{

	//variable to hold reference to main content pane in SafetyNet
	Container mainPane;

	//Test panel and zones
	JPanel testPanel;

	JPanel redZone;
	JPanel greenZone;
	JPanel yellowZone;

	//Local mouse monitor
	LocalMouseMonitor myLMM;


	/**
	 * Constructor.
	 */
	public LMMTestPanel() {

		//Create the panel
		super();

		//Obtain the SafetyNet content pane and add the panel
		mainPane = Gui.getSlidePanel();

		//Create a test panel
		testPanel = new TestPanel();
		mainPane.add(testPanel);
		testPanel.setBounds(200,150,400,300);

		//Define zones
		//Red zone
		redZone = new JPanel();
		redZone.setBackground(Color.red);
		testPanel.add(redZone);
		redZone.setBounds(0, 0, 400, 100);

		//Green zone
		greenZone = new JPanel();
		greenZone.setBackground(Color.green);
		testPanel.add(greenZone);
		greenZone.setBounds(0, 100, 200, 200);

		//Yellow zone
		yellowZone = new JPanel();
		yellowZone.setBackground(Color.yellow);
		testPanel.add(yellowZone);
		yellowZone.setBounds(200, 100, 200, 200);

		//Make the panel visible
		setVisible(true);

		//Create a LocalMouseMonitor
		myLMM = new LocalMouseMonitor(this, testPanel);
		myLMM.setNotifications(true,true,true,true,true,true);

		//Add zones for the three coloured areas
		myLMM.addZone("Red Zone", redZone.getLocation(),
				redZone.getSize());
		myLMM.addZone("Green Zone", greenZone.getLocation(),
				greenZone.getSize());
		myLMM.addZone("Yellow Zone", yellowZone.getLocation(),
				yellowZone.getSize());
	}


	/**
	 * Implementation of the mouse monitor interface.
	 *
	 * @param eventZone is the String containing the name of the event.
	 * @param eventType is the integer defining the type of zone.
	 */
	public void zoneEventOccurred(String eventZone, int eventType)
	{
		if(Debug.localMouse)
			System.out.println("Event occurred of type: "+eventType+
								" in zone: "+eventZone);
		double scaleFactor = 0.9;

		if(ZoneEvents.PRESS == eventType)
		{
			/*
			//----Horizontal scaling only
			testPanel.setLocation((int)((double)(testPanel.getX()*
					scaleFactor)), testPanel.getY());
			testPanel.setSize((int)((double)(testPanel.getWidth()*
					scaleFactor)), testPanel.getHeight());
			scaleColouredPanels();
			myLMM.applyScaleFactor(scaleFactor, true, false);

			//-----Vertical scaling only
			testPanel.setLocation(testPanel.getX(),
					(int)((double)(testPanel.getY()*scaleFactor)));
			testPanel.setSize(testPanel.getWidth(),
					(int)((double)(testPanel.getHeight()*scaleFactor)));
			scaleColouredPanels();
			myLMM.applyScaleFactor(scaleFactor, false, true);
			*/

			//-----Scaling in both directions
			testPanel.setLocation((int)((double)(testPanel.getX()*scaleFactor)),
					(int)((double)(testPanel.getY()*scaleFactor)));
			testPanel.setSize((int)((double)(testPanel.getWidth()*scaleFactor)),
					(int)((double)(testPanel.getHeight()*scaleFactor)));
			scaleColouredPanels();
			myLMM.applyScaleFactor(scaleFactor, true, true);
		}

		if(ZoneEvents.RELEASE == eventType && eventZone == "Red Zone")
		{
			Zone zoneToModify = myLMM.retrieveZone("Red Zone");
			zoneToModify.name = "SCHOOBLERIA";
			//myLMM.removeZone("NonExistent Zone");
		}
	}


	/**
	 * Method called when mouse dragged.
	 *
	 * @param startZone the String name defining the zone.
	 * @param mouseX integer mouse x position.
	 * @param mouseY integer mouse y position.
	 * @param dragComplete boolean value which is true when drag finished.
	 */
	public void dragEventOccurred(String startZone, int mouseX, int mouseY,
												Boolean dragComplete)
	{
		if(!dragComplete && Debug.localMouse == true)
			System.out.println("Drag started at "+startZone+", now at "+
					mouseX+","+mouseY);

		if(dragComplete && Debug.localMouse == true)
			System.out.println("Drag completed at "+mouseX+","+mouseY);
	}


	/**
	 * Method to scale the coloured panels.
	 */
	public void scaleColouredPanels()
	{
		//Scale the coloured panels to match changes in testPanel size
		redZone.setSize(testPanel.getWidth(), testPanel.getHeight()/3);

		greenZone.setSize(testPanel.getWidth()/2, 2*testPanel.getHeight()/3);
		greenZone.setLocation(0, testPanel.getHeight()/3);

		yellowZone.setSize(testPanel.getWidth()/2, 2*testPanel.getHeight()/3);
		yellowZone.setLocation(testPanel.getWidth()/2, testPanel.getHeight()/3);
	}


	private class TestPanel extends JPanel
	{
		public TestPanel()
		{
			super(null);
		}
	}
}