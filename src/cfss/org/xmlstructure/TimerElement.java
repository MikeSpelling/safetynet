/**
 * 		Crab Factory Software Solutions
 *
 * 		Software: 		SafetyNet
 * 		Module: 		TimerElement
 *
 * 		Author: 		Mark Mellar
 *
 * 		Contributers: 	Callum Goddard
 * 		Testers:
 *
 * 		Description:
 * 		Stores information pertaining a timer element, used to define complex
 * 		timing arrangements in media objects
 *
 */

package cfss.org.xmlstructure;

/**
 * Stores information pertaining a timer element, used to define complex
 * timing arrangements in media objects
 * @author Mark Mellar
 *
 */
public final class TimerElement {

	/**
	 * the amount of time the media is to pause for (milliseconds)
	 */
	private int pauseDuration;
	/**
	 * the point in playback the media is to pause (milliseconds)
	 */
	private int pauseTime;
	/**
	 * the point in playback the media is to resume after pauseDuration has expired (milliseconds)
	 */
	private int playTime;

	/**
	 *  costructor
	 */
	public TimerElement(){
	}

	/**
	 * copy constructor
	 * @param def The timer element to be copied.
	 */
	public TimerElement(TimerElement def) {
		this.playTime = def.getPlayTime();
		this.pauseTime = def.getPauseTime();
		this.pauseDuration = def.getPauseDuration();
	}

	/**
	 *  returns pauseDuration
	 * @return the amount of time the media is to stay paused
	 */
	public int getPauseDuration() {
		return pauseDuration;
	}

	/**
	 *  returns pauseTime
	 * @return the time at which the media is to pause (milliseconds from beginning of media)
	 */
	public int getPauseTime() {
		return pauseTime;
	}

	/**
	 *  returns playTime
	 * @return The time at which the media should begin playback (milliseconds from beginning of media)
	 */
	public int getPlayTime() {
		return playTime;
	}

	/**
	 *  sets PauseDuration to pauseDuration
	 * @param pauseDuration the amount of time the media is to stay paused
	 */
	public void setPauseDuration(int pauseDuration) {
		this.pauseDuration = pauseDuration;
	}

	/**
	 *  sets pauseTime
	 * @param pauseTime the amount of time the media is to stay paused
	 */
	public void setPauseTime(int pauseTime) {
		this.pauseTime = pauseTime;
	}

	/**
	 *  sets playTime to playTime
	 * @param playTime The time at which the media should begin playback (milliseconds from beginning of media)
	 */
	public void setPlayTime(int playTime) {
		this.playTime = playTime;
	}
}