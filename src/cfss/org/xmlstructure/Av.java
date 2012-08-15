/**
 * 		Crab Factory Software Solutions
 *
 * 		Software: 		SafetyNet
 * 		Module: 		AV
 *
 * 		Author: 		Mark Mellar
 *
 * 		Contributers: 	Callum Goddard
 * 		Testers:
 *
 * 		Description:
 * 		Abstract class, gives common functionality modules storing audio/visual
 * 		file informaion (audio, video and midi)
 *
 */

package cfss.org.xmlstructure;

/**
 * Abstract class, gives common functionality modules storing audio/visual
 * file informaion (audio, video and midi).
 *
 * @author Mark Mellar
 *
 */
public abstract class Av extends SlideEntity{

	/**
	 * true if media is to loop once finished
	 */
	private boolean loop;
	/**
	 * the path to the relevant media file
	 */
	private String path;

	/**
	 * Standard constructor
	 */
	public Av() {
		super();
	}

	/**
	 * Copy constructor.
	 *
	 * @param def The AV to copy
	 */
	public Av(Av def){
		super(def);
		this.path = def.getPath();

		this.loop = def.isLoop();
	}

	/**
	 * Path constructor.
	 *
	 * @param path Path of the file to be played
	 */
	public Av(String path){
		this.path = path;
	}

	/**
	 * Returns path
	 *
	 * @return path of the file to be played
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returns loop
	 *
	 * @return true if playback is to loop else false
	 */
	public boolean isLoop() {
		return loop;
	}

	/**
	 * Sets loop to loop.
	 *
	 * @param loop true if playback is to loop else false
	 */
	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	/**
	 * Sets path to path.
	 *
	 * @param path the path of the file to be played
	 */
	public void setPath(String path) {
		this.path = path;
	}
}