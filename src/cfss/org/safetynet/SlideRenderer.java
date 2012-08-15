package cfss.org.safetynet;

import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import cfss.org.xmlstructure.*;
import cfss.org.flags.*;
import cfss.org.modules.*;
import cfss.org.LocalMouseMonitor.*;
import cfss.org.safetynet.gui.*;

/************************************************
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		SlideRenderer
 *
 *  Author: 		Philip Day
 *
 *  Contributers:	.
 *  Testers:		.
 *
 *  Description:	Creates the schedule for the slide
 *  				and then renders the slide calling the display and
 *  				hide methods for each entity at the correct time.
 *  				It also renders and handles the quizSlide.
 *
 ***********************************************/

public class SlideRenderer  implements KeyListener
{
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
	/**
	 * Stores the entityPlayers for the current Slide or Quiz Slide
	 */
	private static Vector <Entity> entityPlayers = null;
		/**
	 * Flag for the play/pause button. It is set to true when the slide is playing
	 */
	@SuppressWarnings("unused")
	private static boolean playPause;
	/**
	 * The slideTimer is used to create the schedule for the slide. All entities are displayed and hidden with this timer.
	 */
	private static Timer slideTimer;
	/**
	 * Stores the time for the last entity to be displayed.
	 */
	private static long maxEndTime = 0;
	/**
	 * Stores whether the slide is static. It is static if one entity has an end time of 0.
	 */
	private static boolean isStatic = false;
	/**
	 * Local mouse monitor used to detect user clicks on the quiz answer boxes.
	 */
	private static LocalMouseMonitor questionMouseMonitor = null;
	/**
	 * A polygon player which draws a box around the answer box A
	 */
	private static AnswerBlock answerBoxA = null;
	/**
	 * A polygon player which draws a box around the answer box B
	 */
	private static AnswerBlock answerBoxB = null;
	/**
	 * A polygon player which draws a box around the answer box C
	 */
	private static AnswerBlock answerBoxC = null;
	/**
	 * A polygon player which draws a box around the answer box D
	 */
	private static AnswerBlock answerBoxD = null;
	/**
	 * It is used to lock the answers boxes once the user has clicked on one.
	 */
	@SuppressWarnings("unused")
	private static Boolean questionAnswered = false;
	/**
	 * Contains the users score
	 */
	private static Text score = new Text();
	/**
	 * Contains whether the user got the question correct
	 */
	private static Text correctness = new Text();
	/**
	 * Displays the users score.
	 */
	private static TextPlayer scoreTextPlayer;
	/**
	 * Contains whether the user got the question correct.
	 */
	private static TextPlayer  correctnessTextPlayer;
	/**
	 * This stores the last entity to be displayed or hidden. This is used in the resume slide show.
	 */
	private final static StopTime lastestTime = new StopTime();
	/**
	 * Stores the current quiz slide if it is a quiz slide.
	 */
	private static QuizSlide currentQuizSlide = null;

	/**
	 * Stores the current slide
	 */
	private static Slide currentSlide = null;

	/**
	 * Constructor
	 */
	public SlideRenderer(){

		// Adds the the key listener for the hot keys to work for navigation
		Gui.getMainFrame().addKeyListener(this);
		// sets the focus to the main frame.
		Gui.getMainFrame().setFocusable(true);
	}


