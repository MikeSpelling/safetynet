/************************************************
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		AudioPlayer
 *
 *  Author: 		Harry Taylor
 *
 *  Contributers:	.
 *  Testers:		.
 *
 *  Description:	This class creates an audio player
 *  				for a file path passed in.
 *
 *	Known bugs: 	Be Careful that JMF has been set up properly.
 *
 ***********************************************/

package cfss.org.modules;

import java.awt.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.media.*;
import cfss.org.flags.Debug;
import cfss.org.safetynet.Entity;
import cfss.org.safetynet.SafetyNet;
import cfss.org.safetynet.gui.Gui;
import cfss.org.xmlstructure.Audio;

/**
 * 	This class creates an audio player for a file path passed in.
 *
 *	Known bugs: Be careful that JMF has been set up properly.
 *
 *	@author Harry Taylor
 */
public class AudioPlayer extends Entity implements ControllerListener{

	private JPanel mediaPanel;  // This is what the control panel is added to, and what is returned by the constructor.
	private GainControl gainControl = null;  // This is used to control volume
	private Player player=null;  // This is the actual object that plays back the media
	private boolean controls=true;  // Should a control panel be drawn?
	private boolean loop=false;  // Should the media start again after it reaches the end?
	private boolean autoplay = false;  // Should the media begin playback immediately?
	private boolean realizeComplete = false;  // A flag to indicate whether the player has been realized.
	private Component controlComponent;  // Contains the control panel.  Used to manipulate its size and position.
	// Original coordinates.  Only updated in the constructor.
	private int xCoord;
	private int yCoord;
	// Scaled coordinates.  Updated by any change in window size.
	private int scaledXCoord;
	private int scaledYCoord;
	private int scaledXDim;


	/**
	 * The constructor method.  Stores information in the data structure as
	 * fields, creates a JPanel for the media, then loads the media file ready
	 * to display.
	 *
	 * @param anAudio is the Audio to be used.
	 */
	public AudioPlayer(Audio anAudio){

		// sets z order
		super.setZOrder(anAudio.getZOrder());

		// Copy the data structure information to class-level variables as required.
		this.controls = anAudio.isControls();
		this.loop = anAudio.isLoop();
		this.autoplay= anAudio.isAutoplay();
		this.xCoord = anAudio.getStartPoint().getX();
		this.yCoord = anAudio.getStartPoint().getY();

		// Create a JPanel to contain the control panel and video content.
		mediaPanel = new JPanel();
		mediaPanel.setLayout(null);

		// Load in the video file and create the player, ready to begin playback.
		processMedia(anAudio.getPath());
	}


	/*///////////////////////////////
	 *
	 *     PUBLIC METHODS
	 *
	 *//////////////////////////////


	/**
	 * Displays the media in the main program slidePanel.
	 * Scales the desired dimensions and coordinates to fit the window size.
	 * Starts the Player if the Autoplay flag is set in the data structure.
	 */
	public void display(){

		Dimension currentPanelSize;  // Current size of the presentation panel.
		double sizeFactor;  // Factor between 800x600 grid and current panel size.

		if (Debug.audio) System.out.println("AudioPlayer -> display called");

		if (null != player){
			currentPanelSize = Gui.getSlidePanel().getSize();
			// Must typecast here, otherwise integer maths is performed.
			sizeFactor = ((double)currentPanelSize.width / (double)800);
			if (Debug.audio) System.out.println("AudioPlayer -> display -> scale factor : " + sizeFactor);

			// Now apply the scale to the desired coordinates and dimensions.
			// These scaled results will be used in this class from now on.
			scaledXCoord = (int) (xCoord * sizeFactor);
			if (Debug.audio) System.out.println("AudioPlayer -> display -> scaled xCoord : " + scaledXCoord);
			scaledYCoord = (int) (yCoord * sizeFactor);
			if (Debug.audio) System.out.println("AudioPlayer -> display -> scaled yCoord : " + scaledYCoord);
			scaledXDim = (int) (250 * sizeFactor);
			if (Debug.audio) System.out.println("AudioPlayer -> display -> scaled xDim : " + scaledXDim);

			// Set the size of the JPanel according to input parameters.
			mediaPanel.setBounds(scaledXCoord, 	scaledYCoord, scaledXDim, 25);

			// Add the JPanel to the main window.
			Gui.getSlidePanel().add(mediaPanel, new Integer(super.getZOrder()));
			Gui.getSlidePanel().repaint();

			// If autoplay is True, then begin playback immediately.
			if (autoplay) {
				if (Debug.audio) System.out.println("AudioPlayer -> display -> Autoplay enabled");
				player.start();
			}
			// If autoplay is False, then display the stopped player.
			else {
				if (Debug.audio) System.out.println("AudioPlayer -> display -> Autoplay disabled");
				player.realize();
			}
		}
	}


