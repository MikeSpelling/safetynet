/************************************************
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		VideoPlayer
 *
 *  Author: 		Harry Taylor
 *
 *  Contributers:	.
 *  Testers:		.
 *
 *  Description:	This class creates a video player for a file path passed in.
 *
 *  Instantiate with:
 *	VideoPlayer(String path, int x-coordinate, int y-coordinate,
 *	int width, int height, boolean loop, boolean control, boolean autoplay)
 *
 *	Known bugs:
 *	BE CAREFUL that FOBS4JMF has been set up properly!
 *	IF THE JAVA 2D RENDERER IS USED, IT WON'T WORK
 *	lightweight renderer must be used.
 *	This can be checked in: Media Properties, Plug-in Settings, PlugIn viewer
 *
 ***********************************************/



package cfss.org.modules;

import java.awt.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.media.*;
import cfss.org.LocalMouseMonitor.*;
import cfss.org.safetynet.Entity;
import cfss.org.safetynet.SafetyNet;
import cfss.org.safetynet.gui.Gui;
import cfss.org.xmlstructure.Video;
import cfss.org.flags.*;
/**
 *  *	This class creates a video player for a file path passed in.
 *
 *  Instantiate with:
 *	VideoPlayer(String path, int x-coordinate, int y-coordinate,
 *	int width, int height, boolean loop, boolean control, boolean autoplay)
 *
 *	Known bugs:
 *	BE CAREFUL that FOBS4JMF has been set up properly!
 *	IF THE JAVA 2D RENDERER IS USED, IT WON'T WORK
 *	lightweight renderer must be used.
 *	This can be checked in: Media Properties, Plug-in Settings, PlugIn viewer
 *
 *	@author Harry Taylor
 */
public class VideoPlayer extends Entity implements ControllerListener, MouseMonitorListener{

	private Component controlComponent, videoComponent;  // These are used to position and resize the video picture and controls.
	private JPanel mediaPanel;  // This is what the video is added to.
	private JPanel outerPanel;  // mediaPanel is contained within this.  Sets a back border in video fullscreen mode.
	private GainControl gainControl = null;  // This is used to control volume
	private Player player=null;  // This is the actual object that plays back the media
	private boolean controls=true;  // Should a control panel be drawn?
	private boolean loop=false;  // Should the media start again after it reaches the end?
	private boolean autoplay = false;  // Should the media being playback immediately after being displayed?
	private boolean realizeComplete = false;  // A flag to indicate whether the player has been realized.
	LocalMouseMonitor MouseMonitor=null;  // Used to maximise the video on click.
	// Original coordinates and dimensions.  Only updated in constructor.
	private int xCoord;
	private int yCoord;
	private int xDim;
	private int yDim;
	// Scaled coordinates and dimensions.  Updated by any change in window size.
	private int scaledXCoord;
	private int scaledYCoord;
	private int scaledXDim;
	private int scaledYDim;
	// All sizes will be relative to this 800x600 dimension
	private final Dimension slideDimension = new Dimension(800, 600);



	/**
	 * The constructor method.  Stores information in the datastructure as fields,
	 * creates a JPanel for the media, then loads the media file ready to display.
	 */
	public VideoPlayer(Video aVideo)
	{
		// sets z order
		super.setZOrder(aVideo.getZOrder());

		// Copy the data structure information to class-level variables as required.
		this.controls = aVideo.isControls();
		this.loop = aVideo.isLoop();
		this.autoplay= aVideo.isAutoplay();
		this.xCoord = aVideo.getStartPoint().getX();
		this.yCoord = aVideo.getStartPoint().getY();
		this.xDim = aVideo.getWidth();
		this.yDim = aVideo.getHeight();

		// Create a JPanel to contain the control panel and video content.
		mediaPanel = new JPanel();
		mediaPanel.setLayout(null);
		outerPanel = new JPanel();
		outerPanel.setLayout(null);
		outerPanel.add(mediaPanel);
		outerPanel.setBackground(Color.BLACK);

		// Load in the video file and create the player, ready to begin playback.
		processMedia(aVideo.getPath());
	}




	/*///////////////////////////////
	 *
	 *     PUBLIC METHODS
	 *
	 *//////////////////////////////





