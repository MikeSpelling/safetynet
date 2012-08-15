/**
 *	Crab Factory Software Solutions
 *
 *  Software:			SafetyNet
 *  Module: 			TextPlayer
 *  Date Last Edited	17/06/2010
 *
 *  Author: 			Michael Spelling
 *  Contributers:		Mark Wrightson
 *  Testers:			Michael Spelling (Test Plan)
 *
 *  Description:		This module enables a JTextPane to be created and
 *   					displayed on the main JPanel. It also includes
 *   					methods to enable resizing and can handle text which
 *   					contains links.
 */


package cfss.org.modules;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import cfss.org.xmlstructure.Text;
import cfss.org.xmlstructure.XMLPoint;
import cfss.org.LocalMouseMonitor.LocalMouseMonitor;
import cfss.org.LocalMouseMonitor.MouseMonitorListener;
import cfss.org.LocalMouseMonitor.ZoneEvents;
import cfss.org.flags.Debug;
import cfss.org.safetynet.*;
import cfss.org.safetynet.gui.Gui;


/**
 * Class that enables data in the Text data structure to be displayed in a
 * JTextPane on the screen. Methods for displaying, hiding and resizing are
 * available. It is compatible with ScrollPanePlayer so that it can be displayed
 * inside a JScrollPane.
 *
 * @author Michael Spelling
 */
public class TextPlayer extends Entity {

	private static final long serialVersionUID = 1L;

	// Variables for link information
	private int linkSlideId = -1;
	private String linkUrl = null;

	// Stores the width of the scroll bar to get the correct bounds
	private int scrollBarWidth = 0;

	// All font sizes will be relative to this 800x600 dimension
	private final Dimension slideDimension = new Dimension(800, 600);

	// Create backgroundColor and backgroundAlpha to use in paint method
	private Color backgroundColor = null;
	private float backgroundAlpha;

	// The JTextPane that text will be put on
	private JTextPane textPane = null;

	// The Text data structure that holds the information to display
	private Text text = null;

	// ScrollPanePlayer to use if JTextPane needs to be in a JScrollPane
	private ScrollPanePlayer scrollPanePlayer = null;

	//Mouse monitor for on-click
	private LocalMouseMonitor textLMM = null;


	/**
	 * Constructor creates an empty JTextPane.
	 */
	public TextPlayer() {
		// Create new CustomJTextPane which is not editable
		textPane = new CustomJTextPane();
		textPane.setEditable(false);
		textPane.setOpaque(false);
		textPane.setVisible(false);

		// Add the JTextPane to the slidePanel
		Gui.getSlidePanel().add(textPane, new Integer(super.getZOrder()));
	}


	/**
	 * Copy constructor.
	 *
	 * @param inputTextPlayer is the TextPlayer to be copied into the current
	 * TextPlayer.
	 *
	 */
	public TextPlayer(TextPlayer inputTextPlayer) {
		// Retrieve the JTextPane, Text and ScrollPanePlayer
		textPane = inputTextPlayer.getTextPane();
		text = inputTextPlayer.getText();
		scrollPanePlayer = inputTextPlayer.getScrollPanePlayer();

		// Call update to set up text attributes but not display as visible yet
		update(text);
		// Call resize to set the textPane up to the correct size
		resize();

		// Sets its layer on the panel
		super.setZOrder(text.getZOrder());

		textPane.setVisible(false);

		// Add the JTextPane to the slidePanel if it isnt on a JScrollPane
		if(scrollPanePlayer != null)
			scrollPanePlayer.insert(textPane);
		else {
			Gui.getSlidePanel().add(textPane, new Integer(super.getZOrder()));
		}
	}


	/**
	 * Constructor without JScrollPane for normal JTextPanes containing the
	 * information stored in Text.
	 *
	 * @param inputText is the Text to be used by the TextPlayer.
	 */
	public TextPlayer(Text inputText) {
		// Create new CustomJTextPane which is not editable
		textPane = new CustomJTextPane();
		textPane.setEditable(false);
		textPane.setOpaque(false);

		// Call update to set up text attributes but not display as visible yet
		update(inputText);
		// Call resize to set the textPane up to the correct size
		resize();

		// Sets its layer on the panel
		super.setZOrder(inputText.getZOrder());

		textPane.setVisible(false);
		// Add the JTextPane to the slidePanel
		Gui.getSlidePanel().add(textPane,new Integer(super.getZOrder()));
	}


