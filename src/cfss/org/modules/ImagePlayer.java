package cfss.org.modules;

/*------------------------------------------------------------------------------
	Crab Factory Software Solutions

	Software:		SafetyNet
	Module:			ImagePlayer

	Author:			Miohael Angus
	Contributors:	Michael Spelling
	Testers:


	Description: A self contained module for displaying images on a slide
		-It may be used by instantiating it and calling displayImage().
		-All positioning, sizing and timing is controlled by the module.
		-Functionality is provided for zooming in on the image.
		-arrowFolderPath and exitButtonPath must be set to reference the
			correct folders in which the arrow- and exit button icons can
			be found
		-A number of debug messages are output to console at key points in
			the code. These may be enabled or disabled by changing the
			value of the Boolean instance variable 'debug'

------------------------------------------------------------------------------*/



//import ScrollPanePlayer;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import java.io.*;
import cfss.org.safetynet.BrowserLoader;
import cfss.org.safetynet.Engine;
import cfss.org.safetynet.Entity;
import cfss.org.safetynet.SafetyNet;
import cfss.org.safetynet.gui.Gui;
import cfss.org.xmlstructure.XMLImage;
import cfss.org.xmlstructure.XMLPoint;
import cfss.org.LocalMouseMonitor.*;
import cfss.org.flags.Debug;
import cfss.org.modules.ScrollPanePlayer;

/**
 * Standalone object for displaying an image, featuring zoom functionality and
 * on-click linking to a URL or slide ID.<p>
 *
 * The given image is handled according to the parameters passed to the player
 * in the form of an {@link XMLImage XMLImage} data structure, dictating
 * file path, position, width, height and on-click functionality.<p>
 *
 * Positioning and sizing on the slide area is performed relative to an
 * imaginary 800x600 grid, so that proportionality remains constant for varying
 * slide sizes. Values specified in the parameters are referenced to this grid<p>
 *
 * The image is painted onto its own panel of matching size, so that its
 * painting is independent from other slide elements. The panel is invisible,
 * so that any transparency within the supplied image is correctly displayed.<p>
 *
 * Inactive, URL linking and slide jumping on-click functionality is specified
 * within the XML in the same fashion as other elements. An extra option of -2
 * is used to enable zoom functionality<p>
 *
 * Images with zooming ability are by default overlaid with small arrows in the
 * corners of the image, to indicate that zooming is available. This can however
 * be disabled using {@link #setShowArrows(Boolean) setShowArrows}. When a
 * zoomable image is clicked on, the slide area is darkened and the image
 * displayed at the maximum possible size without exceeding the slide boundaries
 * or excessively distorting the image. The user may click on the darkened slide
 * area or on an exit button to revert the image to its original state on slide<p>
 *
 * The image is not displayed until the external invocation of the
 * {@link #display() display} method, and may be removed again by the use of
 * {@link #unDisplay() unDisplay}. A {@link #resize(double) resize} function is
 * provided so that the image can be rescaled and repositioned to remain in the
 * same relative position and proportions when the slide size is altered.<p>
 *
 * An image may also be placed within a {@link ScrollPanePlayer ScrollPanePlayer}
 * if it is initialised by its
 * {@link #ImagePlayer(XMLImage, ScrollPanePlayer) alternative constructor}<br>
 * In this mode of operation, on-click functionality is currently disabled due
 * to the requirement for independent mouse handling which would be imposed and
 * is not considered necessary.
 *
 * @author Michael Angus
 *
 */
@SuppressWarnings("serial")
public class ImagePlayer extends Entity
{
	private static boolean TEST = true;

	//Panel on which the image is mounted
	private JPanel imagePanel;

	//Flags used throughout the code; do not modify here
	private Boolean showArrows = false;
	private Boolean showImage = false;
	private Boolean isZoomed = false;
	private Boolean zoomEnabled = false;
	private Boolean inScrollPane = false;

	//Image properties, original image file and image currently to be displayed
	private XMLImage imageParameters;
	private Image mainImage;

	//Information about the host environment; slide or scrollpane panel
	private JLayeredPane slidePanel;
	private JPanel scrollPanel;
	private Dimension hostSize;

	//Object for reading loading the images
	private MediaTracker imageTracker;

	//Properties of the unzoomed on-slide image
	private Point imagePosition;
	private Dimension imageSize;

	//Properties of the zoomed on-slide image
	private Point zoomedPosition;
	private Dimension zoomedSize;

	//Thickness of the border around the image in zoom mode. Set as desired.
	private static int borderThickness = 10;