	/**
	 * Displays the media in the main program slidePanel.
	 * Scales the desired dimensions and coordinates to fit the window size.
	 * Starts the Player if the Autoplay flag is set in the data structure,
	 * and declares the LocalMouseMonitor to look for clicks on the video panel.
	 */
	public void display(){

		Dimension currentPanelSize;  // Current size of the presentation panel.
		double sizeFactor;  // Factor between 800x600 grid and current panel size.

		if (null != player){
			currentPanelSize = Gui.getSlidePanel().getSize();
			// Must typecast here, otherwise integer maths is performed.
			sizeFactor = ((double)currentPanelSize.width / (double)800);
			if (Debug.video) System.out.println("scale factor : " + sizeFactor);

			// Now apply the scale to the desired coordinates and dimensions.
			// These scaled results will be used in this class from now on.
			scaledXCoord = (int) (xCoord * sizeFactor);
			if (Debug.video) System.out.println("VideoPlayer -> display -> scaled xCoord : " + scaledXCoord);
			scaledYCoord = (int) (yCoord * sizeFactor);
			if (Debug.video) System.out.println("VideoPlayer -> display -> scaled yCoord : " + scaledYCoord);
			scaledXDim = (int) (xDim * sizeFactor);
			if (Debug.video) System.out.println("VideoPlayer -> display -> scaled xDim : " + scaledXDim);
			scaledYDim = (int) (yDim * sizeFactor);
			if (Debug.video) System.out.println("VideoPlayer -> display -> scaled yDim : " + scaledYDim);

			// Set the size of the JPanel according to input parameters.
			if (true == controls){
				mediaPanel.setBounds(0, 	0, scaledXDim, scaledYDim+25);
				outerPanel.setBounds(scaledXCoord, 	scaledYCoord, scaledXDim, scaledYDim+25);

			}
			else{
				mediaPanel.setBounds(0, 	0, scaledXDim, scaledYDim);
				outerPanel.setBounds(scaledXCoord, 	scaledYCoord, scaledXDim, scaledYDim);
			}

			// Add the JPanel to the main window.
			Gui.getSlidePanel().add(outerPanel,new Integer(super.getZOrder()));

			// Add the mouse monitor

			// If autoplay is True, then begin playback immediately.
			if (autoplay) {
				if (Debug.video) System.out.println("VideoPlayer -> display -> Autoplay enabled");
				player.start();
				MouseMonitor = new LocalMouseMonitor(this, new Point (scaledXCoord, scaledYCoord), new Dimension(scaledXDim, scaledYDim));
				MouseMonitor.setNotifications(true, false, false, true, false, false);
				MouseMonitor.addZone("videoContent", new Point(0, 0), new Dimension(scaledXDim, scaledYDim));
			}
			// If autoplay is False, then display the stopped player.
			else {
				if (Debug.video) System.out.println("VideoPlayer -> display -> Autoplay disabled");
				player.realize();
				// Implement mouse listener, but zone sizes depend on if controls are used.
				MouseMonitor = new LocalMouseMonitor(this, new Point (scaledXCoord, scaledYCoord), new Dimension(scaledXDim, scaledYDim));
				MouseMonitor.setNotifications(true, false, false, true, false, false);
				MouseMonitor.addZone("videoContent", new Point(0, 0), new Dimension(scaledXDim, scaledYDim));
			}

			// This might be a redisplay, so the video component and controls
			// also need to be scaled.
			if (realizeComplete){
				videoComponent.setBounds(0, 0, scaledXDim, scaledYDim);
				if (controls){
					controlComponent.setBounds(0, scaledYDim, scaledXDim, 25);
				}
			}

			Gui.getSlidePanel().repaint();
			mediaPanel.repaint();
		}
		else{
			System.out.println("VideoPlayer -> display -> There is no player to display!");
		}
	}


	/**
	 * Removes the media item from the main screen and deallocates it from memory.
	 */
	public void unDisplay(){
		if (Debug.video) System.out.println("VideoPlayer -> undisplay called");

		// Sets it to false
		super.setIsActive(false);
		realizeComplete = false;

		if (null != player){
			// Make sure that the program does not get stuck in video fullscreen mode.
			if (Gui.isVideoFullScreenOn()){
				gotoNormalMode();
			}

			stopMedia();

			// Check if the player is actually alive before trying to remove the MouseMonitor.
			// If there is no player, the MouseMonitor will not actually exist!
			if ((null != player) && (null != MouseMonitor)){
				MouseMonitor.destroy();
			}
			Gui.getSlidePanel().remove(outerPanel);
			Gui.getSlidePanel().repaint();
		}
		else{
			if (Debug.video) System.out.println("VideoPlayer -> unDisplay -> There was no video to remove.");
		}
	}