	/**
	 * Removes the media item from the main screen and deallocates it from
	 * memory.
	 */
	public void unDisplay(){
		// Sets it to false
		super.setIsActive(false);
		realizeComplete = false;
		if (null != player){
			if (Debug.audio) System.out.println("AudioPlayer -> undisplay called");
			player.stop();
			Gui.getSlidePanel().remove(mediaPanel);
			Gui.getSlidePanel().repaint();
		}
	}


	/**
	 * Resizes the control panel and coordinates according to scaleFactor.
	 * @param scaleFactor - Multiplied by the current dimensions and coordinates
	 * to give the new scaled values.
	 */
	public void resize(double scaleFactor){

		if (Debug.audio) System.out.println("AudioPlayer -> Resize called");

		if((null != player) && (realizeComplete)){
			scaledXCoord = (int) (scaledXCoord * scaleFactor);
			scaledYCoord = (int) (scaledYCoord * scaleFactor);
			scaledXDim = (int) (scaledXDim * scaleFactor);

			mediaPanel.setBounds(scaledXCoord, scaledYCoord, scaledXDim, 25);
			// Set the new dimensions, depending if controls are present or not.
			if (true == controls){
				controlComponent.setBounds(0, 0, scaledXDim, 25);
			}
		}
	}


	/**
	 * Starts playback from the current position.
	 */
	public void playMedia() {
		if (null != player)
			player.start();
			if (Debug.audio) System.out.println("AudioPlayer -> Player started by playMedia");
	}


	/**
	 * Pauses playback at the current position.
	 */
	public void pauseMedia() {
		if (null != player) {
			player.stop();
			if (Debug.audio) System.out.println("AudioPlayer -> Player paused by pauseMedia");
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
			if (Debug.audio) System.out.println("AudioPlayer -> Player stopped by stopMedia");
		}
	}


	/**
	 * Mutes the audio output from the current media file.
	 */
	public void muteMedia() {
		if (Debug.audio) System.out.println("AudioPlayer -> muteMedia called");

		if ((null != player) && realizeComplete) {
			gainControl.setMute(true);
			if (Debug.audio) System.out.println("AudioPlayer -> muteMedia set");
		}
	}


	/**
	 * Restores the previous volume level before muteMedia() was called.
	 */
	public void unMuteMedia() {
		if (Debug.audio) System.out.println("AudioPlayer -> unMuteMedia called");

		if ((null != player) && realizeComplete) {
			gainControl.setMute(false);
			if (Debug.audio) System.out.println("AudioPlayer -> unMuteMedia un-set");
		}
	}


