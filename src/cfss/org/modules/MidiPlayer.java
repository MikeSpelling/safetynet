/**
 *
 * Company Name: 	Touchware Systems
 * Product Name: 	infoHub
 * Author: 			Levente Szabo
 * Contributors: 	Rob Aston, Christopher Booth, Usman Syed
 * Created: 		14/05/2010
 * Modified: 		27/05/2010
 * Version: 		1.1
 *
 */
/**
 *
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		MidiPlayer
 *
 *  Author: 		Touchware Systems
 *
 *  Contributers:	Michael Spelling
 *  Testers:		Michael Spelling
 *
 *  Description:	Module to play MIDI files.
 *
 */


package cfss.org.modules;

import java.io.File;
import java.io.IOException;
import javax.sound.midi.*;
//import javax.swing.JPanel; // Mike S: Removed unused import
import cfss.org.flags.Debug;
import cfss.org.safetynet.*; // Mike S: Added import
import cfss.org.xmlstructure.Midi;



/**
 * This class creates a Midi Object, starts a Thread for it and plays the
 * Midi File
 *
 * @author Touchware Systems
 **/
public class MidiPlayer extends Entity { // Mike S: Renamed to MidiPlayer from
										 // Midi, added extends Entity

	private String file_path;
	private int volume = 65; // Mike S: Changed to start at middle not max
	private long position = 0; // Mike S: New variable for pause method
	private Boolean loop = false;
	private Sequencer midiSequencer;
	private Synthesizer synthesizer;
	private Boolean playable = true;
	private Boolean running = false; // Mike S: Added variable to handle errors
	private Boolean paused = false;	// Mike S: Added variable to handle errors
	private MidiThread sequencerThread; // Mike S: Move this variable to make
										// it class-wide

	/**
	 * Constructor for new Midi Object. Sets variables to those held in the
	 * Midi data structure.
	 *
	 * @param inputMidi is the Midi data structure which will set up the file
	 * path and loop boolean
	 */
	public MidiPlayer(Midi inputMidi) { // Mike S: Added set up constructor
		// sets z order
		super.setZOrder(inputMidi.getZOrder());

		file_path = inputMidi.getPath();
		loop = inputMidi.isLoop();
	}


	/**
	 * Empty constructor, variables need to be set before playing the media.
	 */
	public MidiPlayer() {
	}

	/**
	 * Creates a new Thread and starts Playing the Midi File.
	 */
	public void startMedia (){

		// Mike S: Changed to use instance variable
		sequencerThread = new MidiThread();
		if(playable) {
			super.setIsActive(true);
			sequencerThread.start();
			running = true; // Mike S: Added variable to store threads state
			paused = false;
		}

		// Old Code:
		/*MidiThread newThread = new MidiThread();
		if(playable == true){
			newThread.start();
		}*/
	}

	/**
	 * Stops playing the Midi File if it is currently playing.
	 */
	public void stopMedia() {
		if(paused)	// Mike S: Added pause handling
			midiSequencer.setTickPosition(0);
		else if(playable && running) {	// Mike S: Added running check

			midiSequencer.stop();
			super.setIsActive(false);
			running = false; // Mike S: Added to store threads state
		}
	}


	/**
	 * Mutes the Media if it is playing.
	 */
	public void muteMedia() {	// Mike S: New method muteMedia
		if(playable) {
			MidiChannel[] channels = synthesizer.getChannels();
	        for( int c = 0; channels != null && c < channels.length; c++ )
	            midiSequencer.setTrackMute(c, true);
		}
	}


	/**
	 * Unmutes playing media.
	 */
	public void unMuteMedia() {	// Mike S: New method unMuteMedia
		if(playable) {
			MidiChannel[] channels = synthesizer.getChannels();
	        for( int c = 0; channels != null && c < channels.length; c++ )
	            midiSequencer.setTrackMute(c, false);
		}
	}