	/**
	 * Resized the video and control panel and coordinates according to scaleFactor.
	 * @param scaleFactor - Multiplied by the current dimensions and coordinates to
	 * give the new scaled values.
	 */
	public void resize(double scaleFactor){

		scaleFactor = (double)(Gui.getSlidePanel().getWidth())
		/ (double)slideDimension.width;

		if (Debug.video) System.out.println("VideoPlayer -> resize -> resize called");

		if ((null != player) && (realizeComplete) && (!Gui.isVideoFullScreenOn())){
			// Calculate the new scaled coordinates and dimensions.

			scaledXCoord = (int) (xCoord * scaleFactor);
			scaledYCoord = (int) (yCoord * scaleFactor);
			scaledXDim = (int) (xDim * scaleFactor);
			scaledYDim = (int) (yDim * scaleFactor);

			// Set the new dimensions, depending if controls are present or not.
			if (true == controls){
				mediaPanel.setBounds(0, 0, scaledXDim, scaledYDim+25);
				outerPanel.setBounds(scaledXCoord, scaledYCoord, scaledXDim, scaledYDim+25);
				controlComponent.setBounds(0, scaledYDim, scaledXDim, 25);
			}
			else{
				mediaPanel.setBounds(0, 0, scaledXDim, scaledYDim);
				outerPanel.setBounds(scaledXCoord, scaledYCoord, scaledXDim, scaledYDim);
			}
			videoComponent.setBounds(0, 0, scaledXDim, scaledYDim);
			MouseMonitor.rescale(scaledXDim, scaledYDim);
			MouseMonitor.reposition(scaledXCoord, scaledYCoord);
			if (Debug.video) System.out.println("VideoPlayer -> resize -> mediaPanel new size : " + mediaPanel.getSize());
		}
	}


	/**
	 * Starts playback from the current position.
	 */
	public void playMedia() {
		if (null != player)
			player.start();
			if (Debug.video) System.out.println("VideoPlayer -> Player started by playMedia");
	}


	/**
	 * Pauses playback at the current position.
	 */
	public void pauseMedia() {
		if (null != player) {
			player.stop();
			if (Debug.video) System.out.println("VideoPlayer -> Player paused by pauseMedia");
		}
	}


	/**
	 * Stops playback of the current media and returns the playback point to
	 * the start.
	 */
	public void stopMedia() {
		if ((null != player) && realizeComplete) {
			player.stop();
			player.setMediaTime(new Time(0));
			if (Debug.video) System.out.println("VideoPlayer -> Player stopped by stopMedia");
		}
	}


	/**
	 * Mutes the audio output from the current media file.
	 */
	public void muteMedia() {
		if (Debug.video) System.out.println("VideoPlayer -> muteMedia called");

		if ((null != player) && realizeComplete) {
			gainControl.setMute(true);
			if (Debug.video) System.out.println("VideoPlayer -> muteMedia set");
		}
	}


	/**
	 * Restores the previous volume level before muteMedia() was called.
	 */
	public void unMuteMedia() {
		if (Debug.video) System.out.println("VideoPlayer -> unMuteMedia called");

		if ((null != player) && realizeComplete) {
			gainControl.setMute(false);
			if (Debug.video) System.out.println("VideoPlayer -> unMuteMedia un-set");
		}
	}


	/**
	 * Sets the volume level of the current media file audio.
	 * @param volume - a percentage volume level, between 0 and 80.
	 */
	public void setVolume(float volume) {
		if (Debug.video) System.out.println("VideoPlayer -> setVolume called with " + volume);
		if ((null != player) && (realizeComplete)) {

			// Verify the input
			if (80 < volume){
				if (Debug.video) System.out.println("VideoPlayer -> setVolume -> vol over limit");
				volume = 80;
			}
			if (0 > volume){
				if (Debug.video) System.out.println("VideoPlayer ->  setVolume -> vol under limit");
				volume = 0;
			}

			// setLevel takes a float value between 0 and 1
			gainControl.setLevel(volume/100);
			if (Debug.video) System.out.println("VideoPlayer ->  setVolume -> volume set by setVolume");
		}
	}