	/**
	 * The current slide is passed. The method the slide entities converts them
	 * to entityPlayers and then adds to the entityPlayers vector.
	 * It then creates the schedule for that slide.
	 *
	 * @param aCurrentSlide is the Slide to render.
	 */
	public static void renderSlide(Slide aCurrentSlide){

		currentSlide = aCurrentSlide;

		// Cancel the schedule from previous slide.
		if (null != slideTimer )
			slideTimer.cancel();

		// Initially clears the whole slide
		if (null != entityPlayers)
		{
		hideAll();
		clearEntities();
		}

		// reset flag
		isStatic = false;

		currentQuizSlide = null;

		// Reset the quiz components
		correctness = null;
		scoreTextPlayer = null;
		answerBoxA = null;
		answerBoxB = null;
		answerBoxC = null;
		answerBoxD = null;

		//Set background to slide
		Gui.getBackgroundPanel().setBackground(currentSlide.getBkCol());

		//Used to make code simple
		SlideEntity currentSlideEntity;

		// Check to see if the slide contains any SlideEntities
		entityPlayers = new Vector<Entity>();
		if (currentSlide.getEntities().size() != 0)
		{
			// get first slide entity
			currentSlideEntity = currentSlide.getEntities().get(0);

			// Checks whether the slide has content on it
			if ((null != currentSlide) && (null != currentSlideEntity));
			{
				// loops through the slideEntities converting them to
				// entityPlayers.
				int i;
				for(i = 0; i < currentSlide.getEntities().size(); i++)
				{
					currentSlideEntity = currentSlide.getEntities().get(i);
					currentSlideEntity.setZOrder(i+1);


					// Creates a Text Player if it is Text
					if (currentSlideEntity instanceof Text)
						{

						// Instantiate the Text Player object
						TextPlayer currentTextPlayer = new TextPlayer(
								(Text) currentSlideEntity);

						// Sets the time instance variables in Entity
						currentTextPlayer.setStartTime(
								currentSlideEntity.getStartTime());
						currentTextPlayer.setEndTime(
								currentSlideEntity.getEndTime());

						// Adds the TextPlayer to list of Entity Players
						entityPlayers.add(currentTextPlayer);
						}

					// Creates an Image Player if it is an Image
					else if (currentSlideEntity instanceof XMLImage)
						{

						// Instantiate the Image Player object
						ImagePlayer currentImagePlayer = new ImagePlayer(
								(XMLImage) currentSlideEntity);

						// Sets the time instance variables in Entity
						currentImagePlayer.setStartTime(
								currentSlideEntity.getStartTime());
						currentImagePlayer.setEndTime(
								currentSlideEntity.getEndTime());

						// Adds the ImagePlayer to list of Entity Players
						entityPlayers.add(currentImagePlayer);
						}

					// Creates a Video Player if it is a Video
					else if (currentSlideEntity instanceof Video)
					{

					// Instantiate the Video Player object
					VideoPlayer currentVideoPlayer = new VideoPlayer(
							(Video) currentSlideEntity);

					// Sets the time instance variables in Entity
					currentVideoPlayer.setStartTime(
							currentSlideEntity.getStartTime());
					currentVideoPlayer.setEndTime(
							currentSlideEntity.getEndTime());

					// Adds the AudioPlayer to list of Entity Players
					entityPlayers.add(currentVideoPlayer);
					}

					// Creates a Audio Player if it is an Audio
					else if (currentSlideEntity instanceof Audio)
					{

					// Instantiate the Audio Player object
					AudioPlayer currentAudioPlayer = new AudioPlayer(
							(Audio) currentSlideEntity);

					// Sets the time instance variables in Entity
					currentAudioPlayer.setStartTime(
							currentSlideEntity.getStartTime());
					currentAudioPlayer.setEndTime(
							currentSlideEntity.getEndTime());

					// Adds the Audio Player to list of Entity Players
					entityPlayers.add(currentAudioPlayer);
					}

					// Creates a Graphics Player if it is a Graphic
					else if (currentSlideEntity instanceof XMLPolygon)
					{

					// Instantiate the Graphics Player object
					PolygonPlayer currentPolygonPlayer = new PolygonPlayer(
							(XMLPolygon) currentSlideEntity);

					// Sets the time instance variables in Entity
					currentPolygonPlayer.setStartTime(
							currentSlideEntity.getStartTime());
					currentPolygonPlayer.setEndTime(
							currentSlideEntity.getEndTime());

					// Adds the GraphicsPlayer to list of Entity Players
					entityPlayers.add(currentPolygonPlayer);
					}

					else if (currentSlideEntity instanceof Circle)
					{

					// Instantiate the Graphics Player object
					CirclePlayer currentCirclePlayer = new CirclePlayer(
							(Circle) currentSlideEntity);

					// Sets the time instance variables in Entity
					currentCirclePlayer.setStartTime(
							currentSlideEntity.getStartTime());
					currentCirclePlayer.setEndTime(
							currentSlideEntity.getEndTime());

					// Adds the GraphicsPlayer to list of Entity Players
					entityPlayers.add(currentCirclePlayer);
					}

					// Creates a Midi Player if it is Midi

					else if (currentSlideEntity instanceof Midi)
					{

						// Instantiate the Graphics Player object
						MidiPlayer currentMidiPlayer = new MidiPlayer(
								(Midi) currentSlideEntity);

						// Sets the time instance variables in Entity
						currentMidiPlayer.setStartTime(
								currentSlideEntity.getStartTime());
						currentMidiPlayer.setEndTime(
								currentSlideEntity.getEndTime());

						// Adds the GraphicsPlayer to list of Entity Players
						entityPlayers.add(currentMidiPlayer);
					}


					// Handles if a Slide Entity is a ScrollPane - Only
					// Images and Text Can be displayed at the moment
					else if (currentSlideEntity instanceof ScrollPane)
					{

						// Casks the SlideEntity to a Scroll Pane
						ScrollPane currentScrollPane =
							(ScrollPane) currentSlideEntity;

						// Instantiate the ScrollPane Player object
						ScrollPanePlayer currentScrollPanePlayer =
							new ScrollPanePlayer((ScrollPane) currentSlideEntity);

						for (i=0;i<currentScrollPane.getEntities().size();i++)
						{
							// Stores the current slide Entity to be added to
							// the scroll pane
							SlideEntity currentScrollPaneEntity =
								currentScrollPane.getEntities().get(i);


							// Creates a Text Player if it is Text
							if (currentScrollPaneEntity instanceof Text)
								{

								// Instantiate the Text Player object passing
								// the Reference to Scroll Pane
								TextPlayer currentTextPlayer = new TextPlayer(
										(Text) currentScrollPaneEntity,
										currentScrollPanePlayer);


								// Adds the TextPlayer to list of Entity Players
								// with the Scroll Pane Player
								currentScrollPanePlayer.getEntityVector().add(
										(Entity) currentTextPlayer);
								}

							// Creates an Image Player if it is an Image
							else if (currentScrollPaneEntity instanceof XMLImage)
								{

								// Instantiate the Image Player object
								ImagePlayer currentImagePlayer = new ImagePlayer((XMLImage) currentScrollPaneEntity, currentScrollPanePlayer);


								// Adds the ImagePlayer to list of Entity Players
								entityPlayers.add((Entity)currentImagePlayer);
								}
						}

						// Sets the time instance variables in Entity
						currentScrollPanePlayer.setStartTime(
								currentSlideEntity.getStartTime());
						currentScrollPanePlayer.setEndTime(
								currentSlideEntity.getEndTime());

						// Adds the GraphicsPlayer to list of Entity Players
						entityPlayers.add(currentScrollPanePlayer);
					}
				}

				// Start the timer for the slide
				slideTimer = new Timer();
				// Reset the lastestTime for the slide
				lastestTime.setTimeStopped(0);
				// Reset the maxEndTime for the slide
				maxEndTime = 0;

				// Prints the number on entitiesPlayers for the slide
				if(Debug.renderer) System.out.println(entityPlayers.size());

				// Loops through the entityPlayers vector add each one to the
				// schedule
				for(i=0;i < entityPlayers.size(); i++)
				{
					//entityPlayers.get(i).display();

					if(Debug.renderer){
						if (entityPlayers.get(i) instanceof AudioPlayer)
							System.out.println("AudioPlayer Displayed - " +
									"Entity No:" + i);

						if (entityPlayers.get(i) instanceof TextPlayer)
						System.out.println("TextPlayer Displayed - Entity No:" +
								i);

						if (entityPlayers.get(i) instanceof ImagePlayer)
						System.out.println("ImagePlayer Displayed - Entity No:"
								+ i);

						if (entityPlayers.get(i) instanceof VideoPlayer)
						System.out.println("VideoPlayer Displayed - Entity No:"
								+ i);

						if (entityPlayers.get(i) instanceof PolygonPlayer)
							System.out.println("PolygonPlayer Displayed - " +
									"Entity No:" + i);

						if (entityPlayers.get(i) instanceof CirclePlayer)
								System.out.println("CirclePlayer Displayed - " +
										"Entity No:" + i);

						if (entityPlayers.get(i) instanceof MidiPlayer)
						System.out.println("MidiPlayer Displayed - Entity No:"
								+ i);

						if (entityPlayers.get(i) instanceof ScrollPanePlayer)
							System.out.println("ScrollPanePlayer Displayed - " +
									"Entity No:" + i);
					}

					// Stores the longest End time in maxEndTime
					if (maxEndTime < entityPlayers.get(i).getEndTime()){
						maxEndTime = entityPlayers.get(i).getEndTime();
					}

					// Needs to be final for the the scheduling method to work
					final Entity tempEntity = entityPlayers.get(i);

					// Prints out the start time and endtimes for each entity
					// Player
					if(Debug.renderer){
						System.out.println("start time:" +
								entityPlayers.get(i).getStartTime());
						System.out.println("end time:" +
								entityPlayers.get(i).getEndTime());
					}

					// Calls the display method immediately if the start time
					// is 0
					if (tempEntity.getStartTime() == 0)
					{
						tempEntity.display();
					}

					// Otherwise the display method is tasked to the schedule
					else {
						slideTimer.schedule(new TimerTask(){

					      public void run(){
					      tempEntity.setIsActive(true);
					   	  tempEntity.display();
					   	  lastestTime.setTimeStopped(tempEntity.getStartTime());
					   	 }
					     },(long)tempEntity.getStartTime());
					}

					// Test to see if the entity is static
					if (0 == tempEntity.getEndTime())
					{
						// set the static flag
						isStatic = true;
					}
					// This adds the hide tasks to the schedule
					else {
					slideTimer.schedule(new TimerTask(){
					      public void run(){
					    	  tempEntity.setIsActive(false);
					    	  tempEntity.unDisplay();
					    	  lastestTime.setTimeStopped(tempEntity.getStartTime());
					       }
					     },(long)tempEntity.getEndTime());
					 }
					}
				}
			// Once the slider has reached the end of the schedule if slide
			// isn't static schedule the goto next slide.
			if(false == isStatic)
				{
				slideTimer.schedule(new TimerTask(){
				   public void run(){
					   hideAll(); // This clears all the entities from memory
					   slideTimer.cancel();
					   Engine.nextSlide();
				       }
			     },maxEndTime+500);
				}
			// If the slide is static cancel the timer
			else if(true == isStatic)
			{

			slideTimer.schedule(new TimerTask(){
			   public void run(){
				   slideTimer.cancel();
			       }
		     },maxEndTime+500);
			}
		}
	}


