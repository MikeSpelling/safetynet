/**
 *	Crab Factory Software Solutions
 *
 *  Software:			SafetyNet
 *  Module: 			AnswerBlock
 *  Date Last Edited	17/06/2010
 *
 *  Author: 			Mark Wrightson
 *  Contributers:		Michael Spelling
 *  Testers:
 *
 *  Description:		This module enables quiz slides to define areas for
 *  					answers.
 */


package cfss.org.modules;

import java.awt.*;
import cfss.org.xmlstructure.QuizSlide;
import cfss.org.xmlstructure.XMLPoint;
import cfss.org.xmlstructure.XMLPolygon;
import cfss.org.LocalMouseMonitor.LocalMouseMonitor;
import cfss.org.LocalMouseMonitor.MouseMonitorListener;
import cfss.org.LocalMouseMonitor.ZoneEvents;
import cfss.org.flags.Debug;
import cfss.org.safetynet.*;
import cfss.org.safetynet.gui.Gui;


/**
 * Class that enables areas in slides to be defined as answers. These can then
 * be clicked to display the feedback pane.
 *
 * @author Mark Wrightson
 */
public class AnswerBlock {

	private static final long serialVersionUID = 1L;

	/**
	 * Stores the colour Value for Red.
	 */
	private static final int RED = 16711680;
	/**
	 * Stores the colour Value for Green.
	 */
	private static final int GREEN = 65280;
	/**
	 * Stores the colour Value for Orange.
	 */
	//private static final int ORANGE = 16763955;
	/**
	 * Stores the colour Value for Black.
	 */
	//private static final int BLACK = 0;

	//Mouse monitor for on-click
	private LocalMouseMonitor answerLMM = null;

	private PolygonPlayer answerArea;

	private QuizSlide currentQuizSlide;
	//the index number of the answer zone
	private int index;


	/**
	 * Constructor sets up the answer block.
	 *
	 * @param currentQuizSlide is the QuizSlide to be used.
	 * @param index is the integer used as an idex.
	 */
	public AnswerBlock(QuizSlide currentQuizSlide, int index) {
		this.currentQuizSlide = currentQuizSlide;
		this.index = index;

		int width = (int) (currentQuizSlide.getAnswerAreas().get(index).getWidth());
		int height = (int)(currentQuizSlide.getAnswerAreas().get(index).getHeight());
		XMLPolygon tempPolygon = new XMLPolygon();
		XMLPoint tempAnswerAPoint = new XMLPoint();
		tempAnswerAPoint.setLocation((int)(currentQuizSlide.getAnswerAreas().get(index).getStartPoint().getX()), (int)(currentQuizSlide.getAnswerAreas().get(index).getStartPoint().getY()));

		// Set up the line around answer box A
		tempPolygon.setStartPoint(tempAnswerAPoint);
		tempPolygon.getShapePath().add(new XMLPoint());
		tempPolygon.getShapePath().lastElement().setLocation(0, 0);
		tempPolygon.getShapePath().add(new XMLPoint());
		tempPolygon.getShapePath().lastElement().setLocation(0, height);
		tempPolygon.getShapePath().add(new XMLPoint());
		tempPolygon.getShapePath().lastElement().setLocation(width,height);
		tempPolygon.getShapePath().add(new XMLPoint());
		tempPolygon.getShapePath().lastElement().setLocation(width, 0);
		tempPolygon.setStartTime(0);
		tempPolygon.setEndTime(0);
		tempPolygon.setColor(Color.black);
		tempPolygon.setAlpha(1);
		tempPolygon.setFill(false);
		tempPolygon.setThickness(((int)(2)));

		answerArea = new PolygonPlayer(tempPolygon);
	}

	/**
	 * Display method displays the answer area and creates a mouse monitor
	 * for it.
	 */
	public void display() {
		// Display the area
		answerArea.display();
		// Create mouse area if one is not already present
		if(null == answerLMM) {
			answerLMM = new LocalMouseMonitor(new MouseMonitor(),
					answerArea.getShape().getLocation(),
					answerArea.getShape().getSize());
			answerLMM.setNotifications(true, false, false, true, false, true);
			// Add the zone to the local mouse monitor
			answerLMM.addZone("answer", new Point(0, 0),
					answerArea.getShape().getSize());
		}
	}


	/**
	 * Undisplay hides the answer area and destroys the mouse monitor.
	 */
	public void unDisplay() {
		answerArea.unDisplay();
		lmmDestroy();
	}


	/**
	 * Destroys the LMM if it exists.
	 */
	public void lmmDestroy(){
		if(answerLMM != null)
			answerLMM.destroy();
	}


	/**
	 * Resizes the answer area.
	 */
	public void resize(float scaleFactor) {
		answerArea.resize(scaleFactor);
		if(answerLMM != null) { // Stops null pointer exception
			// Scale the clickable area
			if(answerLMM.retrieveZone("answer") != null) {
				answerLMM.reposition(answerArea.getShape().getLocation().x,
						answerArea.getShape().getLocation().y);
				answerLMM.rescale(answerArea.getShape().getSize().width,
						answerArea.getShape().getSize().height);
			}
		}
	}
	private class MouseMonitor implements MouseMonitorListener {
		private boolean questionAnswered;

		public void dragEventOccurred(String s, int x, int y,
			Boolean complete){}

		public void zoneEventOccurred(String eventZone, int eventType){
			// If within the bounds of the text, go to the link
			if(!eventZone.equals("answer"))return;
			if(eventType==ZoneEvents.ENTER){
					Gui.changeCursor(Cursor.HAND_CURSOR);
			}

			// Print out the name of the zone pressed.
			if(Debug.renderer) System.out.println("Event occurred of type: " +
					eventType+
									" in zone: "+eventZone);

			// If answer box A has been clicked on
			if ((eventType == ZoneEvents.CLICK) && !questionAnswered) {
				// Set the flag
				questionAnswered = true;

				// If answer A is correct colour the answer box A green
				// otherwise colour it red.
				if (currentQuizSlide.getCorrectAnswer() == (index+1))
				{
					answerArea.setLineColor(GREEN);
					answerArea.unDisplay();
					answerArea.display();
				}
				else
				{
					answerArea.setLineColor(RED);
					answerArea.unDisplay();
					answerArea.display();
				}

				// Add the quiz data to the quiz handler
				QuizHandler.addQuizData(currentQuizSlide.getId(), index+1);

				// generate feedback
				SlideRenderer.feedbackGenerator(index+1);

				if (Debug.renderer)
					System.out.println("answerA");
			}
		}
	}


	/**
	 * Returns the answerArea.
	 *
	 * @return answerArea is the PolygonPlayer to return.
	 */
	public PolygonPlayer getAnswerArea() {
		return answerArea;
	}
}