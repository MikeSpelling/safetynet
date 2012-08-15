/*************************************************
 *	Crab Factory Software Solutions
 *
 *	Software: 			SafetyNet
 *	Module: 			CoverFlow
 *  Date Last Edited	17/06/2010
 *
 *  Author: 			Michael Spelling
 *
 *  Contributers:		David Walker, Mark Wrightson
 *  Testers:			Michael Spelling(Test Plan)
 *  					Harry Taylor (Tester)
 *
 *  Description: Takes thumbnails of each slides, displays them in an
 *  easy to navigate row. Clicking on a thumbnail will take the user
 *  to that slide.
 *
 *************************************************/


package cfss.org.safetynet.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;
import javax.swing.JPanel;

import cfss.org.LocalMouseMonitor.LocalMouseMonitor;
import cfss.org.LocalMouseMonitor.MouseMonitorListener;
import cfss.org.LocalMouseMonitor.ZoneEvents;
import cfss.org.flags.Debug;
import cfss.org.safetynet.Engine;
import cfss.org.safetynet.SafetyNet;
import cfss.org.safetynet.gui.coverflow.ImageFlow;
import cfss.org.xmlstructure.TreeEntity;

/**
 * Coverflow is instantiated with a background image but no coverflow images.
 * When initialise is called it checks for images held in the vector of
 * TreeEntities and displays them.
 *
 * @author Michael Spelling
 */
public class CoverFlow extends ShimmyPane implements MouseMonitorListener{
	private static final long serialVersionUID = 1L;
	private JPanel bkgndPanel;
	private ImageFlow imageFlow = null;
	private Vector <TreeEntity> treeEntityVector;
	private LocalMouseMonitor mmCoverFlow;


	/**
	 * Constructor creates an empty coverflow.
	 *
	 * @param bounds is the Rectangle bounds to resize to.
	 */
	public CoverFlow(Rectangle bounds) {
		// Set the interval, delta and direction
		super(1, 5, "down");

		// Set the main panel to be see through
		panel.setOpaque(false);

		// Add the grab tab image
		LoadImage img = new LoadImage("images/grabtab-bottom.png");
		tagPanel.add(img);
		img.setBounds(0,0, img.getPreferredSize().width,
				img.getPreferredSize().height);

		// Add the coverflow image to a transparent background
		LoadImage CoverFlowBK = new LoadImage("images/CoverFlow.png");
		bkgndPanel = new JPanel();
		bkgndPanel.setOpaque(false);
		bkgndPanel.setLayout(new BorderLayout());
		bkgndPanel.add(CoverFlowBK, BorderLayout.CENTER);
		panel.add(bkgndPanel);

		//instantiate the coverFlow mouse Monitor
		mmCoverFlow = new LocalMouseMonitor(this,new Point(0,Gui.getContentPane().getHeight()-10),new Dimension(Gui.getContentPane().getWidth(),10),Gui.getBigMouseMonitorPanel());
		mmCoverFlow.setNotifications(false,false,false,true,false,true);	//notify enter,moved
		mmCoverFlow.addZone("coverFlow", new Point(0,0), new Dimension(Gui.getContentPane().getWidth(),10));

		// Resize to thechosen dimensions
		resize(bounds);
	}


	/**
	 * Resizes the coverflow depending on the point and dimension passed in.
	 *
	 * @param bounds is the Rectangle bounds to resize to.
	 */
	public void resize(Rectangle bounds) {
		int tagWidth = 100;
		int tagHeight = 20;
		int x2 = (bounds.width / 2) + bounds.x - (tagWidth / 2);
		int y2 = bounds.y - 20;

		// Set panel bounds to the chosen point and dimension
		panel.setBounds(bounds);
		panel.validate();

		// Set tag bounds to the calculated point and dimension
		tagPanel.setBounds(x2, y2, tagWidth, tagHeight);

		// Set ImageFlow bounds to the current dimension of slidePanel
		if(imageFlow != null)
			imageFlow.setBounds(0, 0, panel.getWidth(), panel.getHeight());
		bkgndPanel.setBounds(0, 0, panel.getWidth(), panel.getHeight());

		if(isShimmyVisible()){
			mmCoverFlow.reposition(0, Gui.getContentPane().getHeight()-panel.getHeight()-100);
			mmCoverFlow.rescale(Gui.getContentPane().getWidth(),100);

		}
		else if(!isShimmyVisible()){
			mmCoverFlow.reposition(0, Gui.getContentPane().getHeight()-10);
			mmCoverFlow.rescale(Gui.getContentPane().getWidth(), 10);
		}
	}