	/**
	 * Pause Schedule - pauses the schedule and loops through all the media
	 * entities calling there pause method.
	 */
	public static void pauseSchedule(){
		// used for loops
		int i;

		// Used to access the specific methods associated with a video player
		VideoPlayer currentVideoPlayer;
		// Used to access the specific methods associated with a audio player
		AudioPlayer currentAudioPlayer;
		// Used to access the specific methods associated with a midi player
		MidiPlayer currentMidiPlayer;

		// Test to see if the schedule is running
		if (slideTimer != null)
		{
			// Cancel Slide Schedule;
			slideTimer.cancel();

			// Reset slideTimer
			slideTimer = null;

			//Set the playPause flag
			playPause = false;

			// Check to see if the slide has any content.
			if ( null!= entityPlayers )
			{
				// Pause All Video,Midi,Audio Playing
				for(i =0;i <entityPlayers.size(); i++)
				{
					// Only pause media if they are active
					if (entityPlayers.get(i).getIsActive())
					{
						if (entityPlayers.get(i) instanceof VideoPlayer)
						{
							currentVideoPlayer = (VideoPlayer) entityPlayers.get(i);
							currentVideoPlayer.pauseMedia();
						}

						if (entityPlayers.get(i) instanceof AudioPlayer)
						{
							currentAudioPlayer = (AudioPlayer) entityPlayers.get(i);
							currentAudioPlayer.pauseMedia();
						}

						if (entityPlayers.get(i) instanceof MidiPlayer)
						{
							currentMidiPlayer = (MidiPlayer) entityPlayers.get(i);
							currentMidiPlayer.pause();
						}
					}
				}
			}
		}
	}

	/**
	 * Resume Schedule - resumes the schedule and loops through all the
	 * entitiesPlayers and reschedules all the display and undisplay methods.
	 */
	public static void resumeSchedule(){
	// Set the playPause flag to true
	playPause = true;

	// Used for loops
	int i;

	// Timer started again.
	slideTimer = new Timer();

	// Set the last entities to be display or undisplayed
	long delay = lastestTime.getTimeStopped();

	// Check to see if the slide has got content.
		if (null  != entityPlayers)
	{
		for(i=0;i < entityPlayers.size();i++)
			{
				// Needs to be final for the schedule to work
				final Entity tempEntity = entityPlayers.get(i);

				// This adds the display tasks to the schedule
				if ((entityPlayers.get(i).getStartTime()- delay > 0))
				{
					slideTimer.schedule(new TimerTask(){
					      public void run(){
						      tempEntity.setIsActive(true);
						      tempEntity.display();
						   	  lastestTime.setTimeStopped(tempEntity.getStartTime());
						    }
					     },(long)(entityPlayers.get(i).getStartTime() - delay));
				}

				// Test if it is static
				if (0 == tempEntity.getEndTime() )
				{
					isStatic = true;
				}

				// If the entity isn't fina,l it adds the hide tasks to the schedule
				else if (0<(entityPlayers.get(i).getEndTime()- delay))
				{
					slideTimer.schedule(new TimerTask(){
					      public void run(){
					      tempEntity.setIsActive(false);
					      tempEntity.unDisplay();
					   	  lastestTime.setTimeStopped(tempEntity.getEndTime());
					      }
					     },(long) (entityPlayers.get(i).getEndTime()- delay));
				}
			}

		// When there are no more entities to be displayed schedule the goto
		// next slide
			if( false == isStatic)
			{
			slideTimer.schedule(new TimerTask(){
			   public void run(){

				   // cancels the timer
				   slideTimer.cancel();

				   // clears slide
				   hideAll();
				   clearEntities();

				   // goto to the next slide
				   Engine.nextSlide();
			       }
		     },maxEndTime+500);
			}

			else if(true == isStatic)
			{

			slideTimer.schedule(new TimerTask(){
			   public void run(){
				     slideTimer.cancel();
			       }
		     },maxEndTime+500);
			}
	}
}

