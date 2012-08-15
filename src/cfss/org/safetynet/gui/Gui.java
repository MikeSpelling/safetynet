/************************************************
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		Gui
 *
 *  Author: 		Mark Wrightson
 *
 *  Contributers:
 *  Testers:		Michael Tock (test plan)
 *  				Michael Spelling (Tester)
 *
 *  Description:	This module controls the software GUI
 ***********************************************/
package cfss.org.safetynet.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import com.apple.eawt.Application;
import cfss.org.LocalMouseMonitor.LocalMouseMonitor;
import cfss.org.LocalMouseMonitor.MouseMonitorListener;
import cfss.org.LocalMouseMonitor.ZoneEvents;
import cfss.org.flags.Debug;
import cfss.org.safetynet.BrowserLoader;
import cfss.org.safetynet.SafetyNet;
import cfss.org.safetynet.Config;


/**
 * The GUI Class controls the entire UserInterface of SafetyNet.
 * It is responsible for maintaining the slide aspect ratio,
 * fullscreen operations, window resizing etc.
 *
 * @author Mark
 *
 */
public class Gui implements ComponentListener,MouseMonitorListener,WindowStateListener{

	/*GUI Components*/

	/**
	 * the main Window
	 */
	private static JFrame frame;
	/**
	 * main Window container
	 */
	private static Container contentPane;

	/**
	 * Internal Layer Panel
	 */
	private static JLayeredPane layeredPane;
	/**
	 * Main SlidePanel
	 */
	private static JLayeredPane slidePanel;

	/**
	 * Panel for LocalMouseMonitors
	 */
	private static JPanel mouseMonitorPanel;

	private static JPanel lowerMouseMonitorPanel;
	/**
	 * Panel for BigLocalMouseMonitors
	 */
	private static JPanel bigMouseMonitorPanel;

	/**
	 * Panel for images (behind slide)
	 */
	private static JPanel backgroundPanel;

	/**
	 * SafetyNet initialisation Size
	 */
	private static Dimension initSize = new Dimension(800,600);

	/**
	 * Minimum resolution of SafetyNet
	 */
	private static Dimension minSize = new Dimension(640,480);

	/**
	 * specifies fullscreen mode or windowed mode
	 */
	private static boolean fullScreenOn = false;
	/**
	 * stores the dimensions of the window before maximising
	 * such that the size can be correctly restored when coming
	 * out of maximise
	 */
	private static Rectangle preMaximiseBounds;
	/**
	 * stores the dimensions of the window before going fullscreen
	 * such that the size can be correctly restored when coming
	 * out of fullscreen
	 */
	private static Rectangle framePreFullScreenDimensions;

	/**
	 * stores the dimensions of the contentPane before going fullscreen
	 * such that the size can be correctly restored when coming
	 * out of fullscreen
	 */
	private static Rectangle contentPanePreFullScreenDimensions;

	/**
	 * Flag ensures that video can be maximised to fill the screen
	 * The VideoFullScreenOn flag will override the 4:3 aspect ratio of
	 * the slidePanel
	 */
	private static boolean videoFullScreenOn = false;
	/**
	 * When VideoMaximsed is used, it is neccessary to be able to restore the
	 * window to its previous state when the video returns from its maximise mode.
	 * This will ensure that if SafetyNet is full screen prior to video maximise,
	 * it shall return to fullscreen mode when video becomes demaximised
	 */
	private static boolean prevStateFullScreenOn = false;
	/**
	 * instance variables of the LocalMouse Monitors that
	 * ensures the mouse cursor returns to a pointer
	 */
	private static LocalMouseMonitor mmNormalMouse;

	/**
	 * The section Navigation Panel
	 */
	private static SectionNavigation sectionNav;
	/**
	 * The coverFlow Panel
	 */
	private static CoverFlow coverFlow;
	/**
	 * the Top Menu Bar
	 */
	private static MenuBar menuBar;

	/**
	 * slidePanelBounds
	 */
	private static Rectangle slidePanelB = new Rectangle();

