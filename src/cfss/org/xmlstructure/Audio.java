/**
 * 		Crab Factory Software Solutions
 *
 * 		Software: 		SafetyNet
 * 		Module: 		Audio
 *
 * 		Author: 		Mark Mellar
 *
 * 		Contributers: 	Callum Goddard
 * 		Testers:
 *
 * 		Description:
 * 		Stores information pertaining to an audio file which is to be played in
 *  	a slide
 *
 */

package cfss.org.xmlstructure;

/**
 * Stores information pertaining to an audio file which is to be played in
 * a slide
 * @author Mark Mellar
 *
 */
public final class Audio extends TimedMedia{

	/**
	 * Standard constructor.
	 */
	public Audio() {
		super();
	}

	/**
	 * Copy constructor.
	 *
	 * @param def the Audio to be copied
	 */
	public Audio(Audio def) {
		super(def);
	}

	/**
	 * Path constructor.
	 *
	 * @param path the path of the audio
	 */
	public Audio(String path) {
		super(path);
	}
}