	/**
	 * Loops through all the midi players calling their mute methods.
	 */
	public static void muteBackground(){
		// Used for loops
		int i;
		// Used so that the methods associated with a midi player can be accessed.
		MidiPlayer currentMidiPlayer;

		// Checks the slide for content
		if (null != entityPlayers )
		{
			for(i =0;i <entityPlayers.size(); i++)
			{
				// test whether the midi players are active.
				if (entityPlayers.get(i).getIsActive())
				{
					if (entityPlayers.get(i) instanceof MidiPlayer)
					{
						// Mute player
						currentMidiPlayer = (MidiPlayer) entityPlayers.get(i);
						currentMidiPlayer.muteMedia();
					}
				}
			}
		}
	}

	/**
	 * Loops through all the midi players calling their unMute methods.
	 *
	 */
	public static void unMuteBackground(){

		// Used for loops
		int i;

		// Used so that the methods associated with a midi player can be accessed.
		MidiPlayer currentMidiPlayer;

		//Checks the slide for slide for content
		if (null != entityPlayers)
		{

			for(i =0;i <entityPlayers.size(); i++)
				{
				// test whether the players are active.
				if (entityPlayers.get(i).getIsActive()) {
					if (entityPlayers.get(i) instanceof MidiPlayer) {
						// un mute player
						currentMidiPlayer = (MidiPlayer) entityPlayers.get(i);
						currentMidiPlayer.unMuteMedia();
					}
				}
			}
		}
	}

	/**
	 * Loops through all the entity players calling their individual resize
	 * methods.
	 * @param aScaleFactor
	 */
	public static void resizeAll(float aScaleFactor){

		// Check whether the current slide is a quiz slide
		if (null != currentQuizSlide){

			if (correctnessTextPlayer != null)
			correctnessTextPlayer.resize(aScaleFactor);

			// Resize quiz elements
			scoreTextPlayer.resize(aScaleFactor);
			answerBoxA.resize(aScaleFactor);
			answerBoxB.resize(aScaleFactor);
			answerBoxC.resize(aScaleFactor);
			answerBoxD.resize(aScaleFactor);

			if (Debug.renderer)
			System.out.println(questionMouseMonitor.getAlignmentX());

			// Resize the local mouse monitor for those answer boxes.
			//questionMouseMonitor.applyScaleFactor((double)aScaleFactor,true,true);
		}


		// Check the slide for content.
		if (null != entityPlayers) {
			// Used for loops
			int i;
			// Calls the resize method in all the entities.
			for(i =0;i <entityPlayers.size(); i++) {
				// test whether the players are active.
				if (entityPlayers.get(i).getIsActive() == true)
					entityPlayers.get(i).resize(aScaleFactor);
			}
		}
	}