	/**
	 * Allows external classes to check that the Player has been realised before
	 *  trying to call methods on it, which may otherwise cause exceptions.
	 */
	public boolean getRealizeComplete(){
		return realizeComplete;
	}




	/*///////////////////////////////
	 *
	 *     ACTION METHODS
	 *
	 *//////////////////////////////


	/**
	 * Called whenever the Player exhibits certain behaviour, such as reaching the end
	 * of a media file.  This is used to create the initial visual element and
	 * control panel, and also to handle the Loop flag in the data structure.
	 */
	public synchronized void controllerUpdate(ControllerEvent event) {

		if (Debug.video) System.out.println("VideoPlayer -> CONTROLLER EVENT");

		// If the player has just been realized, then create the control panel.
		if (event instanceof RealizeCompleteEvent){

			//  Now that the player is realized, get the gain controls to allow volume control.
			gainControl = player.getGainControl();

			if (Debug.video) System.out.println("VideoPlayer -> Realize complete");

			// Check whether a control panel was requested, then create one if needed.
			if ((controls) && (controlComponent = player.getControlPanelComponent()) != null) {

				if (Debug.video) System.out.println("VideoPlayer -> Creating control panel");

				// Fix the size of the control panel
                Dimension controlDim = controlComponent.getPreferredSize();
                controlDim.height = 25;
                controlDim.width = scaledXDim;
                controlComponent.setPreferredSize(controlDim);
                mediaPanel.add(controlComponent);
                controlComponent.setBounds(0, scaledYDim, scaledXDim, 25);
            }

			// If the input file has a visual component (which it should)
			// then create it and draw it in mediaPanel.
			if ((videoComponent = player.getVisualComponent()) != null){
				if (Debug.video) System.out.println("Creating visual component");
				Dimension videoDim = videoComponent.getPreferredSize();
				videoDim.height = scaledYDim;
				videoDim.width = scaledXDim;
				videoComponent.setPreferredSize(videoDim);
				mediaPanel.add(videoComponent);
				videoComponent.setBounds(0, 0, scaledXDim, scaledYDim);
			}

			mediaPanel.validate();
			realizeComplete = true;
			// Sets it to active
			super.setIsActive(true);
			//Manager.setHint(Manager.LIGHTWEIGHT_RENDERER, true);
		}

		// At the end of the media, loop round if loop is set
		else if (event instanceof EndOfMediaEvent){
			if (loop){
				stopMedia();
				playMedia();
			}
			else {
				stopMedia();
			}
		}
	}


	/**
	 * Method called when mouse is dragged.
	 *
	 * @param startZone is the String of the zone started in.
	 * @param mouseX is the integer of the mouse x position.
	 * @param mouseY is the integer of the mouse y position.
	 * @param dragComplete if the boolean, true when dragging finished.
	 */
	public void dragEventOccurred(String startZone, int mouseX, int mouseY,
			Boolean dragComplete) {
		// TODO Auto-generated method stub

	}


	/**
	 * Method called when mouse event occurs in a zone.
	 *
	 * @param eventZone is the String name of the zone.
	 * @param eventType is the integer of the type of zone.
	 */
	public void zoneEventOccurred(String eventZone, int eventType) {
		// TODO Auto-generated method stub
		System.out.println("MOUSE EVENT");
		//System.out.println("Player state on click : " + playerState);

		if(eventType==ZoneEvents.ENTER){
			if ((player.getState() == Controller.Realized)
					|| player.getState() == Controller.Started
					|| player.getState() == Controller.Prefetched){
				Gui.changeCursor(Cursor.HAND_CURSOR);
			}

		}
		if (eventType == ZoneEvents.CLICK){
			if (Debug.video) System.out.println("VideoPlayer -> Click occurred in zone : " + eventZone);
			// Player is stopped, so start it.
			if ((player.getState() == Controller.Realized) && (player.getState() != Controller.Started)){
				player.start();
				if (Debug.video) System.out.println("VideoPlayer -> player in realised state");
			}
			// Player is playing, so maximise it.
			else if (player.getState() == Controller.Started){
				if (Debug.video) System.out.println("VideoPlayer -> player in started state");
				if (Gui.isVideoFullScreenOn()){
					gotoNormalMode();
				}
				else{
					gotoFullScreenMode();
				}
			}
			else if ((player.getState() == Controller.Prefetched)){
				if (Gui.isVideoFullScreenOn()){
					gotoNormalMode();
				}
			}
		}
	}