	//Zoom indicator arrows - path, internal storage and positioning
	//These are only loaded once and used by all instances of the ImagePlayer,
	//only their positioning changes
	private static String arrowFolderPath = "Icons/Arrows";
	private static Vector <Image> zoomArrows;
	private Vector <Point> arrowPositions;

	//Mouse monitor for on-click, zooming and un-zooming functionality
	LocalMouseMonitor imageLMM;
	private ScrollPanePlayer scrollPanePlayer = null;

	/**
	 * Main constructor.
	 * The transparent image mounting panel is created, the image file prepared
	 * for loading and the appropriate on-click option set. The ImagePlayer is
	 * added to the slide area and positioned/scaled appropriately according to
	 * its XML parameters and the slide dimensions.<br>
	 * If on-click functionality is required, a
	 * {@link LocalMouseMonitor LocalMouseMonitor} is set up for this purpose.
	 *
	 * @param thisImage the {@link XMLImage XMLImage} data structure containing
	 * 	the parameters for the image.
	 */
	public ImagePlayer(XMLImage thisImage)
	{
		//Set z-order (layer of slide at which the image will be placed)
		setZOrder(thisImage.getZOrder());

		//Instantiate the ImagePlayer JPanel with null layout, transparent
		imagePanel = new ImagePanel();
		imagePanel.setLayout(null);
		imagePanel.setOpaque(false);

		//Set reference to XML image properties and initialise the main image
		//and 'imageTracker'
		setImageParameters(thisImage);

		//-2 indicates a zoomable image, hence enable zoom
		if(-2 == imageParameters.getOnClick())
		{
			setZoomEnabled(true);
			setShowArrows(true);
		}

		//Add image panel to the slide panel at specified z-order position
		slidePanel = Gui.getSlidePanel();

		int zPosition = getZOrder();
		slidePanel.add(imagePanel, new Integer(zPosition));

		//Set the image position, size and boundaries by scaling the values
		//specified in the XML. This will also make the ImagePlayer panel match
		//this position and size, and create the scaled version 'displayedImage'
		//of the original image using these values
		adaptToHost();

		//Debug message verifies the correct sizing of the image panel
		if(Debug.imagePlayer) System.out.println(
				"Dimensions of ImagePlayer are: " + imagePanel.getSize().width +
					" by " + imagePanel.getSize().height);
	}

	/**
	 * Alternative constructor for use of ImagePlayer within a
	 * {@link ScrollPanePlayer ScrollPanePlayer}.
	 *
	 * This is mostly identical to the
	 * {@link #ImagePlayer(XMLImage) standard constructor} except that the
	 * image panel is positioned within the scrollpane and derives its
	 * proportional positioning and size from the scrollpane inner panel.<br>
	 * Also, on-click functionality is not implemented for images in scrollpanes
	 * yet due to the requirement for independent mouse handling which would be
	 * imposed.
	 *
	 * @param thisImage
	 * @param aScrollPanePlayer
	 */
	public ImagePlayer(XMLImage thisImage, ScrollPanePlayer aScrollPanePlayer)
	{
		//Instantiate the ImagePlayer JPanel with null layout, transparent
		imagePanel = new ImagePanel();
		imagePanel.setLayout(null);
		imagePanel.setOpaque(false);

		//Works with a Scroll Pane
		scrollPanePlayer = aScrollPanePlayer;

		//Define image parameters from the data in the XMLImage
		setImageParameters(thisImage);

		//Add image panel to the scrollPanel and update flag to indicate this
		scrollPanePlayer.insert(imagePanel);
		inScrollPane = true;

		//Initialise reference to the panel for size information
		scrollPanel = (JPanel)scrollPanePlayer.getPanel();

		//Set the image position, size and boundaries by scaling the values
		//specified in the XML. This will also make the ImagePlayer panel match
		//this position and size.
		adaptToHost();

		//Debug message verifies the correct sizing of the image panel
		if(Debug.imagePlayer) System.out.println(
				"Dimensions of ImagePlayer are: " + imagePanel.getSize().width +
					" by " + imagePanel.getSize().height);
	}