	/**
	 * A quiz Slide is passed to it. It converts the slide entities and
	 * feedback entities into there associated entity players.
	 * It also sets up the local mouse monitor for each answer zone.
	 */
	public void renderQuizSlide(QuizSlide aCurrentQuizSlide){
		int currZorder = 0;
		// Cancel the schedule from previous slide.
		if (null != slideTimer )
			slideTimer.cancel();

		// Stores the current quiz slide
		currentQuizSlide = aCurrentQuizSlide;

		// Checks whether the question has been answered before
		if ( -1 != currentQuizSlide.getGivenAnswer())
			questionAnswered = true;

		// Clears the answer vector.
		Vector<Integer> answer = new Vector<Integer>();

		// Gets the correct answer for this question
		answer.add(aCurrentQuizSlide.getId());

		//Set background to slide
		Gui.getBackgroundPanel().setBackground(currentQuizSlide.getBkCol());

		// Clears the entity player vector
			if (null != entityPlayers )
			{
				hideAll();
				clearEntities();
			}

		// Displays the number of correctly answered questions
			// Get score from the quiz Handler
			score.setData("Score \n   " + QuizHandler.getAnswersCorrect() +  " \n ----- \n   " + QuizHandler.getQuestionsAnswered() );

			// Get the information from the datastructure.
			score.setFontName(currentQuizSlide.getScoreFont());
			score.setFontSize(currentQuizSlide.getScoreSize());
			score.setStartPoint(currentQuizSlide.getScoreStart());
			score.setFontColor(currentQuizSlide.getScoreColour());
			score.setBackgroundColor(currentQuizSlide.getScoreBkCol());
			score.setFontAlpha(currentQuizSlide.getScoreAlpha());
			score.setBackgroundAlpha(currentQuizSlide.getScoreBkAlpha());
			score.setOnClick(-1);
			score.setZOrder(currentQuizSlide.getEntities().size() + currentQuizSlide.getFeedback().size() + 2);

			// Create the textPlayer for score
			scoreTextPlayer = new TextPlayer(score);
			// Display score on screen
			scoreTextPlayer.display();

			// Initialise the entityPlayers vector
			entityPlayers = new Vector<Entity>();

			// Loop through the Quiz Slide displaying the entities
			int i;
			if (null != currentQuizSlide) // Stops the method from breaking
			{

				for(i=0;i<currentQuizSlide.getEntities().size();i++)
				{

				SlideEntity currentSlideEntity = currentQuizSlide.getEntities().get(i);
				currentSlideEntity.setZOrder(i+1);

				// Creates a Text Player if it is Text
				if (currentSlideEntity instanceof Text)
					{

					// Instantiate the Text Player object
					TextPlayer currentTextPlayer = new TextPlayer((Text) currentSlideEntity);

					// Sets the time instance variables in Entity
					currentTextPlayer.setStartTime(currentSlideEntity.getStartTime());
					currentTextPlayer.setEndTime(currentSlideEntity.getEndTime());

					// Adds the TextPlayer to list of Entity Players
					entityPlayers.add(currentTextPlayer);
					}

				// Creates an Image Player if it is an Image
				else if (currentSlideEntity instanceof XMLImage)
					{

					// Instantiate the Image Player object
					ImagePlayer currentImagePlayer = new ImagePlayer((XMLImage) currentSlideEntity);

					// Sets the time instance variables in Entity
					currentImagePlayer.setStartTime(currentSlideEntity.getStartTime());
					currentImagePlayer.setEndTime(currentSlideEntity.getEndTime());

					// Adds the ImagePlayer to list of Entity Players
					entityPlayers.add(currentImagePlayer);
					}

				// Creates a Video Player if it is a Video
				else if (currentSlideEntity instanceof Video)
				{

				// Instantiate the Video Player object
				VideoPlayer currentVideoPlayer = new VideoPlayer((Video) currentSlideEntity);

				// Sets the time instance variables in Entity
				currentVideoPlayer.setStartTime(currentSlideEntity.getStartTime());
				currentVideoPlayer.setEndTime(currentSlideEntity.getEndTime());

				// Adds the AudioPlayer to list of Entity Players
				entityPlayers.add(currentVideoPlayer);
				}

				// Creates a Audio Player if it is an Audio
				else if (currentSlideEntity instanceof Audio)
				{

				// Instantiate the Audio Player object
				AudioPlayer currentAudioPlayer = new AudioPlayer((Audio) currentSlideEntity);

				// Sets the time instance variables in Entity
				currentAudioPlayer.setStartTime(currentSlideEntity.getStartTime());
				currentAudioPlayer.setEndTime(currentSlideEntity.getEndTime());

				// Adds the Audio Player to list of Entity Players
				entityPlayers.add(currentAudioPlayer);
				}

				// Creates a Graphics Player if it is a Graphic
				else if (currentSlideEntity instanceof XMLPolygon)
				{

				// Instantiate the Graphics Player object
				PolygonPlayer currentPolygonPlayer = new PolygonPlayer((XMLPolygon) currentSlideEntity);

				// Sets the time instance variables in Entity
				currentPolygonPlayer.setStartTime(currentSlideEntity.getStartTime());
				currentPolygonPlayer.setEndTime(currentSlideEntity.getEndTime());

				// Adds the GraphicsPlayer to list of Entity Players
				entityPlayers.add(currentPolygonPlayer);
				}

				else if (currentSlideEntity instanceof Circle)
				{

				// Instantiate the Graphics Player object
				CirclePlayer currentCirclePlayer = new CirclePlayer((Circle) currentSlideEntity);

				// Sets the time instance variables in Entity
				currentCirclePlayer.setStartTime(currentSlideEntity.getStartTime());
				currentCirclePlayer.setEndTime(currentSlideEntity.getEndTime());

				// Adds the GraphicsPlayer to list of Entity Players
				entityPlayers.add(currentCirclePlayer);
				}

				// Creates a Midi Player if it is Midi

				else if (currentSlideEntity instanceof Midi)
				{

					// Instantiate the Graphics Player object
					MidiPlayer currentMidiPlayer = new MidiPlayer((Midi) currentSlideEntity);

					// Sets the time instance variables in Entity
					currentMidiPlayer.setStartTime(currentSlideEntity.getStartTime());
					currentMidiPlayer.setEndTime(currentSlideEntity.getEndTime());

					// Adds the GraphicsPlayer to list of Entity Players
					entityPlayers.add(currentMidiPlayer);
				}


				// Handles if a Slide Entity is a ScrollPane - Only Images
				// and Text Can be displayed at the moment
				else if (currentSlideEntity instanceof ScrollPane) {

					// Casks the SlideEntity to a Scroll Pane
					ScrollPane currentScrollPane =
						(ScrollPane) currentSlideEntity;

					// Instantiate the ScrollPane Player object
					ScrollPanePlayer currentScrollPanePlayer =
						new ScrollPanePlayer((ScrollPane) currentSlideEntity);

					for (i=0;i<currentScrollPane.getEntities().size();i++) {
						// Stores the current slide Entity to be added to the
						// scroll pane
						SlideEntity currentScrollPaneEntity =
							currentScrollPane.getEntities().get(i);


						// Creates a Text Player if it is Text
						if (currentScrollPaneEntity instanceof Text) {

							// Instantiate the Text Player object passing the
							// Reference to Scroll Pane
							TextPlayer currentTextPlayer = new TextPlayer(
									(Text) currentSlideEntity,
									currentScrollPanePlayer);


							// Adds the TextPlayer to list of Entity Players with the Scroll Pane Player
							currentScrollPanePlayer.getEntityVector().add(
									currentTextPlayer);
						}

						// Creates an Image Player if it is an Image
						else if (currentScrollPaneEntity instanceof XMLImage) {

							// Instantiate the Image Player object
							ImagePlayer currentImagePlayer = new ImagePlayer(
									(XMLImage) currentSlideEntity,
									currentScrollPanePlayer);


							// Adds the ImagePlayer to list of Entity Players
							entityPlayers.add(currentImagePlayer);
						}

					}

					// Sets the time instance variables in Entity
					currentScrollPanePlayer.setStartTime(
							currentSlideEntity.getStartTime());
					currentScrollPanePlayer.setEndTime(
							currentSlideEntity.getEndTime());

					// Adds the GraphicsPlayer to list of Entity Players
					entityPlayers.add(currentScrollPanePlayer);

				}

				//Display the Entity Player - Everything is Displayed at once
				entityPlayers.get(i).display();
				entityPlayers.get(i).setIsActive(true);
				currZorder = i;
			}
		}

		answerBoxA = new AnswerBlock(currentQuizSlide,0);
		answerBoxA.getAnswerArea().setZOrder(++currZorder);
		answerBoxA.display();

		answerBoxB = new AnswerBlock(currentQuizSlide,1);
		answerBoxB.getAnswerArea().setZOrder(++currZorder);
		answerBoxB.display();

		answerBoxC = new AnswerBlock(currentQuizSlide,2);
		answerBoxC.getAnswerArea().setZOrder(++currZorder);
		answerBoxC.display();

		answerBoxD = new AnswerBlock(currentQuizSlide,3);
		answerBoxD.getAnswerArea().setZOrder(++currZorder);
		answerBoxD.display();

		//Checks whether the question has been answered already and then colours the answer boxes.
		int colourCode;
		if (currentQuizSlide.getGivenAnswer() == currentQuizSlide.getCorrectAnswer())
			colourCode = GREEN;
		else
			colourCode = RED;

		// If answer A has been answered before
		if (1 == currentQuizSlide.getGivenAnswer())
		{
			// Re colour the answer box
			answerBoxA.unDisplay();
			answerBoxA.getAnswerArea().setLineColor(colourCode);
			answerBoxA.display();

			// generate feedback
			feedbackGenerator(1);

			// set flag
			questionAnswered = true;
		}
		else if (2 == currentQuizSlide.getGivenAnswer())
		{
			// Re colour the answer box
			answerBoxB.unDisplay();
			answerBoxB.getAnswerArea().setLineColor(colourCode);
			answerBoxB.display();

			// generate feedback
			feedbackGenerator(2);

			// set flag
			questionAnswered = true;
		}
		else if (3 == currentQuizSlide.getGivenAnswer())
		{
			// Re colour the answer box
			answerBoxC.unDisplay();
			answerBoxC.getAnswerArea().setLineColor(colourCode);
			answerBoxC.display();

			// generate feedback
			feedbackGenerator(3);

			// set flag
			questionAnswered = true;
		}
		else if (4 == currentQuizSlide.getGivenAnswer())
		{
			// Re colour the answer box
			answerBoxD.unDisplay();
			answerBoxD.getAnswerArea().setLineColor(colourCode);
			answerBoxD.display();

			// generate feedback
			feedbackGenerator(4);

			// set flag
			questionAnswered = true;
		}
	}

