/**
 * 		Crab Factory Software Solutions
 *
 * 		Software: SafetyNet
 * 		Module: Midi
 *
 * 		Author: Mark Mellar
 *
 * 		Contributers: Callum Goddard
 * 		Testers:
 * 		Integrators:
 *
 * 		Description:
 * 		Stores information pertaining to a midi file to be played on a slide
 *
 */


package cfss.org.xmlstructure;

/**
 * Stores information pertaining to a midi file to be played on a slide
 * @author Mark Mellar
 *
 */
public final class Midi extends Av{

	/**
	 * constructor
	 */
	public Midi() {
		super();
	}

	/**
	 * copy constructor
	 * @param def the Midi to copy
	 */
	public Midi(Midi def) {
		super(def);
	}

	/**
	 * path constructor
	 * @param path the path of the midi file to play
	 */
	public Midi(String path) {
		super(path);
	}
}