	/**
	 * Stops the media from playing and stores the current position.
	 */
	public void pause() {	// Mike S: New method pause
		if(playable && running && !paused) {
			position = midiSequencer.getTickPosition();
			midiSequencer.stop();
			paused = true;
		}
	}


	/**
	 * Resumes play from the position stored when paused.
	 */
	public void unPause() {	// Mike S: New method unPause
		if(playable) {
			sequencerThread = new MidiThread();
			midiSequencer.setTickPosition(position);
			sequencerThread.start();
			paused = false;
		}
	}


	/**
	 * Starts the media playing.
	 */
	public /*JPanel*/void display() { // Mike S: Changed JPanel to void
		// Sets it to active
		super.setIsActive(true);


		// Mike S: New Code just wants to play the media
		startMedia();

		// Old Code:
		/*JPanel newPanel = new JPanel();
		newPanel.setVisible(false);
		newPanel.setOpaque(false);
		return newPanel;*/
	}


	/**
	 * Stops the media from playing.
	 */
	public void unDisplay() {	// Mike S: New method unDisplay
		// Sets it to false
		super.setIsActive(false);


		stopMedia();
	}


	/**
	 * Gets the file path of the Midi File.
	 *
	 * @return file_path is the current file path of the Midi being played.
	 */
	public String getFile_path() {
		return file_path;
	}


	/**
	 * Gets the Loop Variable.
	 *
	 * @return loop is the current loop boolean.
	 */
	public Boolean getLoop() {
		return loop;
	}


	/**
	 * Gets the Volume(Velocity) Variable.
	 *
	 * @return volume is the current volume integer between 0 and 127.
	 */
	public int getVolume() {
		return volume;
	}


	/**
	 * Sets the path of the Midi File.
	 *
	 * @param newFile_path is the file path to set the Midi to.
	 */
	public void setFile_path(String newFile_path) { // Mike S: Renamed file_path
													// newFile_path
		this.file_path = newFile_path; 	// Mike S: file_path changed to
										// newFile_path
	}


	/**
	 * Sets the Loop Variable.
	 *
	 * @param newLoop is the boolean which determines whether media is looped
	 * or not. If true the Midi will be played again once the end of the file
	 * is reached.
	 */
	public void setLoop(Boolean newLoop) { // Mike S: Renamed loop to newLoop
		this.loop = newLoop; // Mike S: loop changed to newLoop
	}


	/**
	 * Sets the Volume(Velocity) variable to the float. The float is cast to an
	 * integer.
	 *
	 * @param newVolume is the float to set the volume to. This will be cast
	 * to an integer between 0 and 127 by setting anything above 127 to 127
	 * and anything below 0 to 0.
	 */
	public void setVolume(float newVolume) { // Mike S: Renamed volume newVolume
											// Made it a float to comply.

		// Mike S: Added error handling for out of bounds volume
		if(newVolume > 127)
			newVolume = 127;
		if(newVolume < 0)
			newVolume = 0;

		this.volume = (int)newVolume; // Mike S: volume changed to newVolume
	}


	/**
	 * Sets the Volume(Velocity) variable to the input integer.
	 *
	 * @param newVolume is the integer to set the volume to. If it exceeds 127
	 * it will be set to 127 and if it is below 0 it will be set to 0.
	 */
	public void setVolume(int newVolume) { // Mike S: Added this method.
		if(newVolume > 127)
			newVolume = 127;
		if(newVolume < 0)
			newVolume = 0;

		this.volume = (int)newVolume;
	}


	/**
	 * This class creates a Thread to play a Midi File.
	 *
	 *  @author Touchware Systems
	 */
	class MidiThread extends Thread {