	/**
	 * Loops through all the media entity Players setting their volume to the overallVolume which is passed in.
	 * @param overallVolume
	 */
	public static void alterVolume(float overallVolume){

		// Used for the loops
		int i;

		// Used so that the methods associated with a video player can be accessed.
		VideoPlayer currentVideoPlayer;

		// Used so that the methods associated with a Audio player can be accessed.
		AudioPlayer currentAudioPlayer;

		// Used so that the methods associated with a midi player can be accessed.
		MidiPlayer currentMidiPlayer;

		// A Fix to get it working with the video and audio module
		overallVolume = (float)(overallVolume/1.25);

		// Tests whether there are any entities to displayed on the slide.
		if (entityPlayers != null)
		{
			for(i =0;i <entityPlayers.size(); i++)
			{
				// test whether the players are active.
				if (entityPlayers.get(i).getIsActive())
				{
					if (entityPlayers.get(i) instanceof VideoPlayer)
					{
						// Set the volume of the media player
						currentVideoPlayer = (VideoPlayer) entityPlayers.get(i);
						currentVideoPlayer.setVolume(overallVolume);
					}

					if (entityPlayers.get(i) instanceof AudioPlayer)
					{
						// Set the volume of the media player
						currentAudioPlayer = (AudioPlayer) entityPlayers.get(i);
						currentAudioPlayer.setVolume(overallVolume);
					}

					if (entityPlayers.get(i) instanceof MidiPlayer)
					{
						// Set the volume of the media player
						currentMidiPlayer = (MidiPlayer) entityPlayers.get(i);
						currentMidiPlayer.setVolume(overallVolume);
					}
				}
			}
		}
	}


	/**
	 * Loops through all the media entity Players calling their mute methods.
	 */
	public static void muteAll(){
		// Used for the loops
		int i;
		// Used so that the methods associated with a video player can be accessed.
		VideoPlayer currentVideoPlayer;

		// Used so that the methods associated with a Audio player can be accessed.
		AudioPlayer currentAudioPlayer;

		// Used so that the methods associated with a midi player can be accessed.
		MidiPlayer currentMidiPlayer;

		// Tests whether there are any entities to displayed on the slide.
		if (entityPlayers != null)
		{
			for(i =0;i <entityPlayers.size(); i++)
			{
				// test whether the players are active.
				if (entityPlayers.get(i).getIsActive())
				{
					if (entityPlayers.get(i) instanceof VideoPlayer)
					{
						// Mute method called
						currentVideoPlayer = (VideoPlayer) entityPlayers.get(i);
						currentVideoPlayer.muteMedia();
					}

					if (entityPlayers.get(i) instanceof AudioPlayer)
					{
						// Mute method called
						currentAudioPlayer = (AudioPlayer) entityPlayers.get(i);
						currentAudioPlayer.muteMedia();
					}

					if (entityPlayers.get(i) instanceof MidiPlayer)
					{
						// Mute method called
						currentMidiPlayer = (MidiPlayer) entityPlayers.get(i);
						currentMidiPlayer.muteMedia();
					}
				}
			}
		}
	}

	/**
	 * Loops through all the media entity Players calling their un mute methods.
	 */
	public static void unMuteAll(){
		// Used for the loops
		int i;
		// Used so that the methods associated with a video player can be accessed.
		VideoPlayer currentVideoPlayer;

		// Used so that the methods associated with a Audio player can be accessed.
		AudioPlayer currentAudioPlayer;

		// Used so that the methods associated with a midi player can be accessed.
		MidiPlayer currentMidiPlayer;

		// Checks whether is any content on the current slide.
		if (null != entityPlayers)
		{
			for(i =0;i <entityPlayers.size(); i++)
			{
				// test whether the players are active.
				if (entityPlayers.get(i).getIsActive())
				{
					if (entityPlayers.get(i) instanceof VideoPlayer)
					{
						// Un mute method called
						currentVideoPlayer = (VideoPlayer) entityPlayers.get(i);
						currentVideoPlayer.unMuteMedia();
					}

					if (entityPlayers.get(i) instanceof AudioPlayer)
					{
						// Un mute method called
						currentAudioPlayer = (AudioPlayer) entityPlayers.get(i);
						currentAudioPlayer.unMuteMedia();
					}

					if (entityPlayers.get(i) instanceof MidiPlayer)
					{
						// Un mute method called
						currentMidiPlayer = (MidiPlayer) entityPlayers.get(i);
						currentMidiPlayer.unMuteMedia();
					}
				}
			}
		}
	}

