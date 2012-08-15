/************************************************
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		Transport
 *
 *  Author: 		Callum Goddard
 *
 *  Contributers:	Michael Tock (look and feel / button icons)
 *  Testers:		.
 *  Integrators:	.
 *
 *  Description: This contains the transport controls that are added to
 *  the MenuBar.  They control the play/pause of slides in and automated
 *  slide show. And progression to the next slide and the previous slide.
 *
 ***********************************************/


package cfss.org.safetynet.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


import cfss.org.flags.Debug;
import cfss.org.safetynet.*;


/**
 * 	This contains the transport controls that are added to
 *  the MenuBar.  They control the play/pause of slides in and automated
 *  slide show. And progression to the next slide and the previous slide.
 *
 * @author Callum Goddard
 * @author Michael Tock - Look and Feel icons/images
 *
 */
public class Transport extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JButton gotoPreviousSlideButton;
	private JButton playButton;
	private JButton gotoNextSlideButton;

	private static JTextField directNavigationBox;

	private boolean paused = false;

	private String currentSlide = null;


	/**
	 * Class Contructor
	 */
	public Transport(){

		setLayout(new FlowLayout());
		setOpaque(false);
		add(makeGotoPreviousSlideButton());
		add(makePlayButton());

		//*** *** *** buttons have been combined into Play/Pause *** *** ***
		//*** *** *** code not removed as unable to fully test   *** *** ***
		//*** *** *** due to fault in slideRenderer - bug 000068 *** *** ***
		//add(makePauseButton());

		add(makeGotoNextSlideButton());
		add(makeDirectNavigationBox());
	}

	/**
	 * This deals with the actions preformed by the transport buttons.
	 *
	 * When previous slide button is pressed the previous slide
	 * to the the current slide is naviagated to.
	 *
	 * When slideshow button is pressed if the slid show is paused indicated by
	 * isPaused = true - then the slide show is resumed by calling
	 * SlideRenderer.resumeSchedule().
	 *
	 * If the slideshow is unpaused indicated by isPaused = false - the slideshow
	 * is paused by calling SlidRenderer.pauseSchedule().
	 */
	public void actionPerformed(ActionEvent e){

		// when previous slide button is pressed
		// the previous slide to the the current slide is naviagated to.
		if(e.getSource() == gotoPreviousSlideButton)
				Engine.prevSlide();

		// test Case to check id previous button was pressed
		if(Debug.transport)System.out.println("Previous Button");

		// if Slideshow is paused as denoted by isPaused = true -
		// resume the slide show when play button is pressed
		if(e.getSource() == playButton){
			if(Debug.transport)System.out.println();

			if(isPaused() == true) {
				playButton.setIcon(new ImageIcon("icons/grey/Pause.png"));
				SlideRenderer.resumeSchedule();
				setPaused(false);
				if(Debug.transport)System.out.println("Now PLAYING via Play/Pause Button");
			} else{
				playButton.setIcon(new ImageIcon("icons/grey/Play.png"));
				SlideRenderer.pauseSchedule();
				setPaused(true);
				if(Debug.transport)System.out.println("Now PAUSED via Play/Pause Button");
			}

		}


		// if Slideshow is unpaused as denoted by isPaused = false -
		// pause the slide show when the pause button is pressed.
		/*
		if(e.getSource() == pauseButton){
			if(isPaused() == false) {
				SlideRenderer.pauseSchedule();
				setPaused(true);

			}


			// TEST Case if Pause button was pressed
			if(Debug.transport)System.out.println("Pause Button");
		}

		 */

		// If next slide button is pressed the next slide in the
		// the slide show is navigated to.
		if(e.getSource() == gotoNextSlideButton){
					Engine.nextSlide();

					// TEST Case if next Slide Button was Pressed
					if(Debug.transport)System.out.println("Next Slide");

		}

		/*
		 * If the direct Navigate box is updated the input is check to make sure
		 * the input to the directNavtigationBox is an integer.  If it is
		 * it is converted to an integer, as long as the number is less than the
		 * total slides, and not 0 the slide will go to the specified slide.
		 * Else the direct navigation updates to display to show the
		 * current slide ID.
		 */
		if(e.getSource() == directNavigationBox){

			// TEST Case to see if Direct box action listener is triggered
			// correctly
			if(Debug.transport)System.out.println("Direct Box");

			String directBoxNumber = directNavigationBox.getText();
			try{
				Integer.parseInt(directBoxNumber);
				Integer slideToNavigateTo = new Integer(directBoxNumber);

				// TEST Case to check that the number is passed correctly from the
				// Direct navigation box to the gotoSlide function.
				if(Debug.transport)System.out.println("Slide to navigate to =" +
						slideToNavigateTo);

				// TEST case to check giveout Total Slide Number.
				if(Debug.transport)System.out.println("total slides + 1 : " +
						SafetyNet.slideShow.getTree().size());


				if(slideToNavigateTo+1 <= SafetyNet.slideShow.getTree().size()
						&& slideToNavigateTo >= 0){

					// TEST Case to confirm that slideToNavigateTo is passed
					// into Engine.gotoSlide();
					if(Debug.transport)
						System.out.println("Slide to navigate to passed to " +
								"Engine.gotoSlide()");

						Engine.gotoSlide(slideToNavigateTo);
						directNavigationBox.setText(Integer.toString(
								slideToNavigateTo));
				}else{
						currentSlide = Integer.toString(
								Engine.getCurrentSlideID());
						directNavigationBox.setText(currentSlide);
						// TEST Case to confirm that the current slide ID is
						// obtained.
						if(Debug.transport)System.out.println("current slide " +
								"ID ="+currentSlide);
					}
				}catch(NumberFormatException e1){

					//TEST case to check the Exception
					if(Debug.transport)System.out.println("Exception called"+e1);

				currentSlide = Integer.toString(Engine.getCurrentSlideID());
				directNavigationBox.setText(currentSlide);
			}
		}
	}

	/**
	 * Paused Getter
	 * @return paused - boolean
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Sets paused to paused.
	 * @param paused - is a boolean
	 */
	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	/**
	 * Setter to set the direct box value.
	 *
	 * @param currentSlideID - the number
	 */
	public static void setDirectNavigationText(String currentSlideID){

		directNavigationBox.setText(currentSlideID);
	}

	/**
	 * Makes the gotoPreviousSlideButton and adds and action listener.
	 *
	 * @return gotoPreviousSlideButton
	 */
	private Component makeGotoPreviousSlideButton(){

		gotoPreviousSlideButton = new JButton(new ImageIcon(
				"icons/grey/Rewind.png"));
		gotoPreviousSlideButton.setMnemonic('b');
		gotoPreviousSlideButton.addActionListener(this);

		return gotoPreviousSlideButton;
	}

	/**
	 * Makes the playButton and adds and action listener to it
	 *
	 * @return playButton
	 */
	private Component makePlayButton(){

		// Image set to "Pause" as default player state is "play" so first
		// click should pause presentaiton.
		// Fix for bug 0000104.
		playButton = new JButton(new ImageIcon("icons/grey/Pause.png"));
		playButton.setMnemonic('n');
		playButton.addActionListener(this);

		return playButton;
	}

	/**
	 * Makes the Pause button and adds and action listener to it
	 *
	 * @return pauseButton
	 */
	/*private Component makePauseButton(){

		pauseButton = new JButton(new ImageIcon("icons/grey/Pause.png"));
		pauseButton.addActionListener(this);

		return pauseButton;
	}*/

	/**
	 * Makes the next slide button and adds an action listener to it
	 * @return gotoNextSlideButton
	 */
	private Component makeGotoNextSlideButton(){
		gotoNextSlideButton = new JButton(new ImageIcon("icons/grey/Fast " +
				"Forward.png"));
		gotoNextSlideButton.setMnemonic('m');
		gotoNextSlideButton.addActionListener(this);

		return gotoNextSlideButton;
	}

	/**
	 * Makes the Direct Navigation box and adds and action listener to it.
	 *
	 * @return directNavigationBox
	 */
	private Component makeDirectNavigationBox(){
		directNavigationBox = new JTextField(2);
		directNavigationBox.addActionListener(this);
		directNavigationBox.setFont(new Font("Sans-serif", Font.PLAIN, 24));

		currentSlide = Integer.toString(Engine.getCurrentSlideID());
		directNavigationBox.setText(currentSlide);
		return directNavigationBox;
	}
}