	/**
	 * Constructor with JScrollPane and Text to put a JTextPane with the
	 * specified text into the entered ScrollPanePlayer.
	 *
	 * @param inputText is the Text to be used by the TextPlayer.
	 * @param inputScrollPanePlayer is the ScrollPanePlayer that TextPlaye will
	 * add its JTextPane to.
	 */
	public TextPlayer(Text inputText, ScrollPanePlayer inputScrollPanePlayer) {
		// Store the ScrollPanePlayer
		scrollPanePlayer = inputScrollPanePlayer;

		// Create new CustomJTextPane which is not editable
		textPane = new CustomJTextPane();
		textPane.setEditable(false);
		textPane.setOpaque(false);

		// Call update to set the text attributes but not display as visible yet
		update(inputText);
		// Call resize to set the textPane up to the correct size
		resize();

		// Sets its layer on the panel
		super.setZOrder(inputText.getZOrder());

		textPane.setVisible(false);
		// Add the JTextPane to the slidePanel if it isnt on a JScrollPane
		if(scrollPanePlayer != null)
			scrollPanePlayer.insert(textPane);
		else {
			Gui.getSlidePanel().add(textPane, new Integer(super.getZOrder()));
		}
	}


	/**
	 * Method that updates the current Text to the one passed in and then
	 * displays it.
	 *
	 * @param inputText is the Text to be used by the TextPlayer.
	 */
	public void display(Text inputText) {
		// Update the text first
		update(inputText);

		// Display the JTextPane
		display();
	}


	/**
	 * Overloaded display method displays whatever is currently in the
	 * JTextPane. If there is a ScrollPanePlayer then it will insert itself
	 * into the ScrollPanePlayer, otherwise it will add itself to the main
	 * slidePanel. It will also create a local mouse monitor zone if the text
	 * is clickable.
	 */
	public void display() {
		// Sets flag to active
		super.setIsActive(true);

		// Resize to the right size before displaying if there is text
		// but not if there is a scrollPanePlayer as this will resize it
		if(text != null && scrollPanePlayer == null)
			resize();

		// Set the JTextPane to be visible
		textPane.setVisible(true);

		// If text is clickable create a mouse zone for it
		if(text != null) {
			if(text.getOnClick() >= 0) {
				textLMM = new LocalMouseMonitor(new MouseMonitor(),
						textPane.getLocation(), textPane.getSize());
				textLMM.setNotifications(true, false, false, true, false,
						false);

				textLMM.addZone("TextPane", new Point(0, 0),
						textPane.getSize());
			}
		}
	}


	/**
	 * Display hides the JTextPane by setting it to be invisible. It also
	 * removes the local mouse monitor if one was instantiates.
	 */
	public void unDisplay() {
		// Sets flag to inactive
		super.setIsActive(false);

		// Set the JTextPane to be invisible
		textPane.setVisible(false);

		// Destroy any active mouse monitor
		if(textLMM != null)
			textLMM.destroy();
	}


	/**
	 * As this was in the IDS it is included but it simply calls the overloaded
	 * resize method.
	 *
	 * @param stub is an unused float.
	 */
	public void resize(float stub) {
		resize();
	}


	/**
	 * This is included to provide compatibility but it simply calls the
	 * overloaded resize method.
	 *
	 * @param stub is an unused double.
	 */
	public void resize(double stub) {
		resize();
	}