	/**
	 * Loops through the entityPlayers vector calling there unDisplay methods.
	 */
	public static void hideAll() {
		if(Debug.renderer) System.out.println("Hideall");

		//ImagePlayer tempImagePlayer;

		// Get the size of the entity players vector
		int total = entityPlayers.size();
		if(Debug.renderer) System.out.println("Entity Player size:" + entityPlayers.size());

		// Get current quiz slide
		currentQuizSlide = Engine.getCurrentQuizSlide();

		// Check whether the slide has any content
		if(null != entityPlayers) {
			for(int i = 0; i < total; i++) {
				entityPlayers.get(i).unDisplay();
				entityPlayers.get(i).setIsActive(false);
			}

		}

		// Hide all quiz entities
		//if (currentQuizSlide != null) {
			// if feedback has been displayed.
			if(null != correctnessTextPlayer)
				correctnessTextPlayer.unDisplay();

			// Destroy the local mouse monitor if the quiz slide is present
			if(null != questionMouseMonitor)
				questionMouseMonitor.destroy();

			// Hide the quiz elements
			if( null != scoreTextPlayer && null != answerBoxA && null != answerBoxB
				 && null != answerBoxC  && null != answerBoxD) {
				scoreTextPlayer.unDisplay();
				answerBoxA.unDisplay();
				answerBoxB.unDisplay();
				answerBoxC.unDisplay();
				answerBoxD.unDisplay();
			}
	//	}

	}
	/**
	 * Clears the entityPlayers vector and resets the questionAnswered flag.
	 */
	public static void clearEntities()
	{
		// Clears the vector of Entity Players ready for JM Garbage collection
		entityPlayers = new Vector<Entity>();
		if(Debug.renderer) System.out.println("New Entity Size after clear:" + entityPlayers.size());

		//*MA - Remove all items from the slide area
	    //Entities now not referenced to the slide or to sliderenderer
		Gui.getSlidePanel().removeAll();

		//Now dereferenced, run garbage collection to free heap space
		Runtime r = Runtime.getRuntime();
		r.gc();
		r.runFinalization();

		// Reset the flag
		questionAnswered = false;
	}
	/**
	 * Generates the feedback for when a question has been answered
	 * @param anAnswer
	 */
	public static void feedbackGenerator(int anAnswer){

	// Gets current Quiz Slide
	if 	(null != Engine.getCurrentQuizSlide())  // Stops the method from crashing
	{
			answerBoxA.lmmDestroy();
			answerBoxB.lmmDestroy();
			answerBoxC.lmmDestroy();
			answerBoxD.lmmDestroy();

			//questionMouseMonitor.destroy(); // Clears mouse monitor

			// update current quiz slide to show the user has answered it
			currentQuizSlide.setGivenAnswer(anAnswer);

			//update Score
			scoreTextPlayer.unDisplay();
			score.setData("Score \n   " + QuizHandler.getAnswersCorrect() +  "\n ----- \n   " + QuizHandler.getQuestionsAnswered() );
			scoreTextPlayer.setText(score);
			scoreTextPlayer = new TextPlayer(score);
			scoreTextPlayer.display();

			// Displays the feedback whether they answere the question correct or not
			// Check whether the correctness has been instantiated.
			if (null != correctness); {
				correctness = new Text();
				correctness.setZOrder(currentQuizSlide.getEntities().size() +
						currentQuizSlide.getFeedback().size() + 7);

			}

			if(anAnswer == currentQuizSlide.getCorrectAnswer()) {
				// Set up the text player for the the question has been answered
				// correctly
				correctness.setData("Correct");
				correctness.setFontSize(currentQuizSlide.getCorrectSize());
				correctness.setStartPoint(currentQuizSlide.getCorrectStart());
				correctness.setFontColor(currentQuizSlide.getCorrectColour());
				correctness.setBackgroundColor(currentQuizSlide.getCorrectBkcol());
				correctness.setFontAlpha(currentQuizSlide.getCorrectAlpha());
				correctness.setBackgroundAlpha(currentQuizSlide.getCorrectBkAlpha());
				correctness.setOnClick(-1);

				// Display the text on the slide.
				correctnessTextPlayer = new TextPlayer(correctness);
				correctnessTextPlayer.display();
			}
			else {
				// Set up the text player for the the question has been answered incorrectly
				correctness.setData("Incorrect");
				correctness.setFontSize(currentQuizSlide.getCorrectSize());
				correctness.setStartPoint(currentQuizSlide.getCorrectStart());
				correctness.setFontColor(currentQuizSlide.getCorrectColour());
				correctness.setBackgroundColor(currentQuizSlide.getCorrectBkcol());
				correctness.setFontAlpha(currentQuizSlide.getCorrectAlpha());
				correctness.setBackgroundAlpha(currentQuizSlide.getCorrectBkAlpha());
				correctness.setOnClick(-1);

				// Display the text on the slide.
				correctnessTextPlayer = new TextPlayer(correctness);
				correctnessTextPlayer.display();
			}


			//Loops through and generates feedback
			SlideEntity currentSlideEntity;
			int i;

			//TODO this is wrong!!!
			int vectorOrginal = currentQuizSlide.getEntities().size();
			for(i = 0; i < currentQuizSlide.getFeedback().size(); i++) {
				currentSlideEntity = currentQuizSlide.getFeedback().get(i);

				// Set z order
				currentSlideEntity.setZOrder(vectorOrginal+ i+1);

				// Creates a Text Player if it is Text
				if (currentSlideEntity instanceof Text) {

					// Instantiate the Text Player object
					TextPlayer currentTextPlayer = new TextPlayer(
							(Text) currentSlideEntity);

					// Adds the TextPlayer to list of Entity Players
					entityPlayers.add(currentTextPlayer);

					// Display current Feedback Entity
					currentTextPlayer.display();
				}

				// Creates an Image Player if it is an Image
				else if (currentSlideEntity instanceof XMLImage) {

					// Instantiate the Image Player object
					ImagePlayer currentImagePlayer = new ImagePlayer(
							(XMLImage) currentSlideEntity);

					// Adds the ImagePlayer to list of Entity Players
					entityPlayers.add(currentImagePlayer);

					// Display current Feedback Entity
					currentImagePlayer.display();
				}

				// Creates a Video Player if it is a Video
				else if (currentSlideEntity instanceof Video) {

					// Instantiate the Video Player object
					VideoPlayer currentVideoPlayer = new VideoPlayer(
							(Video) currentSlideEntity);

					// Adds the AudioPlayer to list of Entity Players
					entityPlayers.add(currentVideoPlayer);

					// Display current Feedback Entity
					currentVideoPlayer.display();
				}

				// Creates a Audio Player if it is an Audio
				else if (currentSlideEntity instanceof Audio) {

					// Instantiate the Audio Player object
					AudioPlayer currentAudioPlayer = new AudioPlayer(
							(Audio) currentSlideEntity);


					// Adds the Audio Player to list of Entity Players
					entityPlayers.add(currentAudioPlayer);

					// Display current Feedback Entity
					currentAudioPlayer.display();

				}

/*				// Creates a Graphics Player if it is a Graphic
				else if (currentSlideEntity instanceof Shape)
				{

				// Instantiate the Graphics Player object
				GraphicsPlayer currentGraphicPlayer = new GraphicsPlayer(
					(Shape) currentSlideEntity);


				// Adds the GraphicsPlayer to list of Entity Players
				entityPlayers.add(currentGraphicPlayer);

				// Display current Feedback Entity
				currentGraphicPlayer.display();
				}*/

				// Creates a Midi Player if it is Midi
				else if (currentSlideEntity instanceof Shape) {

					// Instantiate the Graphics Player object
					MidiPlayer currentMidiPlayer = new MidiPlayer(
							(Midi) currentSlideEntity);


					// Adds the GraphicsPlayer to list of Entity Players
					entityPlayers.add(currentMidiPlayer);

					// Display current Feedback Entity
					currentMidiPlayer.display();
				}


				// Handles if a Slide Entity is a ScrollPane - Only Images and
				// Text Can be displayed at the moment
				else if (currentSlideEntity instanceof ScrollPane) {

					// Casks the SlideEntity to a Scroll Pane
					ScrollPane currentScrollPane = (ScrollPane) currentSlideEntity;

					// Instantiate the ScrollPane Player object
					ScrollPanePlayer currentScrollPanePlayer = new ScrollPanePlayer((ScrollPane) currentSlideEntity);

					for (i=0;i<currentScrollPane.getEntities().size();i++)
					{
						// Stores the current slide Entity to be added to the scroll pane
						SlideEntity currentScrollPaneEntity = currentScrollPane.getEntities().get(i);


						// Creates a Text Player if it is Text
						if (currentScrollPaneEntity instanceof Text)
						{

							// Instantiate the Text Player object passing the Reference to Scroll Pane
							TextPlayer currentTextPlayer = new TextPlayer((Text) currentScrollPaneEntity, currentScrollPanePlayer);


							// Adds the TextPlayer to list of Entity Players with the Scroll Pane Player
							currentScrollPanePlayer.getEntityVector().add(currentTextPlayer);
						}

						// Creates an Image Player if it is an Image
						else if (currentScrollPaneEntity instanceof XMLImage)
						{

							// Instantiate the Image Player object
							ImagePlayer currentImagePlayer = new ImagePlayer((XMLImage) currentSlideEntity, currentScrollPanePlayer);


							// Adds the ImagePlayer to list of Entity Players
							entityPlayers.add(currentImagePlayer);
						}

					}

					// Adds the GraphicsPlayer to list of Entity Players
					entityPlayers.add(currentScrollPanePlayer);

					// Display current Feedback Entity
					currentScrollPanePlayer.display();
				}
			}
		}

	// Resizes all the feedback entities to fill the screen.
	//resizeAll(scaleFactor);

	}


