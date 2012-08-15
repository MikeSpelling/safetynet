/************************************************
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		Engine
 *
 *  Author: 		Philip Day, Harry Taylor
 *
 *  Contributers:	Michael Spelling
 *  Testers:		.
 *
 *  Description:	Determines the nature of the current slide (quiz or regular)
 *  				from the data structure and makes the appropriate calls to
 *  				SlideRenderer to display it.
 *
 *  				Responsible for determining the next or previous slide.
 *  				Keeps track of slides previously visited to allow a 'back'
 *  				navigation function. Allows direct navigation to a
 *  				specified slide ID.
 *
 ***********************************************/

package cfss.org.safetynet;

import cfss.org.flags.Debug;
import cfss.org.safetynet.gui.Gui;
import cfss.org.safetynet.gui.Transport;
import cfss.org.xmlstructure.*;

/**
 * 	Determines the nature of the current slide (quiz or regular) from the data
 * 	structure and makes the appropriate calls to SlideRenderer to display it.
 *
 * 	Responsible for determining the next or previous slide.
 * 	Keeps track of slides previously visited to allow a 'back' navigation function.
 * 	Allows direct navigation to a specified slide ID.
 *
 *  @author Philip Day
 *  @author Harry Taylor
 *  @author Contributer: Michael Spelling
 *
 */
public class Engine {

	private static Slide currentSlide = null;  // Data structure contents of a 'normal' slide to be passed to the renderer.
	private static int currentSlideID = 1;  // ID of the slide to be rendered.  Always starts as 1.
	private static QuizSlide currentQuizSlide = null;  // Data structure contents of a quiz slide to be passed to the renderer.
	private static SlideRenderer slideRenderer;  // Used to refer to the slideRenderer.

	/**
	 * Instantiates the SlideRenderer and the history stack.
	 */
	public Engine(){
		slideRenderer = new SlideRenderer(); // needs to be instatianted due to local mouse monitor
	}


	/**
	 * Calls the SlideRenderer for the currentSlideID
	 */
	public static void playSlideshow(){

		SafetyNet.updateGui();
		Transport.setDirectNavigationText(Integer.toString(currentSlideID));

		if(Debug.engine) System.out.println("Engine -> playSlideshow -> Current Slide being viewed:" + currentSlideID);
		// If the current slide is a normal Slide, then call the renderer as usual.
		if (SafetyNet.slideShow.getTree().elementAt(currentSlideID) instanceof Slide && !(SafetyNet.slideShow.getTree().elementAt(currentSlideID) instanceof QuizSlide)) {
			setCurrentSlide((Slide) SafetyNet.slideShow.getTree().get(currentSlideID));
			setCurrentQuizSlide(null);
			Gui.getSlidePanel().removeAll();
			SlideRenderer.renderSlide(currentSlide);
		}
		// If the current slide is a quiz, then render it as a quiz.
		else if (SafetyNet.slideShow.getTree().elementAt(currentSlideID) instanceof QuizSlide) {
			setCurrentQuizSlide((QuizSlide) SafetyNet.slideShow.getTree().elementAt(currentSlideID));
			setCurrentSlide(null);
			Gui.getSlidePanel().removeAll();
			slideRenderer.renderQuizSlide(currentQuizSlide); // Can't be accessed in the static way due to local mouse monitor
		}
		// Only the 2 above cases should be possible, so print an error otherwise.
		else{
			if (Debug.engine) System.out.println("Engine -> playSlideshow -> data structure did not return a valid type.");
		}
	}