	/**
	 * sectionNavBounds
	 */
	private static Rectangle sectionNavB = new Rectangle();
	/**
	 * menuBarBounds
	 */
	private static Rectangle menuBarB = new Rectangle();
	/**
	 * coverFlowBounds
	 */
	private static Rectangle coverFlowB = new Rectangle();

	/**
	 * force the window default to open on the secondary display
	 * on multi-screen setups
	 */
	private boolean defaultToSecondMonitor = true;


	/**
	 * Instantiates the SafetyNet GUI, adds the various listeners to the JFrame
	 * and ensures that the GUI is displayed correctly on the monitor
	 */
	public Gui() {

		GraphicsConfiguration gcc;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		//find all of the displays on the current machine
		GraphicsDevice[] gd = ge.getScreenDevices();
		if (Debug.gui)
			System.out.println("Display on Screen: " + gd.length);

    	//if there is more than 1 monitor, display on the last monitor if flag is set
    	//some more clever screen detection could be achieved here
    	if(gd.length>1 && defaultToSecondMonitor==true){
    		gcc = gd[gd.length-1].getDefaultConfiguration();
    	}
    	else{
    		gcc = gd[0].getDefaultConfiguration();
    	}
    	Rectangle monitor = gcc.getBounds();

		// Instantiate a JFrame
    	//TODO The name must be set by the XML
		frame = new JFrame("SafetyNet" ,gcc);

		//if the default size is larger than the monitor, then decrease the size
		//until it fits
    	if(monitor.height <= initSize.height){
    		initSize.height = monitor.height-100;
    	}
    	if(monitor.width <= initSize.width){
    		initSize.width = monitor.width-100;
    	}
    	if(Debug.gui)System.out.println("Monitor:"+monitor);
    	if(Debug.gui)System.out.println("displayed:"+initSize);
		instantiateGUIComponents();

		Image icon = Toolkit.getDefaultToolkit().getImage("icons/CFSS_logo.png");
		// For Mac OSX operating systems only - set the dock icon.
		if (System.getProperty("mrj.version") == null) {
			// Load an image in as an "icon" for windows...
			// Set "icon" as frame icon (For Window Operating systems only)
			frame.setIconImage(icon);
		}
		else{
			Application.getApplication().setDockIconImage(icon);
		}

		// Add code to close window and terminate the application
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//save data if a slideshow is open
				if(null != SafetyNet.getFileLocation())
					Config.saveData();

				System.exit(0);
			}
		});

		//get hotkeys to show menus, get help and exit fullscreen
		frame.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				if(Debug.gui)System.out.println(e.getKeyCode());
				if (KeyEvent.VK_F1 == e.getKeyCode()) //F1
					//show help
					BrowserLoader.openURL("http://www.voltnet.co.uk/tour/help");
				if (KeyEvent.VK_CONTROL == e.getKeyCode()|| KeyEvent.VK_ALT == e.getKeyCode())//ctrl or alt
					//display menu bar
					menuBar.triggerShimmyIn();
				if (KeyEvent.VK_ESCAPE == e.getKeyCode()){//esc
					//if video is fullsccreen, reduce it
					if (isVideoFullScreenOn())
						setVideoWindowedMode();
					//else if window is fullscreen reduce it
					else if (isFullScreenOn())
						setWindowedMode();
				}
			}
			public void keyReleased(KeyEvent e) {
				if (KeyEvent.VK_CONTROL == e.getKeyCode() ||
						KeyEvent.VK_ALT == e.getKeyCode()) //ctrl or alt
					//hide the menu bar
					if (!menuBar.isMouseInMenuArea())
						//cursor should hold menu open
						menuBar.triggerShimmyOut();
			}
		});
	}

	/**
	 * This method sets up the gui.  It retrieves the contentPane, creates a
	 * LayeredPane and instantiates SlidePanel, backgroundPanel, SectionNav,
	 * CoverFlow, MenuBar, and the mouseMonitor Panels
	 */
	private void instantiateGUIComponents(){
		//get the contentPane
		contentPane = frame.getContentPane();
		//set the background color behind the slidePanel
		contentPane.setBackground(new Color(20,20,20));
		contentPane.setSize(new Dimension(initSize.width,initSize.height));
		contentPane.setPreferredSize(new Dimension(initSize.width,
				initSize.height));
		if(Debug.gui)
			System.out.println("content-pane-width:"+
					getContentPane().getWidth());

		//instantiate a LayeredPane
		layeredPane = new JLayeredPane();
		contentPane.add(layeredPane);
		//doublebuffering is required to prevent video render problems
		layeredPane.setDoubleBuffered(true);

		//calculate the initial component sizes
		calcComponentSizes(true);

		//instantiate the mousemonitorPanel
		lowerMouseMonitorPanel = new JPanel();
		layeredPane.add(lowerMouseMonitorPanel,new Integer(1));
		lowerMouseMonitorPanel.setLayout(null);
		//mouseMonitorPanel.setBackground(new Color(255,0,0,100));
		lowerMouseMonitorPanel.setOpaque(false);
		lowerMouseMonitorPanel.setBounds(0,0,initSize.width,initSize.height);

		//instantiate the mousemonitorPanel
		mouseMonitorPanel = new JPanel();
		layeredPane.add(mouseMonitorPanel,new Integer(398));
		mouseMonitorPanel.setLayout(null);
		//mouseMonitorPanel.setBackground(new Color(255,0,0,100));
		mouseMonitorPanel.setOpaque(false);
		mouseMonitorPanel.setBounds(0,0,initSize.width,initSize.height);



		//instantiate the bigMousemonitorPanel
		bigMouseMonitorPanel = new JPanel();
		layeredPane.add(bigMouseMonitorPanel,new Integer(399));
		bigMouseMonitorPanel.setLayout(null);
		//bigMouseMonitorPanel.setBackground(new Color(0,0,255,50));
		bigMouseMonitorPanel.setOpaque(false);
		bigMouseMonitorPanel.setBounds(0,0,contentPane.getWidth(),
				contentPane.getHeight());

		//instantiate the backgroundPanel, placing on layer behind slide panel
		backgroundPanel = new JPanel();
		layeredPane.add(backgroundPanel,new Integer(2));
		backgroundPanel.setLayout(null);
		backgroundPanel.setBackground(new Color(255,255,255));
		backgroundPanel.setBounds(0,0,initSize.width,initSize.height);

		//instantiate the slidePanel, placing on second layer to allow
		// background image behind
		slidePanel = new JLayeredPane();
		layeredPane.add(slidePanel,new Integer(3));
		slidePanel.setLayout(null);

		//Make transparent so background shows through
		slidePanel.setOpaque(false);
		slidePanel.setBackground(new Color(0,0,0));
		slidePanel.setBounds(0,0,initSize.width,initSize.height);

		//instantiate in the hidden off screen position
		menuBar = new MenuBar(menuBarB);

		//instantiate in the hidden off screen position
		sectionNav = new SectionNavigation(sectionNavB);

		//instantiate in the hidden off screen position
		coverFlow = new CoverFlow(coverFlowB);

		if(Debug.gui){
			System.out.println("content-pane-width:"+contentPane.getWidth());
			System.out.println("frame-width-prepack:"+frame.getWidth());
		}

		//pack the frame
		frame.pack();		//pack packs according to the preferred size

		//display the main window
		frame.setVisible(true);

		preMaximiseBounds = frame.getBounds();

		if(Debug.gui)
			System.out.println("frame-width-postpack:"+frame.getWidth());

		//instantiate the default curser
		mmNormalMouse = new LocalMouseMonitor(this,new Point(0,0),
				new Dimension(contentPane.getWidth(),contentPane.getHeight()),
				lowerMouseMonitorPanel);
		mmNormalMouse.setNotifications(false,false,false,true,false,true);
		mmNormalMouse.addZone("normalMouse", new Point(0,0),
				new Dimension(contentPane.getWidth(),contentPane.getHeight()));

		//check for resize events
		frame.addComponentListener(this);
		//check for window state changed events
		frame.addWindowStateListener(this);
		frame.requestFocus();
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
				&& eventZone.equals("normalMouse")){
			Gui.changeCursor(Cursor.DEFAULT_CURSOR);
		}
		if(Debug.gui)
			System.out.println("MOUSE EVENT "+eventType);
	}

	/****************
	 * CalcComponentSizes
	 *
	 * Calculate the size & positions of the slidePanel, coverFlow,
	 * SectionNav & MenuBar
	 * This calls the method CalcComponentSizes(boolean init)
	 * and sets the init value to false.
	 */
	private static void calcComponentSizes(){
		calcComponentSizes(false);
	}

	/****************
	 * CalcComponentSizes
	 *
	 * Calculate the size & positions of the slidePanel, coverFlow,
	 * SectionNav & MenuBar
	 * @param init - specifies whether (on intialisation of the software)
	 * to place the shimmy pane components off screen
	 */
	private static void calcComponentSizes(boolean init){
		/*this method assumes the panel is either visible or not visible
		 * An error will probably occur if the window is resized whilst
		 * a shimmy pane is moving.  The odds of this happening are basically
		 * zero
		 */

		//calculate cover flow co-ordinates
		coverFlowB.x = 0;
		coverFlowB.height = 180;
		coverFlowB.width = contentPane.getWidth();
		if(init || !coverFlow.isShimmyVisible()){
			coverFlowB.y = contentPane.getHeight();
		}
		else{
			coverFlowB.y = contentPane.getHeight() - coverFlowB.height;
		}

		//calculate menuBar co-ordinates
		menuBarB.x = 0;
		menuBarB.height = 50;
		menuBarB.width= contentPane.getWidth();
		if(init || !menuBar.isShimmyVisible()){
			menuBarB.y = -menuBarB.height;
		}
		else{
			menuBarB.y = 0;
		}

		//calculate sectionNav co-ordinates
		sectionNavB.height = contentPane.getHeight()-menuBarB.height;
		sectionNavB.y = menuBarB.height;
		sectionNavB.width= 230;
		if(init || !sectionNav.isShimmyVisible()){
			sectionNavB.x = -sectionNavB.width;
		}
		else{
			sectionNavB.x = 0;
		}

		//calculate the SlidePanel co-ordinates
		if(contentPane.getWidth()/4*3>=contentPane.getHeight()){
			//frame.getHeight is the largest dimension that can be used
			//whilst keeping 4:3 aspect ratio
			slidePanelB.height = contentPane.getHeight();
			slidePanelB.width= contentPane.getHeight()/3*4;
		}
		else{
			//frame.getWidth is the largest dimension that can be used
			//whilst keeping 4:3 aspect ratio
			slidePanelB.width = contentPane.getWidth();
			slidePanelB.height= contentPane.getWidth()/4*3;
		}
		//center the presentationPane in the window
		slidePanelB.x = (contentPane.getWidth()- slidePanelB.width)/2;
		slidePanelB.y = (contentPane.getHeight()- slidePanelB.height)/2;

	}

	/**
     * This method handles the resizing of the GUI when the actionlistener
     * detects the window has been resized.
     */
    private static void resizeHandler(){

    	//prevent window from getting smaller than minimum dimensions minSize
    	if(frame.getWidth()<minSize.width)contentPane.setSize(
    			minSize.width,contentPane.getHeight());
    	if(frame.getHeight()<minSize.height)contentPane.setSize(
    			contentPane.getWidth(),minSize.height);

    	int originalWidth = slidePanel.getWidth();

    	contentPane.setPreferredSize(contentPane.getSize());
    	calcComponentSizes();
    	reSizeComponents();
    	frame.pack();	// this packs to the preferred size of the contentPane
    	float scaleFactor = (float)slidePanel.getWidth()/originalWidth;

    	SafetyNet.resize(scaleFactor);

    	frame.setVisible(true);
    }

	/*****************
	 * Resize the slidePanel, menuBar, SectionNav & Coverflow according to the
	 * updated size & positions calculated by calcComponentSizes
	 */
	private static void reSizeComponents(){

		if(videoFullScreenOn){
			if(Debug.gui)
				System.out.println("resize called in video fullscreen mode");
		}
		else{
			slidePanel.setBounds(slidePanelB);
			mouseMonitorPanel.setBounds(slidePanelB);
			bigMouseMonitorPanel.setBounds(contentPane.getBounds());
			backgroundPanel.setBounds(slidePanelB);
		}
		slidePanel.validate();

		//resize the menuBar panel
		menuBar.resize(menuBarB);
		//resize the sectionNav panel
		sectionNav.resize(sectionNavB);
		//resize the coverFlow panel
		coverFlow.resize(coverFlowB);

		lowerMouseMonitorPanel.setBounds(contentPane.getBounds());
		mmNormalMouse.rescale(contentPane.getWidth(), contentPane.getHeight());
		mmNormalMouse.setSize(contentPane.getWidth(), contentPane.getHeight());
	}


	/**
	 * Sets the SafetyNet window to fullscreen
	 * and fullscreens video.  The slidePanel aspect ratio is changed such
	 * that video can be fullscreen
	 */
	public static void setVideoFullScreenMode(){
		if(!videoFullScreenOn){
			prevStateFullScreenOn = fullScreenOn;
			//set fullscreen
			if(!fullScreenOn)setFullScreenMode();
			videoFullScreenOn=true;
			//make backgroundPanel fullsize of screen
			backgroundPanel.setBounds(0, 0, contentPane.getWidth(), contentPane.getHeight());
			//make slide panel fullsize of screen
			slidePanel.setBounds(0, 0, contentPane.getWidth(), contentPane.getHeight());
			//make mouseMonitor slide panel fullsize of screen
			mouseMonitorPanel.setBounds(0, 0, contentPane.getWidth(), contentPane.getHeight());
		}
    	else{
    		if(Debug.gui)
    			System.out.println("Gui->setVideoFullScreenMode->already in Video Fullscreen mode");

    	}
	}

	/**
	 * Returns SafetyNet to its previous fullscreen setting
	 * and rewindows video.  The slidePanel aspect ratio is also restored
	 */
	public static void setVideoWindowedMode(){
		if(videoFullScreenOn){
			videoFullScreenOn=false;
			//set windowed mode
			if(false==prevStateFullScreenOn){
				setWindowedMode();
			}
			else{
				resizeHandler();		//restore slidepanel size
			}
		}
    	else{
    		if(Debug.gui)
    			System.out.println("Gui->setVideoWindowedMode->already in windowed mode");
    	}
	}


	/**
	 * Sets the window to fullscreen mode
	 */
	public static void setFullScreenMode(){
		if(fullScreenOn || videoFullScreenOn){
			if(Debug.gui)
				System.out.println("already fullscreened");
			return;
		}
		//goto fullscreen mode

		framePreFullScreenDimensions = frame.getBounds();
		contentPanePreFullScreenDimensions = contentPane.getBounds();
		System.out.println("frame-pfs-dimensions"+framePreFullScreenDimensions);
		System.out.println("contentPane-pfs-dimensions"+
				contentPanePreFullScreenDimensions);

		menuBar.menu.setFullscreen(true);	//update the tickBox on the menuBar
		frame.setVisible(false);
		frame.dispose();
		frame.setUndecorated(true);	//remove the window from the frame

    	GraphicsConfiguration gc = frame.getGraphicsConfiguration();
    	GraphicsDevice myScreen = gc.getDevice();
    	GraphicsEnvironment ge =
    		GraphicsEnvironment.getLocalGraphicsEnvironment();

    	// there are no guarantees that screen devices are in order, but
    	// they always are
    	GraphicsDevice[] gd = ge.getScreenDevices();
    	int myScreenIndex = -1;
    	if(Debug.gui)
    			System.out.println(gd.length);
    	for (int i = 0; i < gd.length; i++) {
    	    if (gd[i].equals(myScreen))
    	    {
    	        myScreenIndex = i;
    	        break;
    	    }
    	}

        //Assume default configuration
       	GraphicsConfiguration gcc = gd[myScreenIndex].getDefaultConfiguration();

       	Rectangle monitor = gcc.getBounds();

       	/*sets the screen to fullscreen exclusive mode
		fullscreen exclusive mode is a problem with dual screen monitors
		as SafetyNet minimises as soon as a window on the 2nd screen is
		selected*/
     	//myScreen.setFullScreenWindow(frame);

       	if(Debug.gui)
     		System.out.println(monitor.x + " " + monitor.y +
     			" " + monitor.width + " " + monitor.height);

     	frame.setBounds(monitor);
     	if(Debug.gui)
     		System.out.println("window is on screen" + myScreenIndex);


		fullScreenOn=true;			//set flag
		frame.setVisible(true);		//show the frame
		resizeHandler();

	}

	/**
	 * Sets the window to windowed mode
	 */
	public static void setWindowedMode(){
    	if(fullScreenOn  || videoFullScreenOn){
    		//return to windowed mode
	    	menuBar.menu.setFullscreen(false);	//update the tickBox on the menuBar
			frame.setVisible(false);
			frame.dispose();
			frame.setUndecorated(false);

			contentPane.setBounds(contentPanePreFullScreenDimensions);
			frame.setBounds(framePreFullScreenDimensions);
			System.out.println("restore dimensions:"+framePreFullScreenDimensions);

			/*brings the screen out fullscreen exclusive mode
			fullscreen exclusive mode is a problem with dual screen monitors
			as SafetyNet minimises as soon as a window on the 2nd screen is
			selected*/
			//GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(null);

			fullScreenOn = false;
			videoFullScreenOn = false;
			resizeHandler();
		}
    	else{
    		if(Debug.gui)
    			System.out.println("already in windowed mode");
    	}
	}

	/**
	 * Detects when the state of the window has changed
	 */
	 public void windowStateChanged(WindowEvent e) {
		 if(e.getOldState()==JFrame.MAXIMIZED_BOTH &&
				 e.getNewState()==JFrame.NORMAL){
			//restore preMaximised Size
			 if(Debug.gui)
				 System.out.println("wsc:PreMaxBounds:"+preMaximiseBounds);
			 frame.setBounds(preMaximiseBounds);
		 }
		 if(Debug.gui)
			 displayStateMessage("WindowStateListener method called: " +
			 		"windowStateChanged.", e);
	    }

	 	/**
	 	 * prints out the window state
	 	 * @param prefix - prefix to be added to the printed out statement
	 	 * @param e - Window Event
	 	 */
	    private void displayStateMessage(String prefix, WindowEvent e) {
		   	 String newline = "\n";
			 String space = " ";
	    	int state = e.getNewState();
	        int oldState = e.getOldState();
	        String msg = prefix
	                   + newline + space
	                   + "New state: "
	                   + convertStateToString(state)
	                   + newline + space
	                   + "Old state: "
	                   + convertStateToString(oldState);
	        System.out.println(msg);
	    }

	    private String convertStateToString(int state) {
	        if (state == JFrame.NORMAL) {
	            return "NORMAL";
	        }
	        String strState = " ";
	        if ((state & JFrame.ICONIFIED) != 0) {
	            strState += "ICONIFIED";
	        }
	        //MAXIMIZED_BOTH is a concatenation of two bits, so
	        //we need to test for an exact match.
	        if ((state & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH) {
	            strState += "MAXIMIZED_BOTH";
	        } else {
	            if ((state & JFrame.MAXIMIZED_VERT) != 0) {
	                strState += "MAXIMIZED_VERT";
	            }
	            if ((state & JFrame.MAXIMIZED_HORIZ) != 0) {
	                strState += "MAXIMIZED_HORIZ";
	            }
	            if (" ".equals(strState)){
	                strState = "UNKNOWN";
	            }
	        }
	        return strState.trim();
	    }


	/****************
	 * componentHidden
	 *
	 * currently unused
	 */
	public void componentHidden(ComponentEvent e) {
		if(Debug.gui)
			System.out.println(e.getComponent().getClass().getName() +
					" --- Hidden");
    }

	/****************
	 * componentMoved
	 *
	 * currently unused
	 ****************/
    public void componentMoved(ComponentEvent e) {
    	if(Debug.gui)
    		System.out.println(e.getComponent().getClass().getName() +
    				" --- Moved");
    	if(frame.getExtendedState()==JFrame.NORMAL && !fullScreenOn) {
			 preMaximiseBounds = frame.getBounds();
			 if(Debug.gui)System.out.println("compMoved:PreMaxBounds:"+
					 preMaximiseBounds);
		 }
    }


	/**
	 * componentShown
	 *
	 * currently unused
	 */
    public void componentShown(ComponentEvent e) {
    	if(Debug.gui)
    		System.out.println(e.getComponent().getClass().getName() +
    				" --- Shown");
    }

    /**
	 * componentResized
	 *
	 * Detects when the window is resized, and scales all of the other components
	 */
    public void componentResized(ComponentEvent e) {
    	if(Debug.gui)System.out.println(e.getComponent().getClass().getName() +
    			" --- Resized ");

    	//if the JFrame state is normal & it isn't maximised, save the size of
    	//the Jframe
    	if(frame.getExtendedState()==JFrame.NORMAL && !fullScreenOn) {
			 preMaximiseBounds = frame.getBounds();
			 if(Debug.gui)
				 System.out.println("compResized:PreMaxBounds:"+
						 preMaximiseBounds);
		 }
    	resizeHandler(); //resize everything
    }


	/**
	 *
	 * @param i - Allowed Values:
	 * DEFAULT_CURSOR,CROSSHAIR_CURSOR,TEXT_CURSOR,WAIT_CURSOR,SW_RESIZE_CURSOR,
	 * SE_RESIZE_CURSOR,NW_RESIZE_CURSOR,NE_RESIZE_CURSOR,N_RESIZE_CURSOR,
	 * S_RESIZE_CURSOR,W_RESIZE_CURSOR,E_RESIZE_CURSOR,HAND_CURSOR,MOVE_CURSOR
	 */
	public static void changeCursor(int i){

		Cursor handCursor = new Cursor(i);
		frame.setCursor(handCursor);

	}

	/**
	 * Getter for the ContentPane
	 * @return contentPane - the contentPane of the main SafetyNet JFrame
	 */
	public static Container getContentPane() {
		return contentPane;
	}

	/**
	 * Getter for the LayeredPane
	 * @return layeredPane
	 */
	public static JLayeredPane getLayeredPane() {
		return layeredPane;
	}

	/**
	 * Flag to determine whether the window is fullscreen or not
	 * @return fullScreenOn - state of the window - fullscreen / not fullscreen
	 */
	public static boolean isFullScreenOn(){
		return fullScreenOn;
	}

	/**
	 *
	 * @return slidePanel - returns the JPanel in which all slide components
	 * are added
	 */
	public static JLayeredPane getSlidePanel() {
		return slidePanel;
	}
	/**
	 *
	 * @return mouseMonitorPanel - returns the JPanel in which all mouse Monitor
	 * components are added except for Shimmy Pane mouse Monitor things.
	 * The mouseMonitorPanel is the same size as the slidePanel
	 *
	 */
	public static JPanel getMouseMonitorPanel() {
		return mouseMonitorPanel;
	}

	/**
	 *
	 * @return bigMouseMonitorPanel - returns the JPanel in which the
	 * Shimmy Pane mouse Monitor components are added.
	 * The bigMouseMonitorPanel is the same size as the contentPane
	 */
	public static JPanel getBigMouseMonitorPanel() {
		return bigMouseMonitorPanel;
	}
	/**
	 *
	 * @return backgroundPanel - returns the JPanel upon which the default
	 * background is drawn. Panel is positioned behind the transparent slidepanel
	 *
	 */
	public static JPanel getBackgroundPanel() {
		return backgroundPanel;
	}
	/**
	 *
	 * @return sectionNav - returns the SectionNavigation object
	 */
	public static SectionNavigation getSectionNav() {
		return sectionNav;
	}
	/**
	 *
	 * @return coverFlow - returns the coverFlow object
	 */
	public static CoverFlow getCoverFlow() {
		return coverFlow;
	}

	/**
	 *
	 * @return menuBar - returns the menuBar object
	 */
	public MenuBar getMenuBar() {
		return menuBar;
	}

	/**
	 * See videoMaximise()
	 * @return videoMaximised - returns the video maximised flag
	 */
	public static boolean isVideoFullScreenOn() {
		return videoFullScreenOn;
	}

	/**
	 *
	 * @return frame - returns the main window frame
	 */
	public static JFrame getMainFrame() {
		return frame;
	}

	public static void main(String[] args) {
		new SafetyNet();
	}
}