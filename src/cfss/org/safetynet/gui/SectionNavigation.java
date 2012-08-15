package cfss.org.safetynet.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import cfss.org.LocalMouseMonitor.LocalMouseMonitor;
import cfss.org.LocalMouseMonitor.MouseMonitorListener;
import cfss.org.LocalMouseMonitor.ZoneEvents;
import cfss.org.flags.Debug;
import cfss.org.safetynet.Engine;
import cfss.org.safetynet.SafetyNet;
import cfss.org.xmlstructure.TreeEntity;
import cfss.org.xmlstructure.QuizSlide;;

/************************************************
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		Section Navigation
 *
 *  Author: 		Michael Tock
 *
 *  Contributers:	.
 *  Testers:		Michael Spelling
 *
 *  Description:	This module creates the content for the section navigation
 *  				panel. It also contains the methods for changing the color
 *  				of the leaves depending of the quiz result.
 *
 ***********************************************/

/**
 * This module creates the content for the section navigation
 * panel. It also contains the methods for changing the color
 * of the leaves depending of the quiz result.
 *
 * @author Michael Tock
 *
 */
public class SectionNavigation extends ShimmyPane implements MouseMonitorListener,TreeSelectionListener{

	private JTree contentTree;
	private JScrollPane treeScrollView;
	private Vector<Color> slideColors;
	public DefaultMutableTreeNode defaulttop;
	public DefaultTreeModel treeModel;
	private JPanel imagePanel;

	private int currentSlide = 1; //moved tro correct place

	DefaultTreeCellRenderer cellRenderer;
	private LocalMouseMonitor mmSectionNav;

	/**
	 *	Section navigation constructor. Primarily involved in setting up the
	 *	look and feel of the tree. Also deals with the background images used
	 *	within section navigation.
	 *
	 * @param bounds is the Rectangle bounds to use.
	 */
	public SectionNavigation(Rectangle bounds){
		super(1,4,"left");	//super(timer(milliseconds),pixeldelta,direction)
		if(Debug.sectionNav)System.out.println("SectionNavigation");

		panel.setOpaque(false);

		//add the grab tab image
		LoadImage img = new LoadImage("images/grabtab-left.png");
		tagPanel.add(img);
		img.setBounds(0,0, img.getPreferredSize().width,
				img.getPreferredSize().height);

		LoadImage imgT = new LoadImage("images/SectionNavTop.png");
		LoadImage imgM = new LoadImage("images/SectionNavThin.png");
		LoadImage imgB = new LoadImage("images/SectionNavBottom.png");

		imagePanel = new CustomJPanel();
		imagePanel.setOpaque(false);			//make the panel transparent
		imagePanel.setLayout(new BorderLayout());

		imagePanel.add(imgT,BorderLayout.NORTH);
		imagePanel.add(imgM,BorderLayout.CENTER);
		imagePanel.add(imgB,BorderLayout.SOUTH);

		slideColors = new Vector<Color>();
		slideColors.addElement(null);

		//instantiate the sectionNav mouse Monitor
		mmSectionNav = new LocalMouseMonitor(this,new Point(0,100),
				new Dimension(10,Gui.getContentPane().getHeight()-100),
				Gui.getBigMouseMonitorPanel());
		mmSectionNav.setNotifications(false,false,false,true,false,true);
		mmSectionNav.addZone("sectionNav", new Point(0,0),
				new Dimension(10,Gui.getContentPane().getHeight()-100));

		treeModel  = new DefaultTreeModel(new DefaultMutableTreeNode("SafetyNet"));

	    contentTree = new JTree(treeModel);
	    treeScrollView = new JScrollPane(contentTree);

	    contentTree.getSelectionModel().setSelectionMode(
	    		TreeSelectionModel.SINGLE_TREE_SELECTION);

	    resize(bounds);

		panel.add(treeScrollView);
		panel.add(imagePanel);

		panel.setComponentZOrder(imagePanel, 1);
		panel.setComponentZOrder(treeScrollView, 0);

		contentTree.addTreeSelectionListener(this);

		//get the DefaultTreeCellRenderer from the JTree
        cellRenderer = (DefaultTreeCellRenderer)contentTree.getCellRenderer();

        //set the new Opened, Closed, and Leaf Icons
        cellRenderer.setOpenIcon(new ImageIcon("icons/blue/Down Arrow.png"));
        cellRenderer.setClosedIcon(new ImageIcon("icons/blue/Right Arrow.png"));
        cellRenderer.setLeafIcon(new ImageIcon("icons/blue/slide.png"));

        //setting selection colors
        cellRenderer.setBackgroundNonSelectionColor(Color.white);
        cellRenderer.setBackgroundSelectionColor(Color.LIGHT_GRAY);
        cellRenderer.setBorderSelectionColor(Color.white);
        cellRenderer.setTextSelectionColor(Color.blue);
        cellRenderer.setTextNonSelectionColor(Color.blue);

        if(Debug.sectionNav)System.out.println("Section Nav Constructor Finished");
	}

