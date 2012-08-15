/**
 * 		Crab Factory Software Solutions
 *
 * 		Software: 		SafetyNet
 * 		Module: 		TimedMedia
 *
 * 		Author: 		Mark Mellar
 *
 * 		Contributers: 	Callum Goddard
 * 		Testers:
 *
 * 		Description:
 * 		Stores information pertaining to media object which implement complex
 * 		timing using TimerElements (Video and audio)
 *
 */

package cfss.org.xmlstructure;

import java.util.Vector;
/**
 * Stores information pertaining to media object which implement complex
 * timing using TimerElements (Video and audio)
 * @author Mark Mellar
 *
 */
public abstract class TimedMedia extends Av{

	/**
	 * true if the media should begin playing as soon as it is displayed
	 */
	private boolean autoplay;
	/**
	 * true if controls are to be displayed
	 */
	private boolean controls;
	/**
	 * vector of start and end points to be played, and the amount of time to wait between them
	 * (see TimerElement)
	 */
	Vector<TimerElement> timer;
	/**
	 * constructor
	 */
	public TimedMedia() {
		super();
	}
	/**
	 * path constructor
	 * @param path The path to the media to be played
	 */
	public TimedMedia(String path) {
		super(path);
	}

	/**
	 * copy constructor
	 * @param def The TimedMedia to be copied
	 */
	public TimedMedia(TimedMedia def){
		super(def);
		this.autoplay = def.isAutoplay();
		this.controls = def.isControls();

		//copy each timer
		timer = new Vector<TimerElement>();
		Vector<TimerElement> tempVect = def.getTimer();
		for (int i = 0; i < timer.size(); i++){
			timer.add(new TimerElement(tempVect.get(i)));
		}
	}

	/**
	 *  returns vector of TimerElements
	 * @return A vector of all the timer elements associated with this object
	 */
	public Vector<TimerElement> getTimer() {
		return timer;
	}

	/**
	 * returns autoplay
	 * @return True if the media is to play when displayed, else false
	 */
	public boolean isAutoplay() {
		return autoplay;
	}

	/**
	 * returns controls
	 * @return True if controls are to be displayed when the media is played, else false
	 */
	public boolean isControls() {
		return controls;
	}

	/**
	 * sets autoplay to autoplay
	 * @param autoplay True if the media is to play when displayed, else false
	 */
	public void setAutoplay(boolean autoplay) {
		this.autoplay = autoplay;
	}

	/**
	 * sets controls to controls
	 * @param controls True if controls are to be displayed when the media is played, else false
	 */
	public void setControls(boolean controls) {
		this.controls = controls;
	}

	/**
	 * adds a new timer to the vector
	 * @param timer A timer element which is to be added to the object
	 */
	public void addTimer(TimerElement timer) {
		this.timer.add(timer);
	}
}