	/**
	 * Resizes the JTextPane by comparing the slidePanel's current size to the
	 * reference slideDimension which is 800x600.
	 */
	public void resize() {
		// Calculates scaleFactor by comparing current size to the class
		// variable slideDimension
		double scaleFactor = (double)(Gui.getSlidePanel().getWidth())
			/ (double)slideDimension.width;

		// Reset textPane before changing it to stop any bugs
		textPane.setSize(0,0);

		// Set the new font size scaled by the scale factor
		Font font = new Font(text.getFontName(), Font.PLAIN,
				(int)(scaleFactor * text.getFontSize()));
		textPane.setFont(font);

		// Determine new x,y coordinates
		int newX = (int)(scaleFactor * text.getStartPoint().getX());
		int newY = (int)(scaleFactor * text.getStartPoint().getY());

		// Set the width and if it is wider than the page, set it to the maximum
		// possible width.
		int width = textPane.getPreferredSize().width;
		if(width + newX > Gui.getSlidePanel().getWidth() - scrollBarWidth)
			width = Gui.getSlidePanel().getWidth() - scrollBarWidth;
		// Set height to the preferred height of the JTextPane.
		int height = textPane.getPreferredSize().height;

		// Reset bounds to the new font size and repaint;
		textPane.setBounds(newX, newY, width, height);

		if(textLMM != null) { // Stops null pointer exception
			// Scale the clickable area
			if(textLMM.retrieveZone("TextPane") != null) {
				textLMM.reposition(newX, newY);
				textLMM.rescale(width, height);
			}
		}
	}


	/**
	 * Called when user clicks on clickable text (URL or slideID).
	 * It either opens up the default browser at the specified URL or
	 * calls engine to go to the relevant slide.
	 */
	public void gotoLink() {
		// If link was a web link use BrowserLoader to open the URL which was
		// stored when text was updated
		if(linkUrl != null)
			BrowserLoader.openURL(linkUrl);

		// If link was a slide link then use Engine to go to the appropriate
		// slide which was also stored when text was updated
		else if(linkSlideId > 0)
			Engine.gotoSlide(linkSlideId);
	}


	/**
	 * Method that updates the TextPlayer to contain the Text passed in.
	 * It sets all information regarding the style of text.
	 */
	public void update(Text inputText) {
		// Sets text to the inputText
		text = inputText;

		// Resets the background color and opacity for the paint method
		backgroundColor = text.getBackgroundColor();
		backgroundAlpha = text.getBackgroundAlpha();

		// Create a document from the JTextPane
		MutableAttributeSet attrs = textPane.getInputAttributes();
		StyledDocument doc = textPane.getStyledDocument();

		// Determines what to do if text has link information
		if(text.getOnClick() >= 0) {
			// If text is a link then underline it
			StyleConstants.setUnderline(attrs, true);
			// If it is a web link store the string in the class variable
			if(text.getOnClick() == 0) {
				linkUrl = text.getOnClickUrl();
				linkSlideId = -1;
			}
			// If it is a slide link store the number in the class variable
			else {
				linkSlideId = text.getOnClick();
				linkUrl = null;
			}
		}
		// If there is no link information reset to null and -1 and
		// don't underline
		else {
			StyleConstants.setUnderline(attrs, false);
			linkUrl = null;
			linkSlideId = -1;
		}

		// Determine whether data is HTML or not and set the ContentType
		if(text.getData().regionMatches(0, "<html>", 0, 6))
			textPane.setContentType("text/html");

		// Set font colour with transparencies.
		textPane.setForeground(new Color(
        		(float)(double)(text.getFontColor().getRed()) / 255,
        		(float)(double)(text.getFontColor().getGreen()) / 255,
        		(float)(double)(text.getFontColor().getBlue()) / 255,
        		text.getFontAlpha()));

		// Set the JTextPane to the font and data.
		textPane.setText(text.getData());

        // Set the document attributes (This allows underlining)
		doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
	}


	/**
	 * Method to return the textPane.
	 *
	 * @return textPane is the returned JTextPane.
	 */
	public JTextPane getTextPane() {
		return textPane;
	}


	/**
	 *  Method to return the text.
	 *
	 *  @return text is the returned Text.
	 */
	public Text getText() {
		return text;
	}


	/**
	 * Method to return the scrollPanePlayer.
	 *
	 * @return scrollPanePlayer is the returned ScrollPanePlayer.
	 */
	public ScrollPanePlayer getScrollPanePlayer() {
		return scrollPanePlayer;
	}


	/**
	 * Method to set the text. This won't be a visible change until update() is
	 * called.
	 *
	 * @param inputText is the Text to set the Text in TextPlayer to.
	 */
	public void setText(Text inputText) {
		text = inputText;
	}


