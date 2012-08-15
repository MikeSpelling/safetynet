/************************************************
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		SoundControl
 *
 *  Author: 		Callum Goddard
 *
 *  Contributers:	Michael Spelling
 *  Testers:		.
 *
 *
 *  Description:	This is the SoundControl panel that is contained
 *  				within the menubar.
 *
 ***********************************************/

package cfss.org.safetynet.gui;

import javax.swing.*;
import javax.swing.event.*;
import cfss.org.flags.Debug;
import cfss.org.safetynet.SlideRenderer;
import java.awt.*;
import java.awt.event.*;


/**
 * This is the SoundControl panel that is contained
 * within the menubar.  It contains the buttons for muting midi, muting all
 * and the slider for changing the volume of all audio within SafetyNet.
 *
 * All components have their functionality self contained.
 *
 * When mute midi is clicked all midi within the current slide is muted
 * When Mute all is clicked all audio on the current slide is clicked.
 * When Volume Slider is changed the volume of the slide is altered.
 *
 * @author Callum Goddard
 *
 */
public class SoundControl extends JPanel implements
ActionListener, ChangeListener{

	private static final long serialVersionUID = 1L;
	static final int VOL_MIN = 0;
	static final int VOL_MAX = 100;
	static final int VOL_INIT = 30;

	private JButton midiMuteButton;
	private JButton muteAllButton;

	private JSlider volumeSlider;

	private boolean midiMute = false;
	private boolean allMute = false;


	/**
	 * Contructor that updates the layout and adds the GUI Buttons.
	 *
	 * MidiMute, MuteAll and VolumeSlider are added to SoundControl.
	 */
	public SoundControl(){
		setLayout(new FlowLayout());
		setOpaque(false);
		add(makeMidiMute());
		add(makeAllMute());
		add(makeVolumeSlider());
	}

	/**
	 * This monitors for button presses from the midiMute and muteAll
	 *
	 * If a Button is pressed it is checked to see if the midiMute or muteAll
	 * button was pressed.
	 *
	 * If the midiMute button was pressed there is a check to see if midi is muted or not.
	 * If midi is not muted SlideRenderer.muteBackground() is called
	 * to mute the midi, if not SlideRenderer.unMuteBackground() is called to
	 * unmute the midi.  The midiMute boolean is updated to keep track of
	 * whether the midi is muted or not.
	 *
	 * If the muteAll button was there is a check to see if all the audio is
	 * muted or not.  If it is muted, SlideRenderer.unMuteAll() is called,
	 * if all audio is not muted SlideRenderer.muteAll() is called.
	 * The boolean variable muteAll is updated to reflect the status of the audio
	 *  - true is all audio is muted,false if it is not.
	 */
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == midiMuteButton){

			// test case
			if(Debug.sound)System.out.println("Mute Button Pressed");

			if(isMidiMute() == false){
				midiMuteButton.setIcon(new ImageIcon("icons/grey/Midi off.png"));
				SlideRenderer.muteBackground();
				setMidiMute(true);
			}else if(isMidiMute() == true){
				midiMuteButton.setIcon(new ImageIcon("icons/grey/Midi on.png"));
				SlideRenderer.unMuteBackground();
				setMidiMute(false);
			}
		} else 	if(e.getSource() == muteAllButton){

			// Test case
			if(Debug.sound)System.out.println("Mute All Button Pressed");
			if(isAllMute() == false){
				muteAllButton.setIcon(new ImageIcon("icons/grey/Sound off.png"));
				SlideRenderer.muteAll();
				setAllMute(true);
			} else if(isAllMute() == true){
				muteAllButton.setIcon(new ImageIcon("icons/grey/Sound on.png"));
				SlideRenderer.unMuteAll();
				setAllMute(false);
			}
		}
	}

	/**
	 * This monitors the Volume Slider for changes in volume.
	 *
	 * If a change is detected the SlideRenderer.alterVolume method is called
	 * This will change the volume of everything in on the slide.
	 */
	public void stateChanged(ChangeEvent e) {

		if(e.getSource() == volumeSlider){

			JSlider source = (JSlider)e.getSource();
			SlideRenderer.alterVolume(source.getValue());

			// test case
			if(Debug.sound)System.out.println("Volume Slider Changed, volume level ="+source.getValue());

		}

	}

	/**
	 * This is the Getter for midiMute
	 * @return midiMute - this is a boolean that keeps track of whether midi
	 * is Muted or not.  If the midi is Muted boolean true is returned if not
	 * false is returned.
	 */
	public boolean isMidiMute() {
		return midiMute;
	}

	/**
	 * Setter for midiMute
	 * @param midiMute - will set midiMute to midiMute
	 */
	public void setMidiMute(boolean midiMute) {
		this.midiMute = midiMute;
	}


	/**
	 * This is a getter for allMute
	 * @return boolean allMute - this is a boolean that keeps trak of whether all audio
	 * is muted or not.  If all audio is muted true is returned if not
	 * false is returned.
	 */
	public boolean isAllMute() {
		return allMute;
	}

	/**
	 * This is the setter for allMute.  It will set allMute to allMute.
	 * @param allMute - the value to update allMute to
	 */
	public void setAllMute(boolean allMute) {
		this.allMute = allMute;
	}
	/**
	 * Makes the midiMute button and adds an action listener to it.
	 *
	 * @return midiMuteButton - jbutton with action listener added
	 */
	private Component makeMidiMute(){

		midiMuteButton = new JButton(new ImageIcon("icons/grey/Midi on.png"));
		midiMuteButton.addActionListener(this);

		return midiMuteButton;
	}

	/**
	 * Make the muteAll Button and adds an action listener to it.
	 *
	 * @return allMuteButton - JButton with action listener added.
	 */
	private Component makeAllMute(){

		muteAllButton = new JButton(new ImageIcon("icons/grey/Sound on.png"));
		muteAllButton.addActionListener(this);

		return muteAllButton;
	}

	/**
	 * Makes the VolumeSlider, with min value of 0, max of 100 and initial volume
	 * of 30 - from the static variables.  A change listener is also added to the
	 * the slider to that when it changes the volume can be updated.
	 * @return volumeSlider - component with change listener added.
	 */
	private Component makeVolumeSlider(){
		volumeSlider = new JSlider(JSlider.HORIZONTAL, VOL_MIN, VOL_MAX, VOL_INIT);
		//TODO: This line causes a paint issue, not had time to fix it!
		//volumeSlider.setBackground(new Color(255,255,255,0));
		volumeSlider.addChangeListener(this);

		return volumeSlider;

	}
}