	/**
	 * Sets the next slide ID to currentSlideID and calls playSlideshow method.
	 */
	public static void nextSlide(){

	// Test whether there is a slide show
	if (SafetyNet.slideShow != null){
			// Retrieve the next slide ID if one is specified.
			if ((0 < SafetyNet.slideShow.getTree().elementAt(currentSlideID).getNext())
					&& (SafetyNet.slideShow.getTree().size() >= SafetyNet.slideShow.getTree().elementAt(currentSlideID).getNext() + 1)) {
				// Add the next slide to the history.
				currentSlideID = SafetyNet.slideShow.getTree().elementAt(currentSlideID).getNext();
				if (Debug.engine) System.out.println("ID entered onto stack : " + currentSlideID);

				// Update the section navigation bar with the new current slide.
				Gui.getSectionNav().updateCurrent(currentSlideID);

				// Render the next slide.
				playSlideshow();
			}
			else{
				if (Debug.engine) System.out.println("Engine -> nextSlide -> No next slide is specified in the data structure.");
			}
		}
	}


	/**
	 * Renders previous slides by decrementing the currentSlideID
	 */
	public static void prevSlide(){

		// Get the last one and make it the current slide.
		if (1 < currentSlideID){
			currentSlideID = currentSlideID - 1;
			if (Debug.engine) System.out.println("ID retrieved from stack : " + currentSlideID);

			// Update the section navigation bar with the new current slide.
			Gui.getSectionNav().updateCurrent(currentSlideID);

			// Render the last slide.
			playSlideshow();
		}
		else{
			if (Debug.engine) System.out.println("Engine -> prevSlide -> There is no previous slide!");
		}
	}


	/**
	 * If requestedID is valid, then it becomes currentSlideID and playSlideshow is called.
	 * @param requestedID
	 */
	public static void gotoSlide(int requestedID){

		if(requestedID==currentSlideID) {
			if (Debug.engine) System.out.println("Engine -> " +
			"gotoSlide -> requested slide ID = currentSlideID.  Request Ignored");
			return;
		}
		// Nested ifs make sure to avoid null pointer exceptions by trying to
		// access uninstantiated data
		if (SafetyNet.slideShow != null) {
			if(SafetyNet.slideShow.getTree() != null) {
				// If the requested ID is valid, then render it.
				if ((0 < requestedID) && (SafetyNet.slideShow.getTree().size() >= requestedID + 1)){
					currentSlideID = requestedID;
					// Update the section navigation bar with the new current slide.
					Gui.getSectionNav().updateCurrent(currentSlideID);

					// Render the next slide.
					playSlideshow();

				}
				else
					if (Debug.engine) System.out.println("Engine -> " +
							"gotoSlide -> requested slide ID is not valid");
			}
			else
				if (Debug.engine) System.out.println("Engine -> " +
						"gotoSlide -> requested slide ID is not valid");
		}
		else
			if (Debug.engine) System.out.println("Engine -> " +
					"gotoSlide -> requested slide ID is not valid");
	}


	// GETTERS AND SETTERS //

	/**
	 * Sets the reference for currentSlide
	 */
	public static void setCurrentSlide(Slide currentSlide) {
		Engine.currentSlide = currentSlide;
	}

	/**
	 * Returns the reference of currentSlide.
	 *
	 * @return currentSlide is the Slide to return.
	 */
	public static Slide getCurrentSlide() {
		return currentSlide;
	}

	/**
	 * Sets the reference for currentQuizSlide
	 * @param currentQuizSlide
	 */
	public static void setCurrentQuizSlide(QuizSlide currentQuizSlide) {
		Engine.currentQuizSlide = currentQuizSlide;
	}

	/**
	 * Returns the reference of currentQuizSlide.
	 *
	 * @return currentQuizSlide is the QuizSlide to return.
	 */
	public static QuizSlide getCurrentQuizSlide() {
		return currentQuizSlide;
	}

	/**
	 * Returns the value of currentSlideID.
	 *
	 * @return currentSlideID is the integer to return.
	 */
	public static int getCurrentSlideID() {
		return currentSlideID;
	}


	/**
	 * Sets the value of currentSlideID
	 * @param currentSlideID
	 */
	public static void setCurrentSlideID(int currentSlideID) {
		Engine.currentSlideID = currentSlideID;
	}
}