	/**
	 * Display method, invoked to make the image appear on screen
	 */
	public void display()
	{
		if(Debug.imagePlayer) System.out.println("Imageplayer display called");

		//Check if the host size has changed since instantiation/hiding
		Boolean hostChanged = false;

		if(inScrollPane)
			hostChanged = (hostSize.width != scrollPanel.getWidth() ||
							hostSize.height != scrollPanel.getHeight());
		else
			hostChanged = (hostSize.width != slidePanel.getWidth() ||
					hostSize.height != slidePanel.getHeight());

		//Re-adapt to host dimensions if they have changed
		//If entering zoom mode, adaptation has already been performed
		if(hostChanged && (false == isZoomed))
			adaptToHost();

		//Set it to active
		//This flag is checked by SlideRenderer in resizeAll - not resized if
		//inactive
		setIsActive(true);

		//Enable image displaying and make visible
		setShowImage(true);
		imagePanel.setVisible(true);

		//TODO: remove this test code
		if(-2 == imageParameters.getOnClick())
			System.out.println("Stopped at zoomable image");

		//Enable mouse click detection if XML 'onclick' value specifies it
		//Values -2, 0 and >0 all indicate functionality, hence create listener
		//for all values other than -1
		if(imageParameters.getOnClick() != -1)
			updateMouseDetection(true,false);
	}

	/**
	 * Method to hide the image again once visible, for internal use
	 */
	private void unDisplayInternal()
	{
		if(Debug.imagePlayer) System.out.println("Imageplayer unDisplayInternal called");

		//Remove the mouse monitor if one exists
		updateMouseDetection(false,false);

		//Disable painting and remove panel from screen
		setShowImage(false);
		imagePanel.setVisible(false);
	}

	/**
	 * Method to hide the image again once visible, for use by SlideRenderer.
	 * This calls the internal hiding method and flushes the main image to free
	 * memory.
	 */
	public void unDisplay()
	{
		if(Debug.imagePlayer) System.out.println("Imageplayer unDisplay called");


		//Marked as no longer active for sliderenderer's reference
		setIsActive(false);

		//Call internal method
		unDisplayInternal();

		//Release resources used by main image if required
		flushImage();
	}

	/**
	 * Resize image to maintain proportionality with host.
	 *
	 * Since ImagePlayer derives its position and size from the current size of
	 * the host, there is no requirement for a scale factor. The host must be
	 * at its new size before this method is called for the resizing to function
	 * correctly. This requirement is expected to be fulfilled since the given
	 * scale factor is derived from the change in size of the host, and hence is
	 * created after the host is resized.
	 *
	 * @param scaleFactor factor by which to scale the image, not used here
	 */
	public void resize(double scaleFactor)
	{
		if(isZoomed)
		{
			//Get rid of incorrectly sized elements on-screen
			exitZoom();

			//Rescale the reference host size to new size of host
			if(inScrollPane)
				hostSize = scrollPanel.getSize();
			else
				hostSize = slidePanel.getSize();

			//Zoom in again. This handles correct sizing and zone definition on
			//the basis of the slide panel size
			zoomImage();
		}

		else
		{
			//Hide the current image (already-painted version cannot be resized)
			unDisplayInternal();

			//Adapt to the new dimensions of the slide
			adaptToHost();

			//Reposition the mouse detection zone to match the image panel
			if (imageLMM != null)
			{
			imageLMM.reposition(imagePanel.getX(), imagePanel.getY());
			imageLMM.rescale(imageSize.width, imageSize.height);
			}

			//Display the scaled image
			display();
		}
	}

