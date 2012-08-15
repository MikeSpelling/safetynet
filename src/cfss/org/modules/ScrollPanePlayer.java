/**
 *	Crab Factory Software Solutions
 *
 *  Software:			SafetyNet
 *  Module: 			ScrollPanePlayer
 *  Date Last Edited	08/06/2010
 *
 *  Author: 			Michael Spelling
 *
 *  Contributers:
 *  Testers:			Michael Spelling (Test Plan)
 *  					David Walker (Tester)
 *
 *  Description:		This module enables a vector of entities to be placed
 *						inside a JScrollPane.
 *
 */


package cfss.org.modules;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Vector;

import cfss.org.flags.Debug;
import cfss.org.safetynet.*;
import cfss.org.safetynet.gui.Gui;
import cfss.org.xmlstructure.ScrollPane;
import cfss.org.xmlstructure.Text;
import cfss.org.xmlstructure.XMLImage;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;


/**
 * Class that enables a vector of entities to be displayed inside a single
 * JScrollPane. It uses information stored in the ScrollPane data structure.
 *
 * @author Michael Spelling
 */
public class ScrollPanePlayer extends Entity{
	private static final long serialVersionUID = 1L;

	// Dimension for correct scaling relative to original 800x600 JPanel
	private final Dimension slideDimension = new Dimension(800, 600);

	// Store the panel's width and height in insert and use in resize
	private int panelW = 0;
	private int panelH = 0;

	// Stores the width of the scroll bar to get the correct bounds
	private int scrollBarWidth = 18;

	// Stores the JScrollPane
	private JScrollPane scrollPanel;

	// Stores ScrollPane data structure
	private ScrollPane	scrollPane;

	// Vector to hold all entities to be put in the JScrollPane
	private Vector <Entity> entityVector = new Vector <Entity>();

	// JPanel to place entities on before putting in JScrollPane
	private CustomJPanel panel;


	/**
	 * Constructor sets up the JScrollPane and JPanel which holds the entities
	 * and goes inside the JScrollPane.
	 *
	 * @param inputScrollPane is the ScrollPane data structure that will be
	 * used to create the JScrollPane.
	 */
	public ScrollPanePlayer(ScrollPane inputScrollPane) {
		// Store the ScrollPane data structure
		scrollPane = inputScrollPane;

		// sets z order
		super.setZOrder(inputScrollPane.getZOrder());

		// Set up empty, invisible JPanel of size 1x1 with colour defined in
		// the ScrollPane data structure.
		panel = new CustomJPanel();
		panel.setLayout(null);
		panel.setLocation(0, 0);
		panel.setOpaque(false);
		panel.setVisible(false);

		// Create JScrollPane with JPanel inside it
		scrollPanel = new JScrollPane(panel);
		// Set up to always have a vertical scrollbar and never a horizontal
		scrollPanel.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPanel.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		// Initially set to be invisible
		scrollPanel.setVisible(false);
		scrollPanel.setOpaque(false);

		// Add the JScrollPane to the slidePanel
		Gui.getSlidePanel().add(scrollPanel, new Integer(super.getZOrder()));
	}


	/**
	 * Display loops through entities in the vector of entities, stored in the
	 * class, displaying them inside the JScrollPane.
	 */
	public void display() {
		// Sets it to active
		super.setIsActive(true);

		// Only display vector if there are entities to display
		if(entityVector != null) {
			// Call each entities display method to make them visible
			for(int i = 0; i < entityVector.size(); i++)
				entityVector.get(i).display();
		}

		resize();

		// Set the JPanel and JScrollPane to visible and repaint the slidePanel
		panel.setVisible(true);
		scrollPanel.setVisible(true);
		Gui.getSlidePanel().repaint();
	}


	/**
	 * Overloaded display resets the vector of entities first and then displays
	 * them.
	 *
	 * @param inputEntityVector is the vector of entities to display inside
	 * the JScrollPane.
	 */
	public void display(Vector <Entity> inputEntityVector) {
		entityVector = inputEntityVector;
		display();
	}