	/**
	 * Returns true if panel is visible.
	 *
	 * @return boolean value which is true if panel is visible.
	 */
	public boolean isPanelVisible() {
		return panel.isVisible();
	}


	/**
	 * Specifies the conditions in which the shimmy pane will open.
	 * If the conditions are met, showTimer.start() is called.
	 *
	 * The conditions are:
	 * - Show and hide timers are not running.
	 * - CoverFlow panel is not visible.
	 * - SectionNav panel is not visible.
	 *
	 * @return whether the shimmy pane open timer has been triggered
	 */
	public boolean triggerShimmyIn() {
		if(!showTimer.isRunning() && !isShimmyVisible()
				&& !Gui.getSectionNav().isShimmyVisible()){
			// Incase slide position changes before coverflow is loaded
			updateToSlide();

			// If conditions are met stop hideTimer and start showTimer
			hideTimer.stop();
			showTimer.start();

			// Give coverflow focus when it is displayed
			getFocus();
			return true;
		}
		else
			return false;
	}

	/**
	 * Action to be Performed when the ShimmyPane begins to open.
	 *
	 * This will disable the menuList and set the visibility of the menuBar to true.
	 */
	public void shimmyInStarted(){
		panel.setVisible(true);
	}

	/**
	 * Action to be Performed when the ShimmyPane has fully opened
	 *
	 * This enables the menuList
	 */
	public void shimmyInStopped(){

	}

	/**
	 * This sets the menu bar hide timer running if the hide conditions are met:
	 * These conditions are that:
	 * - The panel is visible.
	 * - The start and hide timers aren't running.
	 * This method will only return true once.  It will return false
	 * on subsequent calls until triggerShimmyIn redisplays the shimmypane.
	 *
	 * @return boolean - true if the coverflow can be hidden false if it cannot.
	 */
	public boolean triggerShimmyOut() {
		if(!hideTimer.isRunning() && isShimmyVisible()){
			// If conditions are met stop showTimer and start hideTimer
			showTimer.stop();
			hideTimer.start();
			return true;
		}
		else
			return false;
	}

	/**
	 * Action to be Performed when the ShimmyPane begins to close
	 *
	 * This disables the use of the menuList
	 */
	public void shimmyOutStarted(){
	}

	/**
	 * Action to be Performed when the ShimmyPane has fully closed
	 *
	 * This enables the use of MenuList and sets the visibility of MenuBar to true.
	 */
	public void shimmyOutStopped(){
		panel.setVisible(false);
		Gui.getMainFrame().requestFocus();

	}

	/**
	 * Not Used. - a required stub for MouseMonitor
	 */
	public void dragEventOccurred(String startZone, int mouseX, int mouseY,
			Boolean dragComplete) {

	}


	/**
	 * This method handles the Shimmy Panel Mouse Events.
	 * It will trigger the shimmypane to move in or out and will position
	 * the zones accordingly.
	 */
	public void zoneEventOccurred(String eventZone, int eventType) {
		if((eventType == ZoneEvents.MOVEMENT || eventType == ZoneEvents.ENTER)
				&& eventZone.equals("normalMouse"))
			Gui.changeCursor(Cursor.DEFAULT_CURSOR);

		if(Debug.coverflow) System.out.println("MOUSE EVENT " + eventType);

		if((eventType == ZoneEvents.MOVEMENT || eventType == ZoneEvents.ENTER)
				&& eventZone.equals("coverFlow")) {

			if(Debug.coverflow) System.out.println(eventZone);
			if(triggerShimmyIn()) {
				mmCoverFlow.reposition(0, Gui.getContentPane().getHeight() -
						panel.getHeight()-100);
				mmCoverFlow.rescale(Gui.getContentPane().getWidth(),100);
			}
			else if(triggerShimmyOut()) {
				mmCoverFlow.reposition(0, Gui.getContentPane().getHeight()-10);
				mmCoverFlow.rescale(Gui.getContentPane().getWidth(), 10);
			}
			else if(Debug.coverflow) System.out.println(
					"CoverFlow - do nothing");
		}
	}