	/**
	 * Method to set the scrollPanePlayer and update to it.
	 *
	 * @param inputScrollPanePlayer is the ScrollPanePlayer to update to.
	 */
	public void setScrollPanePlayer(ScrollPanePlayer inputScrollPanePlayer) {
		scrollPanePlayer = inputScrollPanePlayer;
		update(text);
	}


	/**
	 * Custom class extends JTextPane to enable the paint method to be
	 * overridden.
	 *
	 * @author Michael Spelling
	 */
	private class CustomJTextPane extends JTextPane {
		private static final long serialVersionUID = 1L;

		/**
		 * Empty constructor.
		 */
		public CustomJTextPane(){
		}

		/**
		 *  Overridden paint method creates the background for the text.
		 */
		public void paint(Graphics g) {
			// To make this method work the panel must have its background
			// opacity set to false: panel.setOpaque(false);
			Graphics2D g2 = (Graphics2D) g;

			// Create the background colour with transparencies
			g2.setPaint(new Color(
					(float)((double)(backgroundColor.getRed()) / 255),
					(float)((double)(backgroundColor.getGreen()) / 255),
					(float)((double)(backgroundColor.getBlue()) / 255),
					backgroundAlpha));

			// Fill the background
			g2.fillRect(0, 0, textPane.getWidth(), textPane.getHeight());

			// Call the super paint method to paint the main JTextPane
			super.paint(g);
		}
	}


	/**
	 * Class to capture mouse events for clickable text.
	 *
	 * @author Michael Spelling
	 */
	private class MouseMonitor implements MouseMonitorListener {
		/**
		 * Blank implementation of unneeded drag monitoring method
		 */
		public void dragEventOccurred(String s, int x, int y,
			Boolean complete){}

		/**
		 * Called if mouse clicks in a monitored zone.
		 */
		public void zoneEventOccurred(String eventZone, int eventType){
			// If within the bounds of the text, go to the link

			if(eventType==ZoneEvents.ENTER && eventZone.equals("TextPane")){
					Gui.changeCursor(Cursor.HAND_CURSOR);

			}
			else if (eventType==ZoneEvents.CLICK && eventZone.equals("TextPane"))
				gotoLink();
		}
	}


	////////////////////////////////////////////
	//Main method and testing happens below...//
	////////////////////////////////////////////

	// Global variable to allow resizing.
	// Add: TextPlayer.textPlayer.resize();
	// to resizeHandler in SafetyNet to allow resizing to work.
	public static TextPlayer textPlayer;


	public static void main(String[] args) {
		// Instantiate SafetyNet without producing warnings
		@SuppressWarnings("unused")	SafetyNet myApp = new SafetyNet();

		// Run text tests if true
		testText();
	}