		/**
		 * Creates a Midi Sequencer and starts the Thread.
		 */
		public MidiThread (){
			File midiFile = new File(file_path); // Instantiates a File object
		    try {
		    	// Create a new Sequencer Object with the Midi File
		        Sequence sequence = MidiSystem.getSequence(midiFile);
		        midiSequencer = MidiSystem.getSequencer();
		        midiSequencer.open();
		        midiSequencer.setSequence(sequence);
		        // Set the Sequencer to loop
		        if(loop != null) {
		        	if(loop == true)
		        		midiSequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
		        }
		        // Set the Midi Volume
		        synthesizer = MidiSystem.getSynthesizer();
		        synthesizer.open();
		        if (synthesizer.getDefaultSoundbank() == null) {
		        	// Hardware Volume Change
		        	midiSequencer.getTransmitter().setReceiver(
		        			MidiSystem.getReceiver() );
		        	ShortMessage volumeMessage = new ShortMessage();
	                for( int i = 0; i < 16; i++ ) {
	                    volumeMessage.setMessage( ShortMessage.CONTROL_CHANGE,
	                    		i, 7, volume );
	                    MidiSystem.getReceiver().send( volumeMessage, -1 );
	                }
		        }
		        else {
		        	// Software Volume Change
				    midiSequencer.getTransmitter().setReceiver(
				    		synthesizer.getReceiver() );
				    MidiChannel[] channels = synthesizer.getChannels();
	                for(int c = 0; channels != null && c < channels.length; c++)
	                    channels[c].controlChange( 7, volume );
				}
		    }
		    catch(MidiUnavailableException mue) {
		    	System.out.print("\nError: Midi Unavailable"); // Mike S: Added
		    	playable = false;
		    }
		    catch(InvalidMidiDataException imde) {
		    	System.out.print("\nError: Midi Invalid"); // Mike S: Added
		    	playable = false;
		    }
		    catch(IOException ioe) {
		    	System.out.print("\nError: Midi IO Exception"); // Mike S: Added
		    	playable = false;
		    }
		}

		/**
		 * Overloaded method runs the Midi Thread.
		 */
		public void run() {
			midiSequencer.start();
			while(midiSequencer.isRunning()) {
	            try {
	                Thread.sleep(1000); // Check every second

	            } catch(InterruptedException ignore) {
	            	break;
	            }
	        }
			midiSequencer.close();
		}
	}


	////////////////////////////////////////////
	//Main method and testing happens below...//
	////////////////////////////////////////////

	public static void main(String[] args) {
		// Instantiate SafetyNet without warnings
		@SuppressWarnings("unused") SafetyNet myApp = new SafetyNet();

		// Run tests if true
		testMidi();
	}