	/**
	 * Undisplay loops through entities in the vector of entities, hiding them
	 * all and then hides the JScrollPane.
	 */
	public void unDisplay() {
		// Sets it to false
		super.setIsActive(true);

		// Call all entities unDisplay method to set them as invisible
		for(int i = 0; i < entityVector.size(); i++)
			entityVector.get(i).unDisplay();

		// Set the JPanel and JScrollPane to be invisible and repaint slidePanel
		panel.setVisible(false);
		scrollPanel.setVisible(false);
		Gui.getSlidePanel().repaint();
	}


	/**
	 * hideEntities loops through entities in the vector of entities, hiding them
	 * all.
	 */
	public void hideEntities() {
		if(entityVector != null) {
			// Call all entities unDisplay method to set them as invisible
			for(int i = 0; i < entityVector.size(); i++)
				entityVector.get(i).unDisplay();

			// Set panel size to 0 so it just fills the JScrollPane
			panel.setPreferredSize(new Dimension(0,0));

			Gui.getSlidePanel().repaint();
		}
	}


	/**
	 * Stub to be compatible with IDS, allowing other modules to call it.
	 * The input float is never used, the method simply calls resize().
	 *
	 * @param stub is a float dummy variable that is not used.
	 */
	public void resize(float stub) {
		// Simply call overloaded resize method
		resize();
	}


	/**
	 * Stub to be compatible with IDS, allowing other modules to call it.
	 * The input float is never used, the method simply calls resize().
	 *
	 * @param stub is a double dummy variable that is not used.
	 */
	public void resize(double stub) {
		// Simply call overloaded resize method
		resize();
	}


	/**
	 * Overloaded resize resizes all entities in the JScrollPane and then
	 * resizes the JPanel they are placed on and the JScrollPane.
	 */
	public void resize() {
		// Calculate scaleFactor comparing the slidePanels current size to the
		// original size stored in slideDimension. Make sure it isn't below the
		// minimum.
		double scaleFactor = (double)(Gui.getSlidePanel().getWidth()) /
				(double)slideDimension.width;

		// Calculate new x,y coordinates and width and height using scaleFactor
		// for the JScrollPane
		int newX = (int)(scaleFactor * scrollPane.getStartPoint().getX());
		int newY = (int)(scaleFactor * scrollPane.getStartPoint().getY());
		int scrollWidth =  (int)(scrollPane.getWidth() * scaleFactor);
		int scrollHeight = (int)(scrollPane.getHeight() * scaleFactor);

		// Reset JScrollPanel size to 0 to stop any bugs then set new bounds
		scrollPanel.setSize(0,0);
		scrollPanel.setBounds(newX, newY, scrollWidth, scrollHeight);

		// Calculate the JPanel's new width and height.
		int panelWidth = (int)(panelW * scaleFactor);
		int panelHeight = (int)(panelH * scaleFactor);

		// Set the preferred size of the JPanel and then set the size to be that
		panel.setSize(0,0);
		panel.setPreferredSize(new Dimension(panelWidth, panelHeight));
		panel.setSize(panel.getPreferredSize());

		if(entityVector != null) {
			// Loop through entities calling their resize methods
			for(int i = 0; i < entityVector.size(); i++)
				entityVector.get(i).resize(scaleFactor);
		}
	}


	/**
	 * Method called by entitie's display methods to add themselves to the
	 * JScrollPane.
	 *
	 * @param component is the component(JTextPane or image) to insert into the
	 * JScrollPane.
	 */
	public void insert(Component component) {
		int width = panel.getWidth();
		int height = panel.getHeight();

		// If entity is larger than the JPanel increase the JPanel to the
		// size of the entity
		if(component.getX() + component.getWidth() > width)
			width = component.getX() + component.getWidth();
		if(component.getY() + component.getHeight() > height)
			height = component.getY() + component.getHeight();
		if(width > scrollPanel.getWidth() - scrollBarWidth)
			width = scrollPanel.getWidth() - scrollBarWidth;

		// Store the width and height for use in resize
		panelW = width;
		panelH = height;

		// Set the preferred size of the JPanel and then set the size to be that
		panel.setPreferredSize(new Dimension(width, height));
		panel.setSize(panel.getPreferredSize());

		// Add the component to the JPanel
		panel.add(component);
		panel.validate();
	}