	/**
	 * This method will create/update the navigation tree. The same method is
	 * called to both initialy populate the tree as well as updating when the
	 * content changes.
	 */
	public void updateTree(){
		if(Debug.sectionNav)System.out.println("Update tree...");
		try{
			DefaultMutableTreeNode top = new DefaultMutableTreeNode(
					SafetyNet.slideShow.getDocInf().getHeading());
			treeModel.setRoot(top);
		    createNodesInt(top);
		} catch(Exception e){
			System.out.println("Oh dear... something went wrong in section " +
					"navigation...");
			System.out.println("Looks like you tried to build a tree with " +
					"no input file!");
		}
	}

	/**
	 *	Action listener used to to determine which element of the tree
	 *	has been selected. Calls the go to side X method of engine.
	 */
	public void valueChanged(TreeSelectionEvent e) {
		    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		    contentTree.getLastSelectedPathComponent();

		    if (node == null)
		    //Nothing is selected.
		    return;

		    Object nodeInfo = node.getUserObject();

		    if(!node.isRoot()){
		    	Engine.gotoSlide(((TreeEntity)nodeInfo).getId());
		    	if(Debug.sectionNav)System.out.println(
		    			((TreeEntity)nodeInfo).getId() );
		    } else {
		    	if(Debug.sectionNav)System.out.println("Clicked on the " +
		    			"root... can't go there");
		    }
	}


	/**
	 *	Updates the highlighted slide in section navigation to the current slide
	 *	of the presentaiton.
	 */
	public void updateCurrent(int nextSlide){
		try{
		    setColor(currentSlide, Color.WHITE);
		    setColor(nextSlide, Color.LIGHT_GRAY);
		    currentSlide = nextSlide;
		} catch(Exception e){
			System.out.println("Oh dear... something went wrong in section " +
					"navigation...");
			System.out.println("Looks like you tried to change the current" +
					"/next slide color");
		}
	}

	/**
	 * 	Blank setColor method. Used to initialise the CustomTreeCellRenderer.
	 * 	Should not be used externally but has to be public in order to override
	 * 	the standard java method.
	 */
	public void setColor(){
	    contentTree.setCellRenderer(new CustomTreeCellRenderer());
	}


	/**
	 *	Method used to set the color of a given slide. Uses the slide ID number
	 *	eg starting at 1 and including sections. Can only handle java approved
	 *	colors.
	 */
	public void setColor(int slideId, Color aColor){
		try{
			// update element of slideColors vector with id and color
			slideColors.set(slideId, aColor);
			// this line will do the refresh
		    contentTree.setCellRenderer(new CustomTreeCellRenderer());
		} catch(Exception e){
			System.out.println("Oh dear... something went wrong in section " +
					"navigation...");
			System.out.println("Looks like you tried to color something in!");
		}
	}


    /**
     * Resize method used to update the physical screen dimensions of section
     * navigation to reflect changes in the size of the window.
     *
     * @param bounds is the Rectangle bounds to use.
     */
	public void resize(Rectangle bounds){
		panel.setBounds(bounds);

		int tagWidth = 20;
		int tagHeight = 100;
		int x = bounds.x+panel.getWidth();
		int y = Gui.getContentPane().getHeight()/2-tagHeight/2;
		tagPanel.setBounds(x, y, tagWidth, tagHeight);

		if(Debug.sectionNav)System.out.println("sectionNavResize: "+
				(bounds.x+panel.getWidth())+ " "+y);
		treeScrollView.setBounds(10, 30, 180, panel.getHeight()-66);
		imagePanel.setBounds(0,0, panel.getWidth(), panel.getHeight());

		if(isShimmyVisible()){
			mmSectionNav.reposition(panel.getWidth(),100);
			mmSectionNav.rescale(100, Gui.getContentPane().getHeight()-100);
		}
		else if(!isShimmyVisible()){
			mmSectionNav.reposition(0,100);
			mmSectionNav.rescale(10, Gui.getContentPane().getHeight()-100);
		}

		panel.validate();
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
		if((eventType == ZoneEvents.MOVEMENT || eventType == ZoneEvents.ENTER )&& eventZone.equals("sectionNav")){
			if(Debug.gui)
				System.out.println(eventZone);

			if(triggerShimmyIn());
			else if(triggerShimmyOut());
			else{
				if(Debug.gui)
					System.out.println("SectionNav - do nothing");
			}
		}

	}
	/**
	 *	Private method used by set color to handle the rendering of tree elements.
	 */
	private class CustomTreeCellRenderer extends DefaultTreeCellRenderer{