	public static void testMidi() {
		if(Debug.midi == false)
			return;

		final boolean testFile_path = false;
		final boolean testDisplay = false;
		final boolean testLoop = false;
		final boolean testVolume = false;
		final boolean testMute = false;
		final boolean testPause = true;

		String file = "love.mid";
		MidiPlayer midi = new MidiPlayer();

		if(testFile_path) {
			// Test that an invalid string doesn't crash the program
			System.out.print("\n\nTesting File_path");
			String fileName = "Invalid String";
			midi.setFile_path(fileName);
			midi.setVolume(30);
			midi.setLoop(false);
			midi.display();
		}
		midi = new MidiPlayer();
		midi.setFile_path(file);
		if(testDisplay) {
			System.out.print("\n\nTesting Display");
			midi.setVolume(30);
			midi.setLoop(false);
			// Test that calling unDisplay before display doesnt cause a crash
			midi.unDisplay();
			// Test displaying and undisplaying many times.
			for(int n = 1; n <= 20; n++) {
				System.out.print("\n" + n);
				midi.display();
				System.out.print("\nDisplay");
				try{Thread.sleep(500);}
				catch(InterruptedException ie){System.out.print("Error in pause");}
				midi.unDisplay();
				System.out.print("\nUndisplay");
				try{Thread.sleep(500);}
				catch(InterruptedException ie){System.out.print("Error in pause");}
			}
		}
		if(testLoop) {
			System.out.print("\n\nTesting Loop");
			midi.setVolume(30);
			// Test setting and unsetting loop mulitple times
			for(int n = 1; n <= 2; n++) {
				System.out.print("\n" + n);
				midi.setLoop(true);
				System.out.print("\nLooping");
				midi.display();
				try{Thread.sleep(13000);}
				catch(InterruptedException ie){System.out.print("Error in pause");}
				midi.unDisplay();
				midi.setLoop(false);
				System.out.print("\nNo loop");
				midi.display();
				try{Thread.sleep(13000);}
				catch(InterruptedException ie){System.out.print("Error in pause");}
			}
		}
		if(testVolume) {
			System.out.print("\n\nTesting Volume");
			midi.setLoop(true);
			// Test changing the volume mulitple times
			for(int n = 1; n <= 20; n++) {
				System.out.print("\n" + n);
				midi.setVolume(20);
				System.out.print("\nVolume: " + midi.getVolume());
				midi.display();
				System.out.print("\nDisplay");
				try{Thread.sleep(4000);}
				catch(InterruptedException ie){System.out.print("Error in pause");}
				midi.unDisplay();
				System.out.print("\n\nUndisplay");
				midi.setVolume(125);
				System.out.print("\nVolume: " + midi.getVolume());
				midi.display();
				System.out.print("\nDisplay");
				try{Thread.sleep(4000);}
				catch(InterruptedException ie){System.out.print("Error in pause");}
				midi.unDisplay();
				System.out.print("\n\nUndisplay");
			}
			// Test an invalid range for volume
			try{Thread.sleep(4000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
			midi.unDisplay();
			System.out.print("\n\nUndisplay");
			midi.setVolume(500);
			System.out.print("\nVolume: " + midi.getVolume());
			midi.display();
			System.out.print("\nDisplay");
			try{Thread.sleep(4000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
		}
		if(testMute) {
			System.out.print("\n\nTesting Mute");
			midi.setVolume(30);
			midi.setLoop(true);
			midi.display();
			System.out.print("\nDisplay");
			// Test muting and unmuting mulitple times
			for(int n = 1; n <= 20; n++) {
				System.out.print("\n" + n);
				try{Thread.sleep(2000);}
				catch(InterruptedException ie){System.out.print("Error in pause");}
				midi.muteMedia();
				System.out.print("\nMute");
				try{Thread.sleep(2000);}
				catch(InterruptedException ie){System.out.print("Error in pause");}
				midi.unMuteMedia();
				System.out.print("\nUnmute");
			}
		}
		if(testPause) {
			System.out.print("\n\nTesting Pause");
			midi.setFile_path(file);
			midi.setVolume(60);
			midi.setLoop(true);
			midi.display();
			System.out.print("\nDisplay");

			try{Thread.sleep(3000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
			midi.pause();
			System.out.print("\nPaused");
			try{Thread.sleep(3000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
			midi.pause();
			System.out.print("\nPaused");
			try{Thread.sleep(1000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
			midi.display();
			System.out.print("\nDisplay");
			try{Thread.sleep(1000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
			midi.unDisplay();
			System.out.print("\nStopped");
			try{Thread.sleep(1000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
			midi.display();
			System.out.print("\nDisplay");
			try{Thread.sleep(1000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
			midi.pause();
			System.out.print("\nPause");
			try{Thread.sleep(1000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
			midi.pause();
			System.out.print("\nPause");
			try{Thread.sleep(1000);}
			catch(InterruptedException ie){System.out.print("Error in pause");}
			midi.display();
			System.out.print("\nDisplay");

			// Test pausing and unpausing multiple times
			for(int n = 1; n <= 20; n++) {
				System.out.print("\n" + n);
				try{Thread.sleep(1000);}
				catch(InterruptedException ie){System.out.print("Error in pause");}
				midi.pause();
				System.out.print("\nPause");
				try{Thread.sleep(1000);}
				catch(InterruptedException ie){System.out.print("Error in pause");}
				midi.unPause();
				System.out.print("\nUnpause");
			}
		}
	}
}