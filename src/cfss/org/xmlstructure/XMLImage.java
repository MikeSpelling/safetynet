/**
 * 		Crab Factory Software Solutions
 *
 * 		Software: SafetyNet
 * 		Module: XMLImage
 *
 * 		Author: Mark Mellar
 *
 * 		Contributers: Callum Goddard
 * 		Testers:
 *
 * 		Description:
 * 		Stores information pertaining to an image file which is to be displayed in
 * 		a slide
 *
 */

package cfss.org.xmlstructure;

/**
 * Stores information pertaining to an image file which is to be displayed in
 * a slide
 * @author Mark Mellar
 *
 */
public final class XMLImage extends ClickableEntity{

	/**
	 * height of the image in pixels
	 */
	private int height;
	/**
	 * width of the image in pixels
	 */
	private int width;
	/**
	 * file path to the image to be displayed
	 */
	private String path;

	/**
	 * constructor
	 */
	public XMLImage() {
		super();
	}

	/**
	 * copy constructor
	 * @param def the XMLImage to be copied
	 */
	public XMLImage(XMLImage def){
		super(def);
		//primitives
		this.width = def.getWidth();
		this.height = def.getHeight();

		//immutable
		this.path = def.getPath();

	}

	/**
	 *  returns height
	 * @return The height of the image (pixels)
	 */
	public int getHeight() {
		return height;
	}

	/**
	 *  returns path
	 * @return the local file path of the image to be displayed
	 */
	public String getPath() {
		return path;
	}

	/**
	 *  returns width
	 * @return the width of the image to be displayed (pixels)
	 */
	public int getWidth() {
		return width;
	}

	/**
	 *  sets height to height
	 * @param height the height of the image (pixels)
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 *  sets path to path
	 * @param path the local file path of the image
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 *  set width to width
	 * @param width the width of the image (pixels)
	 */
	public void setWidth(int width) {
		this.width = width;
	}
}