	/**
	 * Creates the images to display by checking the vector of TreeEntity's
	 * in SlideShow and instantiating ImageFlow with this.
	 */
	public void initialise() {
		// Instantiate a vector of TreeEntity's
		treeEntityVector = new Vector <TreeEntity>();

		// Fill the vector with the relevant TreeEntity's if they exist.
		if(SafetyNet.getSlideShow() != null)
			treeEntityVector = SafetyNet.getSlideShow().getTree();

		// Remove any current images
		if(imageFlow != null)
			panel.remove(imageFlow);

		// Try and create a coverflow from the vector.
        try {imageFlow = new ImageFlow(treeEntityVector);}
        catch(Exception e) {
        	System.out.println("Could not create coverflow images");
        }

        // If ImageFlow was created add it to the panel
        if(imageFlow != null) {
	        panel.add(imageFlow);
	        panel.setComponentZOrder(imageFlow, 0);
	        panel.setComponentZOrder(bkgndPanel, 1);
			imageFlow.setBounds(0, 0, panel.getWidth(), panel.getHeight());
        }
	}


	/**
	 * Get focus when coverflow appears by calling FocusGrab in ImageFlow.
	 */
	public void getFocus() {
		if(imageFlow != null)
			imageFlow.FocusGrab();
	}


	/**
	 * Update Coverflow images to the current slide when opening Coverflow or
	 * moving between slides.
	 */
	public void updateToSlide() {
		if(imageFlow != null) {
			if(panel.isVisible()){
				try{
					imageFlow.scrollAndAnimateTo(Engine.getCurrentSlideID() - 1);
				}
				catch(NullPointerException e){}
				catch(IndexOutOfBoundsException e){}
			}
			else{
				try{
					imageFlow.setSelectedIndex(Engine.getCurrentSlideID() - 1);
				}
				catch(NullPointerException e){}
				catch(IndexOutOfBoundsException e){}
			}
		}
	}


	////////////////////////////////////////////
	//Main method and testing happens below...//
	////////////////////////////////////////////

	public static void main(String[] args) {
		// Instantiate SafetyNet without warnings
		@SuppressWarnings("unused") SafetyNet myApp = new SafetyNet();

		// Run tests debug flag is set
		testCoverFlow();
	}


	public static void testCoverFlow() {
		if(Debug.coverflow == false)
			return;

		final boolean testResize = false;
		final boolean testMouseHandling = false;
		final boolean testKeyboardHandling = false;
		final boolean testMouseWheel = false;

		if(testResize) {
			System.out.print("\n\nTesting Resizing");
			System.out.print("\nTry resizing the empty presentation, check " +
					"coverflow acts appropriately");
			System.out.print("\nLoad up the test XML whilst maximised." +
					" Try resizing various sizes to test it works.");
		}

		if(testMouseHandling) {
			System.out.print("\n\nTesting Mouse Handling");
			System.out.print("\nOpen g1_SN_FA_v0.1.xml. Try navigating " +
					"through slides by mouse. When you click on the centre " +
					"image you should be transported to that slide.");
		}

		if(testKeyboardHandling) {
			System.out.print("\n\nTesting Keyboard Handling");
			System.out.print("\nOpen g1_SN_FA_v0.1.xml. Try navigating " +
					"through slides via the left and right buttons on the " +
					"keyboard.\nWhen you press enter you should be " +
					"transported to that slide.");
		}

		if(testMouseWheel) {
			System.out.print("\n\nTesting Mouse Wheel Handling");
			System.out.print("\nOpen g1_SN_FA_v0.1.xml. Try navigating " +
					"through slides by scrolling up and down with the" +
					"mouse wheel.\n");
		}
	}
}