		private static final long serialVersionUID = 1L;

		public CustomTreeCellRenderer(){
			super();
		}

        public Component getTreeCellRendererComponent(JTree pTree,
            Object slide, boolean pIsSelected, boolean pIsExpanded,
            boolean pIsLeaf, int pRow, boolean pHasFocus)
        {
		    DefaultMutableTreeNode node = (DefaultMutableTreeNode)slide;
		    super.getTreeCellRendererComponent(pTree, slide, pIsSelected,
	                     pIsExpanded, pIsLeaf, pRow, pHasFocus);

		    TreeEntity tempEnt;

		    if(node.isRoot()){
			   setBackgroundNonSelectionColor(Color.white);
		       setBackgroundSelectionColor(Color.LIGHT_GRAY);
		    }else {
			   tempEnt = ((TreeEntity)(node.getUserObject()));
			   setBackgroundNonSelectionColor(slideColors.elementAt(tempEnt.getId()));
		    }
		    return (this);
        }
    };


    /**
     * Custom painting to override the standard java paint methods.
     * Important for some leaf coloring methods.
     *
     * @author Michael Spelling
     *
     */
	private class CustomJPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		public void customJPanel(){
		}

		public void paint(Graphics g) {
			//Graphics2D g2 = (Graphics2D) g;

			// Create the background colour
			//g2.setPaint(new Color(255, 255, 255,0));
			// Fill the background
			//g2.fillRect(0, 0, panel.getSize().width, panel.getSize().height);

			// Call the super paint method to paint the main JTextPane
			super.paint(g);
		}
	}


	/**
	 *	Private method used by update to handle aspects of the tree.
	 *	Used to add leaves to either section or subsection levels.
	 */
	private void createNodesInt(DefaultMutableTreeNode top) {
	    boolean sectionExists = false;
		DefaultMutableTreeNode section = null;
	    DefaultMutableTreeNode slide = null;
	    for (int i = 1; i < SafetyNet.slideShow.getTree().size(); i++){
	    	// Add elements to slide color based on i with default values
	    	slideColors.add(i, Color.white);
	    	if (SafetyNet.slideShow.getTree().get(i) instanceof QuizSlide){
	    		setColor(i, Color.YELLOW);
	    		if (Debug.sectionNav)System.out.println("Colour set to Yellow");
	    	}

	    	// section
	    	if (SafetyNet.slideShow.getTree().get(i).isSection() == true){
	    		section = new DefaultMutableTreeNode(SafetyNet.slideShow.getTree().get(i));
	    	    top.add(section);
	    	    sectionExists = true;

	    	}else{
	    		slide = new DefaultMutableTreeNode(SafetyNet.slideShow.getTree().get(i));
	    		if(!sectionExists){
	    			//section = new DefaultMutableTreeNode(SafetyNet.slideShow.getTree().get(i));
	    			top.add(slide);
	    		}
	    		else section.add(slide);
	    	}
	    }
	    contentTree.expandRow(0); // opens the "top" level folder (eg safetynet)
	    if(Debug.sectionNav)System.out.println("Tree Updated?");
	}



	/**
	 * Specifies the conditions in which the shimmy pane will open.
	 * If the conditions are met, showTimer.start(); is called.
	 * The conditions are:
	 * - show and hide timers are not running
	 * - sectionNav panel is not visible
	 * - coverflow panel is not visible
	 * @return whether the shimmy pane open timer has been triggered
	 */
	public boolean triggerShimmyIn(){
		if(! showTimer.isRunning()
				&& !isShimmyVisible()
				&& !Gui.getCoverFlow().isShimmyVisible()){
			hideTimer.stop();
			showTimer.start();		//show the shimmypane
			mmSectionNav.reposition(panel.getWidth(),100);
			mmSectionNav.rescale(100, Gui.getContentPane().getHeight()-100);
			return true;
		}
		else return false;
	}

	/**
	 * Specifies the conditions in which the shimmy pane will close.
	 * If the conditions are met, hideTimer.start(); is called.
	 * The conditions are:
	 * - show and hide timers are not running
	 * - sectionNav panel is visible
	 * @return whether the shimmy pane close timer has been triggered
	 */
	public boolean triggerShimmyOut(){

		if(! hideTimer.isRunning()
				&& isShimmyVisible()){
			showTimer.stop();
			hideTimer.start();		//hide the shimmypane
			mmSectionNav.reposition(0,100);
			mmSectionNav.rescale(10, Gui.getContentPane().getHeight()-100);
			return true;
		}
		else
			return false;
	}

	/**
	 * Method to get focus when shimmy panes stop.
	 */
	public void shimmyOutStopped(){
		Gui.getMainFrame().requestFocus();
	}

	// Main Function for testing - no javadoc needed.
	public static void main(String[] args) {
		new SafetyNet();
	}
}