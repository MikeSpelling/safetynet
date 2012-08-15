/************************************************
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		MenuBar
 *
 *  Author: 		Mark Wrightson
 *
 *  Contributers:	Callum Goddard, Michael Spelling
 *  Testers:		.
 *
 *
 *  Description:	This is the MenuBar which appears at the top of
 * 				 	SafetyNet. It contains the menuList, SoundControl and
 * 					Transport bars.
 *
 ***********************************************/

package cfss.org.safetynet.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JPanel;
import cfss.org.LocalMouseMonitor.LocalMouseMonitor;
import cfss.org.LocalMouseMonitor.MouseMonitorListener;
import cfss.org.LocalMouseMonitor.ZoneEvents;
import cfss.org.flags.Debug;
import cfss.org.safetynet.SafetyNet;

/**
 * This is the MenuBar which appears at the top of
 * SafetyNet.  It contains the menuList, SoundControl and Transport bars.
 *
 * @author Mark Wrightson
 */
public class MenuBar extends ShimmyPane implements MouseMonitorListener{
	public MenuList menu;
	private SoundControl soundPanel;
	private Transport transportPanel;
	private JPanel menuPanel;
	private JPanel bkgndPanel;
	private LocalMouseMonitor mmMenuBar;
	private boolean mouseInMenuArea;

	/**
	 * Constructor setups up the menu.
	 *
	 * @param bounds is the rectangle containing the bounds
	 */
	public MenuBar(Rectangle bounds){
		super(1,4,"up");		//super(timer(milliseconds),pixeldelta,direction)


		panel.setLayout(null);
		panel.setBackground(new Color(200,200,200,0));
		// Add the grab tab image

		LoadImage CoverFlowBK = new LoadImage("images/menubar.png");
		bkgndPanel = new JPanel();
		bkgndPanel.setOpaque(false);	// Make the panel transparent
		bkgndPanel.setLayout(new BorderLayout());
		bkgndPanel.add(CoverFlowBK, BorderLayout.CENTER);
		panel.add(bkgndPanel);

		menuPanel = new JPanel();
		menuPanel.setLayout(new BorderLayout());
		menuPanel.setOpaque(false);
		menu = new MenuList();

		// create transport controls
		transportPanel = new Transport();
		menuPanel.add(transportPanel, BorderLayout.CENTER);

		soundPanel = new SoundControl();
		menuPanel.add(soundPanel, BorderLayout.EAST);

		// create the menu bar
		menuPanel.add(menu.create(), BorderLayout.WEST);

		panel.add(menuPanel);
        panel.setComponentZOrder(menuPanel, 0);
        panel.setComponentZOrder(bkgndPanel, 1);

		//instantiate the menuBar mouse Monitor
		mmMenuBar = new LocalMouseMonitor(this,new Point(0,0),
				new Dimension(Gui.getContentPane().getWidth(),10),
				Gui.getBigMouseMonitorPanel());
		mmMenuBar.setNotifications(false,false,false,true,false,true);
		mmMenuBar.addZone("menuBar", new Point(0,0), new Dimension(
				Gui.getContentPane().getWidth(),10));

		resize(bounds);
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
	 * the zones accordingly
	 */
	public void zoneEventOccurred(String eventZone, int eventType) {
		if((eventType == ZoneEvents.MOVEMENT || eventType == ZoneEvents.ENTER )
				&& eventZone.equals("menuBar")){
			if(Debug.gui)
				System.out.println(eventZone);
			if(triggerShimmyIn()){
				mouseInMenuArea = true;
			}
			else if(triggerShimmyOut()){
				mouseInMenuArea = false;
			}
			else{
				if(Debug.gui)
					System.out.println("MenuBar - do nothing");
			}
		}
	}

	/**
	 * Resizes the bounds
	 *
	 * @param bounds is the rectangle of the bounds.
	 */
	public void resize(Rectangle bounds){
		panel.setBounds(bounds);
		menuPanel.setBounds(0, 0, bounds.width, bounds.height);
		bkgndPanel.setBounds(0, 0, bounds.width, bounds.height);
		menuPanel.validate();

		if(isShimmyVisible()){
			mmMenuBar.reposition(0,panel.getHeight()+5);
			mmMenuBar.rescale(Gui.getContentPane().getWidth(),100);
		}
		else if(!isShimmyVisible()){
			mmMenuBar.reposition(0,0);
			mmMenuBar.rescale(Gui.getContentPane().getWidth(), 10);
		}
	}

	/**
	 * Specifies the conditions in which the shimmy pane will open.
	 * If the conditions are met, showTimer.start(); is called.
	 * The conditions are:
	 * - show and hide timers are not running
	 * - menuBar panel is not visible
	 *
	 * @return whether the shimmy pane open timer has been triggered.
	 */
	public boolean triggerShimmyIn(){
		if(! showTimer.isRunning()
				&& !isShimmyVisible()
				&& isShimmyEnabled()
				){
			showTimer.start();
			mmMenuBar.reposition(0,panel.getHeight()+5);
			mmMenuBar.rescale(Gui.getContentPane().getWidth(),100);
			return true;
		}
		else return false;
	}

	/**
	 * Action to be Performed when the ShimmyPane begins to open.
	 *
	 * This will disable the menuList and set the visibility of the menuBar to
	 * true.
	 */
	public void shimmyInStarted(){
		menu.setEnabled(false);
		panel.setVisible(true);
		if(Debug.menu)System.out.println("shimmy in started");
	}

	/**
	 * Action to be Performed when the ShimmyPane has fully opened
	 *
	 * This enables the menuList
	 */
	public void shimmyInStopped(){
		menu.setEnabled(true);
		if(Debug.menu)System.out.println("shimmy in stopped");
	}

	/**
	 * This sets the menu bar hide timer running if, the hide conditions are met:
	 * - The menu is visible
	 * - The menu is not locked on screen.
	 * - the start and hide timers aren't running
	 * This method will only return true once.  It will return false
	 * on subsequent calls until triggerShimmyIn redisplays the shimmypane.
	 *
	 * @return boolean - true if the menubar can be hidden
	 * false if it cannot.
	 */
	public boolean triggerShimmyOut(){

		if( ! hideTimer.isRunning()
				&& isShimmyVisible()
				&& !menu.isMenuItemOpen()
				&& !menu.isLock()){
			hideTimer.start();
			mmMenuBar.reposition(0,0);
			mmMenuBar.rescale(Gui.getContentPane().getWidth(), 10);
			return true;
		}
		else return false;
	}

	/**
	 * Action to be Performed when the ShimmyPane begins to close
	 *
	 * This disables the use of the menuList
	 */
	public void shimmyOutStarted(){
		menu.setEnabled(false);
		if(Debug.menu)System.out.println("shimmy out started");
	}

	/**
	 * Action to be Performed when the ShimmyPane has fully closed
	 *
	 * This enables the use of MenuList and sets the visibility of MenuBar to true.
	 */
	public void shimmyOutStopped(){
		menu.setEnabled(true);
		panel.setVisible(false);
		Gui.getMainFrame().requestFocus();
		if(Debug.menu)System.out.println("shimmy out stopped");
	}

	/**
	 * Returns whether mouse is in area of menu.
	 *
	 * @return mouseInMenuArea the boolean to return.
	 */
	public boolean isMouseInMenuArea() {
		return mouseInMenuArea;
	}


	public static void main(String[] args) {
		new SafetyNet();
	}
}