	/**
	 * Obtains the current size of the host and recalculates image size and
	 * position to maintain required proportionality.
	 *
	 * The 800x600 grid for the current host size is derived and used to
	 * scale the position and size of the image and its panel. If necessary,
	 * the zoom arrow icons are also initialised here.
	 */
	public void adaptToHost()
	{
		//Update host size to current actual value
		if(inScrollPane)
			hostSize = scrollPanel.getSize();
		else
			hostSize = slidePanel.getSize();

		//Derive the number of onscreen pixels for one increment of the 800x600
		//grid used for specifying size and positioning in the XML
		double gridX = (double)(hostSize.width)/800;
		double gridY = (double)(hostSize.height)/600;

		//Hence scale image coordinates and place relative to slide origin
		//to achieve the slide positioning defined in the XML
		int scaledXPos = (int)(gridX*imageParameters.getStartPoint().getX());
		int scaledYPos = (int)(gridY*imageParameters.getStartPoint().getY());
		setImagePosition(new Point(scaledXPos,scaledYPos));

		//Also scale the dimension values so that the onscreen size matches the
		//grid-relative size defined in the XML
		int scaledWidth = (int)(gridX*imageParameters.getWidth());
		int scaledHeight = (int)(gridY*imageParameters.getHeight());
		setImageSize(new Dimension(scaledWidth,scaledHeight));

		// Set ImagePlayer panel to match the image size and position
		imagePanel.setSize(imageSize);
		imagePanel.setLocation(imagePosition);

		//These debug messages are to verify correct slide adaptation
		if(Debug.imagePlayer) System.out.println("Slide: width:"+hostSize.width+
				" height:"+hostSize.height);
		if(Debug.imagePlayer) System.out.println(
				"Image: x:"+imagePosition.x+" y:"+imagePosition.y);
		if(Debug.imagePlayer) System.out.println(
				"Image: width:"+imageSize.width+" height:"+imageSize.height);
		if(Debug.imagePlayer) System.out.println("ImagePlayer: x:"+imagePanel.getX()+" y:"+
										imagePanel.getY());
		if(Debug.imagePlayer) System.out.println(
				"ImagePlayer: width:"+imagePanel.getWidth()+" height:"+
					imagePanel.getHeight());

		//If zoom arrows are to be displayed, initialise them
		//at correct positions
		if(showArrows)
		{
			try {
				initArrows(arrowFolderPath);
			} catch (InterruptedException e) {
				System.out.println("ImagePlayer: Arrow initialisation interrupted");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Maximises the image within current boundaries.
	 *
	 * The enlarged image is placed centrally on the slide, with an exit button
	 * icon placed in its top-right corner, and is surrounded by a
	 * border of matching colour to the slide background. The slide area is
	 * darkened and the LocalMouseMonitor updated to allow clicking on the
	 * darkened region or the exit button to exit zoom mode.<p>
	 *
	 * The z-order positioning of the image is promoted to the topmost available
	 * position to avoid any other slide elements obscuring the image. <p>
	 *
	 * The constraints for this process are the screen width, screen height and
	 * maximum scale factor 2 (this maximum exists to avoid excessive pixelation
	 * of the image). The method determines the maximum possible increase within
	 * these constraints and applies the resulting enlargement.<br>
	 * Aspect ratio of the original image (not necessarily on-slide version) is
	 * maintained.
	 */

	public void zoomImage()
	{
		//For clarity of code, create some variables to hold slide dimensions
		//and original image dimensions
		int slideWidth = hostSize.width;
		int slideHeight = hostSize.height;
		int originalWidth = getMainImage().getWidth(null);
		int originalHeight = getMainImage().getHeight(null);

		//Determine maximum zoomed size on the basis of a maximum zoom factor
		//of 2(to avoid excessive pixelation)
		int maxWidth = 2*originalWidth;
		int maxHeight = 2*originalHeight;

		//Determine the overlap with the slide edges
		//Positive value = magnitude of overlap
		//Negative value (no overlap) = distance to slide edge from image edge
		int xOverlap = maxWidth - slideWidth;
		int yOverlap = maxHeight - slideHeight;

		//The overlap with the greatest positive value defines the maximum scale
		//factor since the image must not clip the slide edges.
		double scaleFactor;

		if(xOverlap > yOverlap)
			scaleFactor = (double)slideWidth/originalWidth;
		else
			scaleFactor = (double)slideHeight/originalHeight;

		//If neither are positive then resulting scale factor will be greater
		//than 2. If so, set it to 2 (maximum)
		if(scaleFactor > 2)
			scaleFactor = 2;

		//Instantiate variables zoomedSize and zoomedPosition if required
		if(null == zoomedSize)
			zoomedSize = new Dimension(0,0);
		if(null == zoomedPosition)
			zoomedPosition = new Point(0,0);

		//Determine new width and height
		zoomedSize.width = (int)(scaleFactor*originalWidth);
		zoomedSize.height = (int)(scaleFactor*originalHeight);

		//Determine new position
		zoomedPosition.x = (int)(0.5*slideWidth - 0.5*zoomedSize.width);
		zoomedPosition.y = (int)(0.5*slideHeight - 0.5*zoomedSize.height);

		//Debug message to inform of new size and position of image
		if(Debug.imagePlayer) System.out.println("Zoomed image will be drawn at ("+
				zoomedPosition.x+","+zoomedPosition.y+") with dimensions "+
				zoomedSize.width+" x "+zoomedSize.height);

		//Hide the image panel whilst it is reconfigured for zoom mode
		unDisplayInternal();

		//Determine highest unoccupied layer of the slide panel
		int topLayer = slidePanel.highestLayer() + 1;

		//Promote the image panel to the top layer, so that no other object will
		//obscure the image by displaying on top of it
		slidePanel.remove(imagePanel);
		slidePanel.add(imagePanel,new Integer(topLayer));

		//Enlarge the image panel to completely fill the slide area
		imagePanel.setSize(hostSize);
		imagePanel.setLocation(0,0);

		//Enable drawing to screen of the image panel, and make it black and
		//semitransparent to provide slide darkening effect
		imagePanel.setOpaque(true);
		imagePanel.setBackground(new Color(0,0,0,127));

		//Set flag to indicate that zoomed mode is now activated, and display
		//the reconfigured image
		isZoomed = true;
		display();

		//Update the mouse detection zone to match the new size of image panel
		//updateMouseDetection(true,true);
	}

	/**
	 * Exits zoom mode and reverts the image state to its original on-slide
	 * condition, updating the LocalMouseMonitor accordingly.
	 * The image is removed from its elevated layer and placed at its original
	 * z-order position
	 */
	public void exitZoom()
	{
		//Debug message indicating zoom mode exit
		if(Debug.imagePlayer)
			System.out.println("Zoom mode exited");

		//Reset zoom flag and remove zoomed image from screen
		isZoomed = false;
		unDisplayInternal();

		//Remove from elevated layer and return to original z-position
		slidePanel.remove(imagePanel);
		int originalLocation = getZOrder();
		slidePanel.add(imagePanel,new Integer(originalLocation));

		//Revert panel to transparent state and re-display on the slide
		//at its original dimensions
		imagePanel.setOpaque(false);
		adaptToHost();
		display();
	}

	/**
	 * Updatesthe mouse detection.
	 *
	 * @param isEnabled is the boolean to set if enabled.
	 * @param toZoomMode is the boolean which is true if zoomed.
	 */
	public void updateMouseDetection(Boolean isEnabled, Boolean toZoomMode)
	{
		//If detection is not to be enabled, destroy the mouse monitor if exists
		//Executed when the image is removed from the slide
		if(false == isEnabled)
		{
			if(imageLMM != null)
				imageLMM.destroy();
			return;
		}
		//Executed when image is displayed on the slide
		//Matches image panel size in both zoom mode and on-slide
		else
		{
			//Create the mouse monitor with parent area matching the size
			//and position of the image panel. Events handled by inner class
			//MouseMonitor
			imageLMM = new LocalMouseMonitor(new MouseMonitor(),imagePanel);

			//Register interest in click events for on-click linking and zoom
			//mode enter/exit.
			//Register interest in cursor entry into parent area for cursor
			//switching to hand icon when over clickable image
			imageLMM.setNotifications(true, false, false, true, false, false);

			//Create the detection zone for the mouse monitor
			imageLMM.addZone("ImageArea", new Point(0,0), imagePanel.getSize());
		}
	}

	/**
	 * Sets all parameters of the image using an {@link XMLImage XMLImage} data
	 * structure.
	 *
	 * @param imageParameters the data structure containing the parameters
	 */
	public void setImageParameters(XMLImage imageParameters) {
		this.imageParameters = imageParameters;
	}

	/**
	 * Set the dimensions which specify host size for ImagePlayer
	 *
	 * @param hostSize the dimensions to be used
	 */
	public void setHostSize(Dimension hostSize) {
		this.hostSize = hostSize;
	}

	/**
	 * Set the position of the image relative to the origin of the host
	 *
	 * This is specified in real pixels, not in terms of the 800x600 grid
	 *
	 * @param imagePosition the x,y position of the image
	 */
	public void setImagePosition(Point imagePosition) {
		this.imagePosition = imagePosition;
	}

	/**
	 * Set the width and height of the image and its panel
	 *
	 * This is in real pixels, not in terms of 800x600 grid
	 *
	 * @param imageSize the size the image is to be
	 */
	public void setImageSize(Dimension imageSize) {
		this.imageSize = imageSize;
	}

	/**
	 * Sets whether or not arrows are to be shown to indicate zoom functionality
	 *
	 * By default the arrows are shown for zoomable images
	 *
	 * @param showArrows flag to specify arrow displaying
	 */
	public void setShowArrows(Boolean showArrows) {
		this.showArrows = showArrows;
	}

	/**
	 * Flag which controls whether or not the image is displayed.
	 *
	 * The image is never displayed unless this flag is <code>true</code>
	 *
	 * @param showImage flag to allow image to be displayed
	 */
	public void setShowImage(Boolean showImage) {
		this.showImage = showImage;
	}

	/**
	 * Flag to set whether or not zoom functionality is enabled.
	 *
	 * This is <code>false</code> by default, and is set to <code>true</code> at
	 *  instantiation if zoom functionality is specified in the XML parameters.
	 *
	 * @param zoomEnabled flag to specify enabling of zoom
	 */
	public void setZoomEnabled(Boolean zoomEnabled) {
		this.zoomEnabled = zoomEnabled;
	}

	/*
	 * ImagePanel class, a JPanel with an overridden <code>paint</code> method.
	 *
	 * Although defined as a distinct class for customised painting, only the
	 * <code>paint</code> method differs from JPanel. Instances of this class
	 * are therefore JPanels and may be handled and referenced as such.<br>
	 * i.e. the type is unchanged; these objects may be assigned to variables
	 * of type JPanel without type casting.
	 *
	 * @author Michael Angus
	 *
	 */
	private class ImagePanel extends JPanel
	{
		/*
		 * Constructor, simply uses superclass constructor to create JPanel.
		 * Because only super() is called, this object is a JPanel and may be
		 * handled and referenced as if it were this type.
		 */
		public ImagePanel()
		{
			super();
		}

		/*
		 * Paint method override to allow direct control of the painting of the
		 * image panel.
		 * This allows the image to be painted directly onto the panel, and
		 * allows this behaviour to be conditional according to current state
		 * and instance-specific requirements<p>
		 * Painting is enabled by the <code>showImage</code> flag in
		 * {@link ImagePlayer ImagePlayer}, which is set within ImagePlayer by
		 * its {@link ImagePlayer#display() display} and
		 * {@link ImagePlayer#unDisplay() unDisplay} methods, and may also be
		 * accessed externally via
		 * {@link ImagePlayer#setShowImage(Boolean) setShowImage}<p>
		 * If this flag is <code>false</code> the panel's visibility is disabled
		 * to better ensure that the image displaying follows the state of this
		 * flag. The method then terminates immediately.
		 */
		public void paint(Graphics g)
		{
			if(Debug.imagePlayer) System.out.println("paintComponent called from: "
											+ super.toString());
			//Only if 'showImage' is true is any painting performed. If false
			//the method exits immediately, after ensuring that the panel is hidden
			if(!showImage){

				//TODO: test this code when images are available again
				if(TEST)flushImage();

				if(isVisible())
					setVisible(false);

				if(Debug.imagePlayer) System.out.println(
						"Exited without painting");
				return;
			}

			//Ensure the panel itself is drawn first
			super.paint(g);

			//Use the given Graphics object to create a Graphics2D object for
			//image rendering, and set its interpolation method to improve
			//rendering quality of scaled images
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	                   RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			//Zoomed version drawn at zoomedPosition, otherwise at (0,0)
			//(relative to imagePanel)
			if(isZoomed)
			{
				//Draw the border for the zoomed image
				drawBorder(g);

				//Draw the image with appropriate position and size for zoomed
				//mode as derived by zoomImage()
				g2.drawImage(getMainImage(),zoomedPosition.x,
						zoomedPosition.y,zoomedSize.width,
						zoomedSize.height, null);

				//Debug message for verifying correct painting
				if(Debug.imagePlayer) System.out.println(
				"Painted zoomed image at ("+zoomedPosition.x+","+
					zoomedPosition.y+") with dimensions "+
						zoomedSize.width+" by "+zoomedSize.height);
			}
			//If not zoomed, image displayed at normal size on the slide
			else
			{
				//Draw the image with required on-slide dimensions
				//onto the image panel
				g2.drawImage(getMainImage(),0,0,imageSize.width,
								imageSize.height, null);

				//Debug message for verifying correct painting
				if(Debug.imagePlayer) System.out.println(
				"Painted on-slide image at ("+imagePosition.x+","+
					imagePosition.y+") with dimensions "+
						imageSize.width+" by "+imageSize.height);

				//Draw arrows if they are to be displayed
				if(showArrows)
				{
					int x=0;
					int y=0;

					//Iterate through vectors containing arrows and corresponding
					//positions, drawing each one in turn
					for(int i=0; i<=3; i++)
					{
						x = arrowPositions.get(i).x;
						y = arrowPositions.get(i).y;

						if(Debug.imagePlayer) System.out.println("About to draw arrow "+i+
								" at coordinates ("+x+","+y+")");

						g.drawImage(zoomArrows.get(i),x,y,null);
					}
				}
			}
			//Debug check for heap over-allocation
			if(Debug.imagePlayer)
			{
			long heapSize = Runtime.getRuntime().totalMemory();
		    System.out.println("Heap Size= "+heapSize);
			}
		}
	}

	private class MouseMonitor implements MouseMonitorListener
	{
		//Blank implementation of unneeded drag monitoring method
		public void dragEventOccurred(String s,int x,int y,Boolean complete){}

		//Mouse handling for on-click functionality and cursor icon switching
		public void zoneEventOccurred(String eventZone, int eventType)
		{
			//TODO: remove test code
			if(imageParameters.getPath().equalsIgnoreCase("images/01 of 10.jpg"))
				System.out.println("shaft");

			//If mouse moves over an image on slide with mouse detection, cursor
			//icon becomes a hand to indicate on-click availability
			if(eventType==ZoneEvents.ENTER)
				Gui.changeCursor(Cursor.HAND_CURSOR);

			//Trigger appropriate action when on-slide image is clicked.
			if(eventType==ZoneEvents.CLICK && false == isZoomed)
			{
				//Debug message to indicate a click event and its position
				if(Debug.imagePlayer) System.out.println(
						"Mouse clicked on slide image: "+imageParameters.getPath());

				//Zoom image if zooming is enabled.
				//Terminate method afterwards to avoid exitZoom being
				//invoked immediately after zoomImage sets the isZoomed flag
				if(zoomEnabled)
				{
					zoomImage();
					return;
				}

				//Go to URL if specified in XML
				else if(0 == imageParameters.getOnClick())
					goToUrl(imageParameters.getOnClickUrl());

				//Jump to slide ID if specified in XML
				else if(imageParameters.getOnClick() >0)
					jumpToSlide(imageParameters.getOnClick());
			}

			//If the image is in zoomed mode, exit this mode
			else if(eventType==ZoneEvents.CLICK && isZoomed)
			{
				Gui.changeCursor(Cursor.DEFAULT_CURSOR);
				exitZoom();
			}
		}
	}

	private void drawBorder(Graphics g)
	{
		//First initialise colour
		Color borderColour = slidePanel.getBackground();

		//Set flags (for use in creating the border) for whether the zoomed
		//image takes the full height and/or width of the slide area
		Boolean isFullWidth = false;
		Boolean isFullHeight = false;

		if(hostSize.getWidth() == zoomedSize.width)
			isFullWidth = true;
		if(hostSize.getHeight() == zoomedSize.height)
			isFullHeight = true;

		//Check flags isFullWidth and isFullHeight to ensure border does not
		//go outside slide area. If one of the flags is true, its corresponding
		//size and position variables will omit the border thickness from
		//their calculation (so the border will end at any image edge
		//that contacts the slide edge
		int borderWidth;
		int borderHeight;
		int x;
		int y = zoomedPosition.y - borderThickness;

		//Check horizontal
		if(isFullWidth)
		{
			borderWidth = zoomedSize.width;
			x = zoomedPosition.x;
		}
		else
		{
			borderWidth = zoomedSize.width + 2*borderThickness;
			x = zoomedPosition.x - borderThickness;
		}

		//Check vertical
		if(isFullHeight)
		{
			borderHeight = zoomedSize.height;
			y = zoomedPosition.y;
		}
		else
		{
			borderHeight = zoomedSize.height + 2*borderThickness;
			y = zoomedPosition.y - borderThickness;
		}

		//Debug messages to verify correct sizing and positioning of border
		if(Debug.imagePlayer) System.out.println("Border panel will be drawn at ("+
				x+","+y+") with dimensions "+
				borderWidth+" x "+borderHeight);

		//Draw the first rectangle which will define the border
		g.setColor(borderColour);
		g.fillRect(x, y, borderWidth, borderHeight);

		//Draw the second rectangle which will give a white background to any
		//image with transparency
		g.setColor(Color.WHITE);
		g.fillRect(zoomedPosition.x, zoomedPosition.y,
					zoomedSize.width, zoomedSize.height);
	}

	/*
	 * Prepares the data for the main image so that it may be drawn to
	 * screen and its properties may be read.<br>
	 * The image is returned immediately if already prepared. If initialised
	 * but not prepared, it is loaded into memory by imageTracker and returned<br>
	 * If not yet initialised, the Image object for mainImage is loaded using
	 * {@link #loadImage(String) loadImage} and/or imageTracker is created and
	 * mainImage added to it.
	 *
	 * @return mainImage the main image to be displayed on the image panel,
	 * fully prepared to be painted on the panel and/or have its properties
	 * read.
	 */
	private Image getMainImage()
	{
		//First check if image is already loaded, load it if it is not, then
		//return the fully prepared image. If tbe image and/or imageTracker
		//have not been initialised, a NullPointerException is thrown
		try
		{
			if(true == imageTracker.checkID(0))
				return mainImage;
			else
			{
				imageTracker.waitForID(0);
				return mainImage;
			}
		}
		//If mainImage and/or imageTracker are not initialised, this block will
		//be executed
		catch(NullPointerException e)
		{
			//Initialise mainImage object from file if required
			if(null == mainImage)
				mainImage = (loadImage(imageParameters.getPath()));

			//Create imageTracker to track images for imagePanel and add the
			//main image to it, if not already done
			if(null == imageTracker)
			{
				imageTracker = new MediaTracker(imagePanel);
				imageTracker.addImage(mainImage,0);
			}

			//Re-invoke this method to return fully prepared image
			return getMainImage();
		}
		//If imageTracker is interrupted whilst loading the image, no image can
		//be returned.
		catch (InterruptedException e)
		{
			//Restore interrupted status
			Thread.currentThread().interrupt();
			return null;
		}
	}

	private Image loadImage(String filePath)
	{
		//Prepare a toolkit for loading the image
		Toolkit tool = Toolkit.getDefaultToolkit();

		//Load image from filepath and return the resultant Image object
		Image loadedImage = tool.getImage(filePath);

		if(Debug.imagePlayer) System.out.println("Loading image from: "+
				(new File(filePath)).getAbsolutePath());

		return loadedImage;
	}

	//Method called for freeing heap memory occupied by image
	private void flushImage()
	{
		//Release resources used by the main image for the scaling and painting
		//process, since the image is now displayed.
		try
		{
			mainImage.flush();
			if(Debug.imagePlayer)
				System.out.println("Image from "+imageParameters.getPath()+
						" has been flushed");
			long heapSize = Runtime.getRuntime().totalMemory();
		    System.out.println("Heap Size= "+heapSize);
		}
		//If there is nothing to be flushed, this exception is thrown. Nothing
		//need be performed if so, since attempted operation not needed
		catch(NullPointerException e){}
	}

	private void initArrows(String arrowFolderPath) throws InterruptedException
	{
		//If the arrow images have not been loaded, load them from source files
		//contained in folder specified by arrowFolderPath
		if(null == zoomArrows)
		{
			//Create temporary variables for iteration and initialise vector
			//that is to contain the arrow images
			String filePath;
			Image currentImage;
			zoomArrows = new Vector<Image>(4);

			//Load arrows into the vector in this order:
			//Top-left, top-right, bottom-left, bottom-right
			for(int i=0; i<=3; i++)
			{
				filePath = (arrowFolderPath + "/" + i + ".png");
				currentImage = loadImage(filePath);
				zoomArrows.add(i,currentImage);
			}
		}

		//Setup media tracker and load arrow image data ready for painting.
		//This also allows the image dimensions to be read (for positioning)
		MediaTracker arrows = new MediaTracker(imagePanel);

		for(int i=0; i<=3; i++)
		{
			arrows.addImage(zoomArrows.get(i), i);
		}
		arrows.waitForAll();

		//Initialise arrowPositions vector to 4x(0,0), create intermediate
		//variables for holding calculated coordinates and arrow dimensions
		arrowPositions = new Vector <Point>(4);
		int x;
		int y;
		int arrowWidth = zoomArrows.get(0).getWidth(null);
		int arrowHeight = zoomArrows.get(0).getHeight(null);

		//Set the positioning of the arrows based on image edges and
		//origin, which is (0,0) since drawing coordinates based on panel origin
		x = 0;
		y = 0;
		arrowPositions.add(new Point(x,y));	//Top left

		x = imageSize.width - arrowWidth;
		y = 0;
		arrowPositions.add(new Point(x,y));	//Top right

		x = 0;
		y = imageSize.height - arrowHeight;
		arrowPositions.add(new Point(x,y));	//Bottom left

		x = imageSize.width - arrowWidth;
		y = imageSize.height - arrowHeight;
		arrowPositions.add(new Point(x,y));	//Bottom right
	}

	private void goToUrl(String url)
	{
		if(Debug.imagePlayer) System.out.println("Clicked to go to URL: "+url);
		BrowserLoader.openURL(url);
	}

	private void jumpToSlide(int slideId)
	{
		if(Debug.imagePlayer) System.out.println("Clicked to jump to slide: "+slideId);
		Engine.gotoSlide(slideId);
	}

//TODO: REMOVE TEST CODE=======================================================
	public static void main(String[] args)
	{

		new SafetyNet();
		if(TEST){
			String path1 = "images/flushtest.png";
			int x1 = 0;
			int y1 = 0;
			int width1 = 250;
			int height1 = 150;
			int starttime1 = 0;
			int endtime1 = 100000000;
			int onclick1 = -2;
			String onclickurl1 = "URL 1";

			//Create any XML images here
			XMLImage image1 = new XMLImage();
			image1.setPath(path1);
			XMLPoint tempPoint = new XMLPoint();
			tempPoint.setX(x1);
			tempPoint.setY(y1);
			image1.setStartPoint(tempPoint);
			image1.setWidth(width1);
			image1.setHeight(height1);
			image1.setStartTime(starttime1);
			image1.setEndTime(endtime1);
			image1.setOnClick(onclick1);
			image1.setOnClickUrl(onclickurl1);
			image1.setZOrder(100);

			//Create image player
			ImagePlayer player1 = new ImagePlayer(image1);
			player1.display();
		}
	}
//=============================================================================
}