	/**
	 * Method to return the JScrollpane containing the JPanel which may have
	 * entities on it.
	 *
	 * @return scrollPanel is the JScrollPane.
	 */
	public JScrollPane getScrollPanel() {
		return scrollPanel;
	}


	/**
	 * Method to return the vector of entities in the JScrollPane.
	 *
	 * @return entityVector is the vector of entities which are contained in the
	 * JScrollPane.
	 */
	public Vector <Entity> getEntityVector() {
		return entityVector;
	}


	/**
	 * Method to set the vector of entities.
	 *
	 * @param inputEntityVector is the vector of entities to be used in the
	 * JScrollPane.
	 */
	public void setEntityVector(Vector <Entity> inputEntityVector) {
		entityVector = inputEntityVector;
	}

	/**
	 * Method to return the inner panel of the scrollpane.
	 *
	 * @return 'panel' is the content-containing panel within the scrollpane
	 */
	public CustomJPanel getPanel() {
		return panel;
	}

	/**
	 * Custom class extends JPanel to enable the paint method to be
	 * overridden.
	 *
	 * @author Michael Spelling
	 */
	public class CustomJPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		/**
		 * Empty constructor.
		 */
		public CustomJPanel(){
			super();
		}

		/**
		 *  Overridden paint method creates the background for the text.
		 */
		public void paint(Graphics g) {

			// To make this method work the panel must have its background
			// opacity set to false: panel.setOpaque(false);

			Graphics2D g2 = (Graphics2D) g;

			g2.setPaint(new Color(
					(float)((double)(scrollPane.getBkCol().getRed()) / 255),
					(float)((double)(scrollPane.getBkCol().getGreen()) / 255),
					(float)((double)(scrollPane.getBkCol().getBlue()) / 255),
					scrollPane.getBkAlpha()));

			// Fill the background
			g2.fillRect(0, 0, panel.getWidth(),
					panel.getHeight());

			// Call the super paint method to paint the main JTextPane
			super.paint(g);
		}
	}



	////////////////////////////////////////////
	//Main method and testing happens below...//
	////////////////////////////////////////////

	public static void main(String[] args) {
		// Instantiate SafetyNet without producing warnings
		@SuppressWarnings("unused") SafetyNet myApp = new SafetyNet();

		// Run scrollPane tests if true
		testScrollPane();
	}

	// Global variables to allow resizing.
	// Add: ScrollPanePlayer.scrollPanePlayer.resize();
	// to resizeHandler in SafetyNet to allow resizing to work.
	public static TextPlayer textPlayer;
	public static ScrollPanePlayer scrollPanePlayer;

	public static void testScrollPane() {
		// Return if dont want to run test
		if(!Debug.scrollPane)
			return;

		// Variables to chose which tests to run
		final boolean testConstructor = false;
		final boolean testGetScrollPanel = false;
		final boolean testGetSetVector = false;
		final boolean testDisplayUndisplay = false;
		final boolean testHideEntities = false;
		final boolean testResize = false;

		// Set up Text, ScrollPane and XMLImage
		Text someText = new Text();
		someText.setFontSize((float)20);
		someText.setFontName("serif");
		someText.setFontColor(Color.black);
		someText.setFontAlpha((float)1);
		someText.setBackgroundColor(Color.black);
		someText.setBackgroundAlpha((float)0.5);
		someText.setData("Here is a lot of text.");
		someText.getStartPoint().setX(100);
		someText.getStartPoint().setY(100);
		someText.setOnClick(-1);
		someText.setOnClickUrl("www.google.co.uk");

		Text text2 = new Text();
		text2.setFontSize((float)30);
		text2.setFontName("serif");
		text2.setFontColor(Color.red);
		text2.setFontAlpha((float)1);
		text2.setBackgroundColor(Color.black);
		text2.setBackgroundAlpha((float)0.5);
		text2.setData("Second text!!!!");
		text2.getStartPoint().setX(400);
		text2.getStartPoint().setY(400);
		text2.setOnClick(-1);
		text2.setOnClickUrl("www.google.co.uk");

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.getStartPoint().setX(0);
		scrollPane.getStartPoint().setY(300);
		scrollPane.setWidth(800);
		scrollPane.setHeight(300);
		scrollPane.setBkCol(Color.red);
		scrollPane.setBkAlpha((float)0.5);

		XMLImage image1 = new XMLImage();
		image1.setPath("CoverFlow.png");
		image1.getStartPoint().setX(300);
		image1.getStartPoint().setY(100);
		image1.setWidth(250);
		image1.setHeight(150);
		image1.setStartTime(0);
		image1.setEndTime(999999999);
		image1.setOnClick(-2);
		image1.setOnClickUrl("URL 1");

		scrollPanePlayer = new ScrollPanePlayer(scrollPane);

		// Simple test for constructor
		if(testConstructor) {
			Vector <Entity> entityVector = new Vector<Entity>();
			textPlayer = new TextPlayer(someText, scrollPanePlayer);
			entityVector.add(textPlayer);

			someText = new Text();
			someText.setData("Some data");
			someText.getStartPoint().setX(600);
			someText.getStartPoint().setY(600);
			textPlayer = new TextPlayer(someText, scrollPanePlayer);
			entityVector.add(textPlayer);

			scrollPanePlayer.display(entityVector);
		}

		// Simple test to display the JScrollPane
		if(testGetScrollPanel) {
			System.out.print("\n\nTesting getScrollPanel");
			scrollPanePlayer.getScrollPanel().setVisible(true);
			System.out.print("\nAn empty JScrollPane should now be displayed");
			try{Thread.sleep(4000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
		}

		// Test to get and set the entity vector
		if(testGetSetVector) {
			System.out.print("\n\nTesting get/set entityVector");

			Vector <Entity> entityVector = new Vector<Entity>();
			textPlayer = new TextPlayer(someText);
			entityVector.add(textPlayer);

			someText.setData("Some data");
			someText.getStartPoint().setX(200);
			someText.getStartPoint().setY(200);
			textPlayer = new TextPlayer(someText);
			entityVector.add(textPlayer);

			someText.setData("Gee");
			someText.getStartPoint().setX(400);
			someText.getStartPoint().setY(1600);
			textPlayer = new TextPlayer(someText);
			entityVector.add(textPlayer);

			ImagePlayer player1 = new ImagePlayer(image1);
			entityVector.add(player1);

			scrollPanePlayer.setEntityVector(entityVector);
			Vector <Entity> output = scrollPanePlayer.getEntityVector();

			// Display all entities, not in ScrollPane
			for(int i = 0; i < output.size(); i++) {
				output.get(i).display();
			}
			System.out.print("\nEntities should be displayed individually");
		}

		// Test display and unDisplay methods
		if(testDisplayUndisplay) {
			System.out.print("\n\nTesting display and unDisplay");

			// Set up the vector
			Vector <Entity> entityVector = new Vector<Entity>();
			textPlayer = new TextPlayer(someText, scrollPanePlayer);
			entityVector.add(textPlayer);

			someText.setData("Some data");
			someText.getStartPoint().setX(200);
			someText.getStartPoint().setY(200);
			textPlayer = new TextPlayer(someText, scrollPanePlayer);
			entityVector.add(textPlayer);

			someText.setData("Gee");
			someText.getStartPoint().setX(400);
			someText.getStartPoint().setY(1600);
			textPlayer = new TextPlayer(someText, scrollPanePlayer);
			entityVector.add(textPlayer);

			ImagePlayer player1 = new ImagePlayer(image1, scrollPanePlayer);
			entityVector.add(player1);

			// Test alternatively displaying and undisplaying
			scrollPanePlayer.display(entityVector);
			System.out.print("\nDisplay");
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			scrollPanePlayer.unDisplay();
			System.out.print("\nUndisplay");
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			scrollPanePlayer.display();
			System.out.print("\nDisplay");
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			scrollPanePlayer.display();
			System.out.print("\nDisplay");
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			scrollPanePlayer.unDisplay();
			System.out.print("\nUndisplay");
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			scrollPanePlayer.unDisplay();
			System.out.print("\nUndisplay");
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			scrollPanePlayer.display();
			System.out.print("\nDisplay");
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
		}

		// Test hiding the entities
		if(testHideEntities) {
			System.out.print("\n\nTesting Hiding");

			// Set up the vector
			Vector <Entity> entityVector = new Vector<Entity>();
			textPlayer = new TextPlayer(someText, scrollPanePlayer);
			entityVector.add(textPlayer);

			someText.setData("Some data");
			someText.getStartPoint().setX(200);
			someText.getStartPoint().setY(200);
			textPlayer = new TextPlayer(someText, scrollPanePlayer);
			entityVector.add(textPlayer);

			someText.setData("Gee");
			someText.getStartPoint().setX(400);
			someText.getStartPoint().setY(1600);
			textPlayer = new TextPlayer(someText, scrollPanePlayer);
			entityVector.add(textPlayer);

			ImagePlayer player1 = new ImagePlayer(image1, scrollPanePlayer);
			entityVector.add(player1);

			// Test displaying, hiding and undisplaying the entities
			scrollPanePlayer.display();
			System.out.print("\nDisplayed with no entities");
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			scrollPanePlayer.display(entityVector);
			System.out.print("\nDisplay");
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			scrollPanePlayer.hideEntities();
			System.out.print("\nhideEntities");
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			someText.setData("Mike");
			someText.getStartPoint().setX(600);
			someText.getStartPoint().setY(0);
			textPlayer = new TextPlayer(someText, scrollPanePlayer);
			entityVector.add(textPlayer);
			scrollPanePlayer.setEntityVector(entityVector);

			scrollPanePlayer.display();
			System.out.print("\nDisplay");
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			scrollPanePlayer.display();
			System.out.print("\nDisplay");
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			scrollPanePlayer.hideEntities();
			System.out.print("\nhideEntities");
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			scrollPanePlayer.hideEntities();
			System.out.print("\nhideEntities");
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			scrollPanePlayer.display();
			System.out.print("\nDisplay");
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			scrollPanePlayer.hideEntities();
			System.out.print("\nhideEntities");
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			scrollPanePlayer.unDisplay();
			System.out.print("\nUndisplay");
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			scrollPanePlayer.display();
			System.out.print("\nDisplay");
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
		}

		////////////////////////////////////////////////////////////////////////
		////	Add: TextPlayer.textPlayer.resize(); to resizeHandler in    ////
		////	SafetyNet to allow resizing to work.						////
		////////////////////////////////////////////////////////////////////////
		if(testResize) {
			System.out.print("\n\nTesting resize");
			System.out.print("\nRemeber to add:" +
					" ScrollPanePlayer.scrollPanePlayer.resize();" +
					" to resizeHandler");

			Vector <Entity> entityVector = new Vector<Entity>();
			textPlayer = new TextPlayer(someText, scrollPanePlayer);
			entityVector.add(textPlayer);

			someText.setData("Some data");
			someText.getStartPoint().setX(200);
			someText.getStartPoint().setY(200);
			textPlayer = new TextPlayer(someText, scrollPanePlayer);
			entityVector.add(textPlayer);

			someText.setData("Gee");
			someText.getStartPoint().setX(400);
			someText.getStartPoint().setY(1600);
			textPlayer = new TextPlayer(someText, scrollPanePlayer);
			entityVector.add(textPlayer);

			ImagePlayer player1 = new ImagePlayer(image1, scrollPanePlayer);
			entityVector.add(player1);

			scrollPanePlayer.display(entityVector);
			System.out.print("\nDisplayed");
			System.out.print("\nTry resizing to test it works.");
		}
	}
}