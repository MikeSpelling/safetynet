/**
 * 		Crab Factory Software Solutions
 *
 * 		Software: 		SafetyNet
 * 		Module: 		Video
 *
 * 		Author: 		Mark Mellar
 *
 * 		Contributers: 	Callum Goddard
 * 		Testers:
 *
 * 		Description:
 * 		Stores information pertaining to a video file which is to be played in
 *  	a slide
 *
 */

package cfss.org.xmlstructure;

/**
 * Stores information pertaining to a video file which is to be played in
 * a slide
 * @author Mark Mellar
 *
 */
public final class Video extends TimedMedia{

	/**
	 * height of the video window in pixels
	 */
	private int height;
	/**
	 * width of the video in pixels
	 */
	private int width;
	/**
	 * constructor
	 */
	public Video() {
		super();
	}

	/**
	 * copy constructor
	 * @param def The video to be copied
	 */
	public Video(Video def) {
		super(def);
		this.width = def.getWidth();
		this.height = def.getHeight();

	}

	/**
	 * Returns height
	 * @return The height if the video window (pixels)
	 */
	public int getHeight() {
		return height;
	}

	/**
	 *  Returns width
	 * @return the width of the video window (pixels)
	 */
	public int getWidth() {
		return width;
	}

	/**
	 *  sets height to height
	 * @param height the height of the video window (pixels)
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 *  sets width to width
	 * @param width The width of the video window (pixels)
	 */
	public void setWidth(int width) {
		this.width = width;
	}
}