	public static void testText() {
		// Return if dont want to run test
		if(!Debug.text)
			return;

		// Variables to chose which tests to run
		final boolean testFontSize = false;
		final boolean testFontColor = false;
		final boolean testFontAlpha = false;
		final boolean testFontName = false;
		final boolean testBackgroundColor = false;
		final boolean testBackgroundAlpha = false;
		final boolean testData = false;
		final boolean testConstructor = false;
		final boolean testDisplayUndisplay = false;
		final boolean testResize = false;
		final boolean testLinks = false;
		final boolean testHtml = false;
		final boolean testPosition = false;

		// Set up some text
		Text text = new Text();
		text.setFontSize((float)20);
		text.setFontName("serif");
		text.setFontColor(Color.black);
		text.setFontAlpha((float)1);
		text.setBackgroundColor(Color.white);
		text.setBackgroundAlpha((float)1);
		text.setData("Here is a lot of text.");
		text.getStartPoint().setX(100);
		text.getStartPoint().setY(100);
		text.setOnClick(-2);
		text.setOnClickUrl("www.google.co.uk");

		// Font Size Testing
		if(testFontSize) {
			System.out.print("\n\nTesting Font Size");
			textPlayer = new TextPlayer(text);

			// Test within normal range that font resizes properly
			for(int n = 1; n <= 100; n++) {
				text.setFontSize((float)n);
				text.setData("Font Size: " + text.getFontSize());
				textPlayer.display(text);
				System.out.print("\n" + (n*100)/100 + "%\t" +
						text.getFontSize());
				try{Thread.sleep(250);}
				catch(InterruptedException ie){System.out.print(
						"Error in pause");}
			}

			// Test extreme values

			text.setFontSize((float)-999);
			textPlayer.display(text);
			System.out.print("\nFont size: " + text.getFontSize());
			try{Thread.sleep(5000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			text.setFontSize((float)-1.2);
			textPlayer.display(text);
			System.out.print("\nFont size: " + text.getFontSize());
			try{Thread.sleep(5000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			text.setFontSize((float)0);
			textPlayer.display(text);
			System.out.print("\nFont size: " + text.getFontSize());
			try{Thread.sleep(5000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			text.setFontSize((float)999);
			textPlayer.display(text);
			System.out.print("\nFont size: " + text.getFontSize());
			try{Thread.sleep(5000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
		}

		// Font Colour Testing
		if(testFontColor) {
			System.out.print("\n\nTesting Font Colour");
			text.setBackgroundColor(Color.white);
			text.setBackgroundAlpha(1);
			textPlayer = new TextPlayer(text);
			String data;

			// Test within normal range that font recolours properly
			System.out.print("\nTesting valid colours");
			for(int r = 0; r <= 255; r=r+25) {
				for(int g = 0; g <= 255; g=g+25) {
					for(int b = 0; b <= 255; b=b+25) {
						data = "Font Color: " + r + " " + g +
							" " + b;
						text.setData(data);
						text.setFontColor(new Color(r,g,b));
						textPlayer.display(text);
						System.out.print("\nFont Color: " + r + " " + g +
								" " + b);
						try{Thread.sleep(1);}
						catch(InterruptedException ie){System.out.print(
								"Error in pause");}
					}
				}
			}

			// Text extreme values
			System.out.print("Testing extreme values");
			text.setFontColor(new Color(0,0,0,0));
			textPlayer.display(text);
			System.out.print("\nFont Color: " + text.getFontColor().getRed() +
					" " + text.getFontColor().getGreen() + " " +
					text.getFontColor().getBlue() + " " +
					text.getFontColor().getAlpha());
			try{Thread.sleep(1000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			text.setFontColor(new Color(999,666,331,69696969));
			textPlayer.display(text);
			System.out.print("\nFont Color: " + text.getFontColor().getRed() +
					" " + text.getFontColor().getGreen() + " " +
					text.getFontColor().getBlue() + " " +
					text.getFontColor().getAlpha());
			try{Thread.sleep(1000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			text.setFontColor(new Color((float)1.325,(float)213.213,
					(float)7575.00123,(float)123.321));
			textPlayer.display(text);
			System.out.print("\nFont Color: " + text.getFontColor().getRed() +
					" " + text.getFontColor().getGreen() + " " +
					text.getFontColor().getBlue() + " " +
					text.getFontColor().getAlpha());
			try{Thread.sleep(1000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			text.setFontColor(new Color(-1,-0,-333,-65789));
			textPlayer.display(text);
			System.out.print("\nFont Color: " + text.getFontColor().getRed() +
					" " + text.getFontColor().getGreen() + " " +
					text.getFontColor().getBlue() + " " +
					text.getFontColor().getAlpha());
			try{Thread.sleep(1000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
		}

		// Font Alpha Testing
		if(testFontAlpha) {
			System.out.print("\n\nTesting Font Alpha");
			text.setBackgroundColor(Color.white);
			text.setBackgroundAlpha(1);
			textPlayer = new TextPlayer(text);
			String data;

			// Test within normal range that font recolours properly
			System.out.print("\nTesting valid alpha values");
			for(float n = 0; n < 1; n = n + (float)0.01) {
				data = "Font Alpha: " + n;
				text.setData(data);
				text.setFontAlpha(n);
				textPlayer.display(text);
				System.out.print("\n" + (int)(100*n) + "%\t" +
						text.getFontAlpha());
				try{Thread.sleep(50);}
				catch(InterruptedException ie){System.out.print(
						"Error in pause");}
			}

			// Test extreme range
			System.out.print("\nTesting extreme values");
			text.setFontAlpha((float)-999);
			textPlayer.display(text);
			System.out.print("\nFont Alpha: " + text.getFontAlpha());
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			text.setFontAlpha((float)999);
			textPlayer.display(text);
			System.out.print("\nFont Alpha: " + text.getFontAlpha());
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
		}

		// Font Name Testing
		if(testFontName) {
			System.out.print("\n\nTesting Font Names");
			textPlayer = new TextPlayer(text);

			GraphicsEnvironment env = GraphicsEnvironment.
				getLocalGraphicsEnvironment();
			Font[] fonts = env.getAllFonts();

			for(int n = 0; n < fonts.length; n++)
				System.out.print("\n" + fonts[n].getFontName());

			System.out.print("\nTesting valid fonts");
			for(int n = 0; n < fonts.length; n++) {
				text.setFontName(fonts[n].getFontName());
				text.setData(fonts[n].getFontName());
				textPlayer.display(text);
				System.out.print("\n" + (n*100)/(fonts.length-1) + "%\t" +
						text.getFontName());
				try{Thread.sleep(200);}
				catch(InterruptedException ie){System.out.print(
						"Error in pause");}
			}

			System.out.print("Testing invalid fonts");

			text.setFontName("This isnt a valid font");
			text.setData(text.getFontName());
			textPlayer.display(text);
			System.out.print("\n" + text.getFontName());
			try{Thread.sleep(200);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			text.setFontName("1234");
			text.setData(text.getFontName());
			textPlayer.display(text);
			System.out.print("\n" + text.getFontName());
			try{Thread.sleep(200);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
		}

		// Background Color Testing
		if(testBackgroundColor) {
			System.out.print("\n\nTesting Background Colour");
			textPlayer = new TextPlayer(text);
			String data;

			System.out.print("\nTesting valid colours");
			for(int r = 0; r <= 255; r=r+25) {
				for(int g = 0; g <= 255; g=g+25) {
					for(int b = 0; b <= 255; b=b+25) {
						data = "Background Color: " + r + " " + g +
							" " + b;
						text.setData(data);
						text.setBackgroundColor(new Color(r,g,b));
						text.setFontColor(new Color(255-r, 255-g, 255-b));
						textPlayer.display(text);
						System.out.print("\nBackground Color: " + r + " " + g +
								" " + b);
						try{Thread.sleep(1);}
						catch(InterruptedException ie){System.out.print(
								"Error in pause");}
					}
				}
			}

			// Text extreme values
			System.out.print("\nTesting extreme values");
			text.setBackgroundColor(new Color(0,0,0,0));
			textPlayer.display(text);
			System.out.print("\nBackground Color: " +
					text.getBackgroundColor().getRed() + " " +
					text.getBackgroundColor().getGreen() + " " +
					text.getBackgroundColor().getBlue() + " " +
					text.getBackgroundColor().getAlpha());
			try{Thread.sleep(1000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			text.setBackgroundColor(new Color(999,666,331,69696969));
			textPlayer.display(text);
			System.out.print("\nBackground Color: " +
					text.getBackgroundColor().getRed() + " " +
					text.getBackgroundColor().getGreen() + " " +
					text.getBackgroundColor().getBlue() + " " +
					text.getBackgroundColor().getAlpha());
			try{Thread.sleep(1000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			text.setBackgroundColor(new Color((float)1.325,(float)213.213,
					(float)7575.00123,(float)123.321));
			textPlayer.display(text);
			System.out.print("\nFont Color: " +
					text.getBackgroundColor().getRed() + " " +
					text.getBackgroundColor().getGreen() + " " +
					text.getBackgroundColor().getBlue() + " " +
					text.getBackgroundColor().getAlpha());
			try{Thread.sleep(1000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			text.setBackgroundColor(new Color(-1,-0,-333,-65789));
			textPlayer.display(text);
			System.out.print("\nFont Color: " +
					text.getBackgroundColor().getRed() + " " +
					text.getBackgroundColor().getGreen() + " " +
					text.getBackgroundColor().getBlue() + " " +
					text.getBackgroundColor().getAlpha());
			try{Thread.sleep(1000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
		}

		// Background Alpha Testing
		if(testBackgroundAlpha) {
			System.out.print("\n\nTesting Background Alpha");
			text.setFontColor(Color.black);
			text.setFontAlpha(1);
			text.setBackgroundColor(Color.white);
			textPlayer = new TextPlayer(text);
			String data;

			// Test within normal range that font recolours properly
			System.out.print("\nTesting valid alpha values");
			for(float n = 0; n < 1; n = n + (float)0.01) {
				data = "Background Alpha: " + n;
				text.setData(data);
				text.setBackgroundAlpha(n);
				textPlayer.display(text);
				System.out.print("\n" + (int)(100*n) + "%\t" +
						text.getBackgroundAlpha());
				try{Thread.sleep(100);}
				catch(InterruptedException ie){System.out.print(
						"Error in pause");}
			}

			// Test extreme range
			System.out.print("\nTesting extreme values");
			text.setBackgroundAlpha((float)-999);
			textPlayer.display(text);
			System.out.print("\nFont Alpha: " + text.getBackgroundAlpha());
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			text.setBackgroundAlpha((float)999);
			textPlayer.display(text);
			System.out.print("\nFont Alpha: " + text.getBackgroundAlpha());
			try{Thread.sleep(2000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
		}

		// Data Testing
		if(testData) {
			System.out.print("\n\nTesting Data");
			textPlayer = new TextPlayer(text);
			String data = "Large data:";

			System.out.print("\nData: None");
			text.setData("");
			System.out.print("\nData: " + text.getData());
			textPlayer.display(text);
			try{Thread.sleep(5000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			System.out.print("\nData: 12342345Some String");
			text.setData(1 + "" + 23452345 + "Some String");
			System.out.print("\nData: " + text.getData());
			textPlayer.display(text);
			try{Thread.sleep(5000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			System.out.print("\nData: Includes string and large number of " +
					"variables");
			for(int n = 0; n < 100; n++)
				data = data + " " + n;
			text.setData(data);
			System.out.print("\nData: " + text.getData());
			textPlayer.display(text);
			try{Thread.sleep(5000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
		}

		// Test Constructor
		if(testConstructor) {
			System.out.print("\n\nTesting Constructor");
			textPlayer = new TextPlayer(text);
			System.out.print("\nConstructed with text. Not displayed yet.");
			try{Thread.sleep(5000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			textPlayer.display();
			System.out.print("\nDisplay now called");
			try{Thread.sleep(5000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			textPlayer.unDisplay();
			textPlayer = new TextPlayer();
			System.out.print("\nConstructed empty.");
			try{Thread.sleep(5000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			textPlayer.display();
			System.out.print("\nDisplay now called. Nothing here right?");
			try{Thread.sleep(5000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			textPlayer.display(text);
			System.out.print("\nDisplay with text now called");
			try{Thread.sleep(5000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
		}

		// Test display and unDisplay methods
		if(testDisplayUndisplay) {
			System.out.print("\n\nTesting Display");
			textPlayer = new TextPlayer(text);
			textPlayer.display();
			System.out.print("\nOriginal text should be displayed");
			try{Thread.sleep(5000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			textPlayer.unDisplay();
			System.out.print("\nText should now be hidden");
			try{Thread.sleep(5000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			text.setData("New data");
			text.setFontName("Arial");
			text.setFontColor(Color.red);
			text.setFontAlpha((float)0.5);
			text.setBackgroundColor(Color.blue);
			text.setBackgroundAlpha((float)0.5);

			textPlayer.display();
			System.out.print("\nOriginal text should still be displayed");
			try{Thread.sleep(3000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			textPlayer.unDisplay();
			System.out.print("\nText should now be hidden");
			try{Thread.sleep(3000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			textPlayer.display(text);
			System.out.print("\nNew text should now be displayed");
			try{Thread.sleep(3000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			textPlayer.display(text);
			System.out.print("\nDisplay called again");
			try{Thread.sleep(3000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			textPlayer.display(text);
			System.out.print("\nAnd again");
			try{Thread.sleep(3000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			textPlayer.unDisplay();
			System.out.print("\nUndisplay called");
			try{Thread.sleep(3000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			textPlayer.unDisplay();
			System.out.print("\nAnd again");
			try{Thread.sleep(3000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			textPlayer.display(text);
			System.out.print("\nDisplayed");
			try{Thread.sleep(3000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
		}

		// Test resizing
		////////////////////////////////////////////////////////////////////////
		////	Add: TextPlayer.textPlayer.resize(); to resizeHandler in    ////
		////	SafetyNet to allow resizing to work.						////
		////////////////////////////////////////////////////////////////////////
		if(testResize) {
			System.out.print("\n\nTesting Resize");
			System.out.print("\nRemeber to add:" +
					" TextPlayer.textPlayer.resize();" +
					" to resizeHandler");

			textPlayer = new TextPlayer(text);
			text.setData("New data");
			text.setFontName("Arial");
			text.setFontSize((float)20);
			text.setFontColor(Color.red);
			text.setFontAlpha((float)0.5);
			text.setBackgroundColor(Color.blue);
			text.setBackgroundAlpha((float)0.5);
			textPlayer.display(text);
			System.out.print("\nTry resizing the window to test text is " +
					"properly displayed");
		}

		// Link Testing
		if(testLinks) {
			System.out.print("\n\nTesting Links");
			textPlayer = new TextPlayer(text);

			// Test a Url link
			text.setOnClick(0);
			text.setOnClickUrl("http://voltnet.co.uk/sweng/testing/class/id" +
					"/8?&response=125");
			text.setData(text.getOnClickUrl());
			textPlayer.display(text);
			System.out.print("\nUrl Link should be underlined");
			try{Thread.sleep(5000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			textPlayer.gotoLink();
			System.out.print("\nDefault browser should be opened");
			try{Thread.sleep(5000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			text.setOnClick(23);
			text.setData("" + text.getOnClick());
			textPlayer.display(text);
			textPlayer.gotoLink();
			System.out.print("\nEngine should have done nothing as the slide " +
					"ID is invalid");
			try{Thread.sleep(5000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			text.setOnClick(-1);
			text.setData("This is normal text");
			textPlayer.display(text);
			System.out.print("\nThis should be normal, not underlined text.");
			textPlayer.gotoLink();
		}

		// HTML Testing
		if(testHtml) {
			System.out.print("\n\nTesting Html");

			// Set some HTML data
			text.setData("<html><body><h1>A HTML Heading</h1><p>A HTML " +
					"paragraph.<br>Another one</p></body></html>");
			textPlayer = new TextPlayer(text);

			textPlayer.display(text);
			System.out.print("\nText should be displayed as html");
			try{Thread.sleep(5000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			text.setData("A HTML Heading A HTML paragraph. Another one");
			textPlayer.display(text);
			System.out.print("\nText should be normal");
		}

		// Position testing
		if(testPosition) {
			System.out.print("\n\nTesting Position");

			XMLPoint point = new XMLPoint();
			textPlayer = new TextPlayer(text);

			// Test valid range
			for(int x = 0; x < Gui.getSlidePanel().getWidth(); x=x+10) {
				for(int y=0; y<Gui.getSlidePanel().getHeight(); y=y+10) {
					System.out.print("\nX: " + x + "\tY: " + y);
					point.setX(x);
					point.setY(y);
					text.setStartPoint(point);
					textPlayer.display(text);
					try{Thread.sleep(10);}
					catch(InterruptedException ie){System.out.print(
							"Error in pause");}
				}
			}
			try{Thread.sleep(3000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			// Test extreme range

			point.setX(-999);
			point.setY((int)-0.234);
			System.out.print("\nX: " + point.getX() + "\tY: " + point.getY());
			text.setStartPoint(point);
			textPlayer.display(text);
			try{Thread.sleep(3000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}

			point.setX(67896789);
			point.setY((int)5697904);
			System.out.print("\nX: " + point.getX() + "\tY: " + point.getY());
			text.setStartPoint(point);
			textPlayer.display(text);
			try{Thread.sleep(3000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
		}
	}
}