	/**
	 * Not used in this class but it is needed for the key listener interface.
	 */
	public void keyTyped(KeyEvent kevt){ // Ignore
	}

	/**
	 * This method is called when a user has pressed the right arrow key or space bar or enter on the keyboard.
	 * It then calls the next slide method in engine. Also if the left arrow key or backspace key is press,
	 * this method calls the previous slide method in engine
	 */
	public void keyPressed(KeyEvent e) {
		if (SafetyNet.getSlideShow() != null)
		{
			if (37 == e.getKeyCode() ) // if left arrow is pressed
			{
				pauseSchedule();
				hideAll();
				Engine.prevSlide();
				if(Debug.renderer) System.out.println("Go to previous slide");
			}


			else if (39 == e.getKeyCode()) // if right arrow is pressed
			{
				pauseSchedule();
				hideAll();
				Engine.nextSlide();
				if(Debug.renderer) System.out.println("Go to next slide");
			}

			else if (( 32 == e.getKeyCode()) || (10 == e.getKeyCode())) // if space or enter key are pressed
			{
				pauseSchedule();
				hideAll();
				Engine.nextSlide();
				if(Debug.renderer) System.out.println("Go to next slide");
			}
			else if (8 == e.getKeyCode())//if backspace is pressed.
			{
				pauseSchedule();
				hideAll();
				Engine.prevSlide();
				if(Debug.renderer) System.out.println("Go to previous slide");
			}
		}
		// Prints out the id of every key press
		if(Debug.renderer) System.out.println(e.getKeyCode());
	}

	/**
	 * Not used in this class but it is needed for the key listener interface.
	 */
	public void keyReleased(KeyEvent ke) { // ignore
	}
}