	/*///////////////////////////////
	 *
	 *     PRIVATE METHODS
	 *
	 *//////////////////////////////


	//close the player before dying
	protected void finalize(){
		player.close();
	}


	private void processMedia(String path) {

		// Check if the path is a URL and handle it appropriately.
    	if (path.regionMatches(0, "http://", 0, 7)){
    		try {
        		if (Debug.video) System.out.println("VideoPlayer -> processMedia -> Looks like the path is a URL");
        		URL mediaURL = new URL(path);
        		player = Manager.createPlayer(mediaURL);
                player.addControllerListener(this);

        	}
        	 catch (MalformedURLException e) {
        		 // Path is simply invalid, so no player is created.
        		 if (Debug.video) System.out.println("VideoPlayer -> processMedia -> path not a valid URL!");
        		 else {
        			 System.out.println("VideoPlayer -> processMedia -> Got exception " + e);
        		 }
             }
        	 catch (IOException e) {
        		 // Path is simply invalid, so no player is created.
        		 if (Debug.video) System.out.println("VideoPlayer -> processMedia -> IO Exception occurred!");
        		 else {
        			 System.out.println("VideoPlayer -> processMedia -> Got exception " + e);
        		 }
             }
        	 catch (NoPlayerException e) {
        		 // Path is simply invalid, so no player is created.
        		 if (Debug.video) System.out.println("VideoPlayer -> processMedia -> Player could not be created!");
        		 else {
        			 System.out.println("VideoPlayer -> processMedia -> Got exception " + e);
        		 }
             }
    	}
    	//  If the path is not a URL, then use a File
    	else{
    		try {
    			if (Debug.video) System.out.print("VideoPlayer -> processMedia -> Looks like the path is local... ");
                File mediaFile = new File(path);
                if (Debug.video) System.out.println(path);
                MediaLocator mediaLocation = new MediaLocator(mediaFile.toURL());
                player = Manager.createPlayer(mediaLocation);
                player.addControllerListener(this);
            }
            catch (IOException e) {
            	if (Debug.video) System.out.println("VideoPlayer -> processMedia -> path is not a valid local path");
            	else{
            		System.out.println("VideoPlayer -> processMedia -> Got exception " + e);
            	}
            }
            catch (NoPlayerException e) {
       		 // Path is simply invalid, so no player is created.
            	if (Debug.video) System.out.println("VideoPlayer -> processMedia -> Player could not be created!");
       		 	else {
       		 		System.out.println("VideoPlayer -> processMedia -> Got exception " + e);
       		 	}
            }
    	}
	}