	/**
	 * Sets the volume level of the current media file audio.
	 * @param volume - a percentage volume level, between 0 and 80.
	 */
	public void setVolume(float volume) {
		if (Debug.audio) System.out.println("AudioPlayer -> setVolume called with " + volume);
		if ((null != player) && (realizeComplete)) {

			// Verify the input
			if (80 < volume){
				if (Debug.audio) System.out.println("AudioPlayer -> setVolume -> vol over limit");
				volume = 80;
			}
			if (0 > volume){
				if (Debug.audio) System.out.println("AudioPlayer -> setVolume -> vol under limit");
				volume = 0;
			}

			// setLevel takes a float value between 0 and 1
			if (Debug.audio) System.out.println("AudioPlayer -> setVolume -> about to setLevel to " + volume/100);
			gainControl.setLevel(volume/100);
			if (Debug.audio) System.out.println("AudioPlayer -> setVolume -> volume set by setVolume");
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
	 * of a media file.  This is used to create the
	 * control panel, and also to handle the Loop flag in the data structure.
	 */
	public synchronized void controllerUpdate(ControllerEvent event) {

		if (Debug.audio) System.out.println("CONTROLLER EVENT");

		// If the player has just been realized, then create the control panel.
		if (event instanceof RealizeCompleteEvent){

			//  Now that the player is realized, get the gain controls to allow volume control.
			gainControl = player.getGainControl();

			if (Debug.audio) System.out.println("AudioPlayer -> Realize complete");

			// Check whether a control panel was requested, then create one if needed.
			if ((controls) && (controlComponent = player.getControlPanelComponent()) != null) {

				if (Debug.audio) System.out.println("AudioPlayer -> Creating control panel");

				// Fix the size of the control panel
                Dimension controlDim = controlComponent.getPreferredSize();
                controlDim.height = 25;
                controlDim.width = scaledXDim;
                controlComponent.setPreferredSize(controlDim);
                mediaPanel.add(controlComponent);
                controlComponent.setBounds(0, 0, scaledXDim, 25);
            }
			mediaPanel.validate();
			realizeComplete = true;
			// Sets it to active
			super.setIsActive(true);

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
        		if (Debug.audio) System.out.println("AudioPlayer -> " +
        				"processMedia -> Looks like the path is a URL");
        		URL mediaURL = new URL(path);
        		player = Manager.createPlayer(mediaURL);
                player.addControllerListener(this);

        	}
        	 catch (MalformedURLException e) {
        		 // Path is simply invalid, so no player is created.
        		 if (Debug.audio) System.out.println("AudioPlayer -> " +
        		 		"processMedia -> path not a valid URL!");
        		 else {
        			 System.out.println("AudioPlayer -> processMedia -> " +
        			 		"Got exception " + e);
        		 }
             }
        	 catch (IOException e) {
        		 // Path is simply invalid, so no player is created.
        		 if (Debug.audio) System.out.println("AudioPlayer -> " +
        		 		"processMedia -> IO Exception occurred!");
        		 else {
        			 System.out.println("AudioPlayer -> processMedia -> " +
        			 		"Got exception " + e);
        		 }
             }
        	 catch (NoPlayerException e) {
        		 // Path is simply invalid, so no player is created.
        		 if (Debug.audio) System.out.println("AudioPlayer -> " +
        		 		"processMedia -> Player could not be created!");
        		 else {
        			 System.out.println("AudioPlayer -> processMedia -> " +
        			 		"Got exception " + e);
        		 }
             }
    	}
    	//  If the path is not a URL, then use a File
    	else{
    		try {
    			if (Debug.audio) System.out.println("AudioPlayer -> " +
    					"processMedia -> Looks like the path is local");
                File mediaFile = new File(path);
                MediaLocator mediaLocation = new MediaLocator(mediaFile.toURL());
                player = Manager.createPlayer(mediaLocation);
                player.addControllerListener(this);
            }
            catch (IOException e) {
            	if (Debug.audio) System.out.println("AudioPlayer -> " +
            			"processMedia -> path is not a valid local path");
            	else{
            		System.out.println("AudioPlayer -> processMedia -> " +
            				"Got exception " + e);
            	}
            }
            catch (NoPlayerException e) {
       		 // Path is simply invalid, so no player is created.
            	if (Debug.audio) System.out.println("Player could not be " +
            			"created!");
       		 	else {
       		 		System.out.println("AudioPlayer -> processMedia -> " +
       		 				"Got exception " + e);
       		 	}
            }
    	}
	}


	/*///////////////////////////////
	 *
	 *     TESTING METHODS
	 *
	 *//////////////////////////////


	/**
	 * FOR TESTING PURPOSES ONLY
	 *
	 * Allows parameters to be passed to the audio player without
	 * the need for the data structure.
	 *
	 * @param aPath
	 * 			file Path of the Audio file
	 * @param xPos
	 * 			x coordinate where the audio should appear on the slide
	 * @param yPos
	 * 			y coordinate where the audio should appear on the slide
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
	public AudioPlayer(String aPath, int xPos, int yPos, boolean aLoop, boolean aControls, boolean anAutoplay) {

		if (Debug.audio) System.out.println("AudioPlayer -> CONSTRUCTOR CALLED");

		// Allows the ControllerListener to see whether a control panel should be created.
		this.controls = aControls;
		// Allows the ControllerListener to know whether to restart the media at its end.
		this.loop = aLoop;

		this.autoplay = anAutoplay;

		// Set the class level coordinates equal to the input coordinates
		this.xCoord = xPos;
		this.yCoord = yPos;
		// This is where the control panel will be placed, if required.
		mediaPanel = new JPanel();
		mediaPanel.setLayout(null);
		//mediaPanel.setSize(200, 25);

		processMedia(aPath);
	}


	/**
	 * FOR TESTING PURPOSES ONLY
	 *
	 * Allows for audio player testing by running the AudioPlayer file.
	 * @param args
	 */
	public static void main(String[] args) {
		new SafetyNet();
		AudioPlayer audio = new AudioPlayer("/C:/MP3's/terror.mp3", 100, 100, true, true, true);

		audio.display();

		try{
			Thread.sleep(5000);
		}
		catch(InterruptedException e){
			System.out.println("Wiggly woo");
		}

		audio.unDisplay();

		/*
		audio.resize(1);

		try{
			Thread.sleep(5000);
		}
		catch(InterruptedException e){
			System.out.println("Wiggly woo");
		}


		audio.unDisplay();

		try{
			Thread.sleep(5000);
		}
		catch(InterruptedException e){
			System.out.println("Wiggly woo");
		}

		audio.display();
		*/

	}

}