	private void gotoFullScreenMode(){

		Dimension videoSize;
		Dimension panelSize;
		double panelAspectRatio;
		double videoAspectRatio;
		int videoHeight;
		int videoWidth;
		int offsetHeight;
		int offsetWidth;

		// Tell the GUI to enter a special case of fullscreen mode.
		Gui.setVideoFullScreenMode();

		panelSize = Gui.getSlidePanel().getSize();

		// Set the outerpanel to fill the screen.  This stops any other entities
		// or backgroud items appears in the gaps above/alongside the video.
		outerPanel.setBounds(0,0,panelSize.width, panelSize.height);

		if (Debug.video) System.out.println("VideoPlayer -> maximise -> SlidePanel size : " + Gui.getSlidePanel().getSize());

    	// Calculate aspect ratios for screen and video.
    	panelAspectRatio = ((double)Gui.getSlidePanel().getSize().width / (double)Gui.getSlidePanel().getSize().height);
    	videoSize = videoComponent.getPreferredSize();
    	videoAspectRatio = ((double) videoSize.width / (double) videoSize.height);
    	if (Debug.video) System.out.println("VideoPlayer -> maximise -> panel aspect ratio : " + panelAspectRatio);
    	if (Debug.video) System.out.println("VideoPlayer -> maximise -> video aspect ratio : " + videoAspectRatio);

    	// If the video has a bigger aspect ratio than the screen,
    	// then display as 'letterbox'.
    	if (videoAspectRatio > panelAspectRatio){

    		if (Debug.video) System.out.println("VideoPlayer -> maximise -> Letterbox mode");
    		// Calculate video height as a fraction of the screen width.
    		videoHeight = (int) (panelSize.width / videoAspectRatio);

    		// If controls are present then take these into account in the JFrame size.
    		if (true == controls){
    			// Calculate how far down the screen the 'letterbox' should be.
    			offsetHeight = (panelSize.height - (videoHeight+25))/2;
        		mediaPanel.setBounds(0, offsetHeight, panelSize.width, videoHeight+25);
    			controlComponent.setBounds(0, videoHeight, panelSize.width, 25);
    		}
    		else{
    			offsetHeight = (panelSize.height - (videoHeight))/2;
        		mediaPanel.setBounds(0, offsetHeight, panelSize.width, videoHeight);
    		}
        	videoComponent.setBounds(0, 0, panelSize.width, videoHeight);
        	// Correct the mouse monitor zone.
        	MouseMonitor.rescale(panelSize.width, videoHeight);
        	MouseMonitor.reposition(0, offsetHeight);
    	}

    	// If the video has a lower aspect ratio than the screen,
    	// then use the 'letterbox' technique sideways.
    	else if (videoAspectRatio < panelAspectRatio){

    		if (Debug.video) System.out.println("VideoPlayer -> maximise -> Sideways letterbox mode");

    		// If controls are present then take these into account in the JFrame size.
    		if (true == controls){
	    		videoWidth = (int) ((panelSize.height-25) * videoAspectRatio);
	    		// Calculate how far in the sideways 'letterbox' should start.
	    		offsetWidth = (panelSize.width - videoWidth)/2;
	    		controlComponent.setBounds(0, (panelSize.height-25), videoWidth, 25);
	    		videoComponent.setBounds(0, 0, videoWidth, panelSize.height-25);
	    		// Correct the mouse monitor zone.
	    		MouseMonitor.rescale(videoWidth, panelSize.height-25);
    		}
    		else{
    			videoWidth = (int) (panelSize.height * videoAspectRatio);
    			// Calculate how far in the sideways 'letterbox' should start.
	    		offsetWidth = (panelSize.width - videoWidth)/2;
	    		videoComponent.setBounds(0, 0, videoWidth, panelSize.height);
	    		// Correct the mouse monitor zone.
	    		MouseMonitor.rescale(videoWidth, panelSize.height);
    		}
    		MouseMonitor.reposition(offsetWidth, 0);
    		mediaPanel.setBounds(offsetWidth, 0, videoWidth, panelSize.height);
    	}

    	// If the video has the same aspect ratio as the screen, then use all the space!
    	else if (videoAspectRatio == panelAspectRatio){

    		if (Debug.video) System.out.println("VideoPlayer -> maximise -> Square mode");

    		// Snip a little bit off the height,
    		// otherwise the controls will appear under the are reserved
    		// for the Windows start bar.
    		if (controls){
    			panelSize.height = panelSize.height - 65;
    		}

    		if (true == controls){
    			mediaPanel.setBounds(0, 0, panelSize.width, panelSize.height);
    			controlComponent.setBounds(0, panelSize.height-25, panelSize.width, 25);
    			videoComponent.setBounds(0, 0, panelSize.width, panelSize.height-25);
    			// Correct the mouse monitor zone.
    			MouseMonitor.rescale(panelSize.width, panelSize.height-25);
    		}
    		else{
    			mediaPanel.setBounds(0, 0, panelSize.width, panelSize.height);
    			videoComponent.setBounds(0, 0, panelSize.width, panelSize.height);
    			// Correct the mouse monitor zone.
    			MouseMonitor.rescale(panelSize.width, panelSize.height);
    		}
    		MouseMonitor.reposition(0,0);
    	}
	}


	private void gotoNormalMode(){

		if (Debug.video) System.out.println("NORMAL SIZE");

		// Tell the GUI to exit the special fullscreen mode.
		Gui.setVideoWindowedMode();

		// Set the size of the JPanel according to the previous scaled coordinates and dimensions.
		videoComponent.setBounds(0, 0, scaledXDim, scaledYDim);
		videoComponent.setPreferredSize(new Dimension(scaledXDim, scaledYDim));
		// If a control panel is present, take it into account with the dimensions.
		if (true == controls){
			mediaPanel.setBounds(0, 	0, scaledXDim, scaledYDim+25);
			outerPanel.setBounds(scaledXCoord, 	scaledYCoord, scaledXDim, scaledYDim+25);
			controlComponent.setBounds(0, scaledYDim, scaledXDim, 25);
		}
		else{
			mediaPanel.setBounds(0, 	0, scaledXDim, scaledYDim);
			outerPanel.setBounds(scaledXCoord, 	scaledYCoord, scaledXDim, scaledYDim);
		}

		if (Debug.video) System.out.println("VideoPlayer -> maximise -> video dimensions : " + videoComponent.getBounds());

		// Correct the mouse monitor zone.
		MouseMonitor.rescale(scaledXDim, scaledYDim);
		MouseMonitor.reposition(scaledXCoord, scaledYCoord);
	}




	/*///////////////////////////////
	 *
	 *     TESTING METHODS
	 *
	 *//////////////////////////////


	/**
	 * FOR TESTING PURPOSES ONLY
	 *
	 * Allows parameters to be passed to the video player without
	 * the need for the data structure.
	 *
	 * @param aPath
	 * 			file Path of the Audio file.
	 * @param xPos
	 * 			x coordinate where the audio should appear on the slide.
	 * @param yPos
	 * 			y coordinate where the audio should appear on the slide.
	 * @param width
	 * 			is the required width of the video when it is displayed.
	 * @param height
	 * 			is the required height of the video when it is displayed.
	 * @param aLoop
	 * 			determines if audio should loop. Set true to loop playback, set false
	 * 			for non looped playback.
	 * @param aControls
	 * 			determines if controls should be displayed.
	 * 			Set true to display controls, set false to have no controls displayed.
	 * @param anAutoplay
	 * 			determines if playback of audio should start should immediately
	 * 			when the audio is displayed. Set true to start playback immediately when,
	 * 			set false playback will need to be triggered by the user.
	 */
	public VideoPlayer(String aPath, int xPos, int yPos, int width, int height, boolean aLoop, boolean aControls, boolean anAutoplay,int zOrder) {

		if (Debug.video) System.out.println("CONSTRUCTOR CALLED");

		// Allows the ControllerListener to see whether a control panel should be created.
		this.controls = aControls;
		// Allows the ControllerListener to know whether to restart the media at its end.
		this.loop = aLoop;
		// If set, tells the player to begin playback as soon as loading is complete.
		this.autoplay = anAutoplay;
		// Contains the location of the media file.
		//this.path = aPath;
		// Set the class level coordinates equal to the input coordinates.
		this.xCoord = xPos;
		this.yCoord = yPos;
		this.xDim = width;
		this.yDim = height;
		super.setZOrder(zOrder);
		// Create a JPanel to contain the control panel and video content.
		mediaPanel = new JPanel();
		mediaPanel.setLayout(null);
		outerPanel = new JPanel();
		outerPanel.setLayout(null);
		outerPanel.add(mediaPanel);
		outerPanel.setBackground(new Color(0,0,0));

		processMedia(aPath);
	}


	/**
	 * FOR TESTING PUPOSES ONLY
	 *
	 * Allows for audio player testing by running the VideoPlayer file.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		SafetyNet myApp = new SafetyNet();
		int zOrder = 10;	//if you can't see the video display it higher
		VideoPlayer video = new VideoPlayer("/F:/SWENG/Programming/Peep Show S06E01 - H264MENCODER-MP3.AVI", 200, 200, 600, 300, false, true, true,zOrder);
		//AudioPlayer audio = new AudioPlayer("/F:/SWENG/Programming/LTT.mp2", 20, 20, true, true, true);

		//path for MW testing: /C:/Users/Mark/Desktop/heroes.419.hdtv-lol.avi

		//audio.display();

		try{
			Thread.sleep(3000);
		}
		catch(InterruptedException e){
			System.out.println("Wiggly woo");
		}

		video.display();
		System.out.println("Video displayed");

		/*
		try{
			Thread.sleep(5000);
		}
		catch(InterruptedException e){
			System.out.println("Wiggly woo");
		}

		video.setVolume(100);


		/*
		video.unDisplay();
		System.out.println("Video undisplayed");


		try{
			Thread.sleep(5000);
		}
		catch(InterruptedException e){
			System.out.println("Wiggly woo");
		}


		video.display();
		System.out.println("Video redisplayed");


		try{
			Thread.sleep(5000);
		}
		catch(InterruptedException e){
			System.out.println("Wiggly woo");
		}
		*/

	}
}