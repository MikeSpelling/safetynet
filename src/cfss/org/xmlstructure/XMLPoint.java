/**
 * 		Crab Factory Software Solutions
 *
 * 		Software: SafetyNet
 * 		Module: Point
 *
 * 		Author: Mark Mellar
 *
 * 		Contributers: Michael Spelling, Callum Goddard
 * 		Testers:
 *
 * 		Description:
 * 		Stores information pertaining to a point (x,y coordinates) on a slide.
 *
 */


package cfss.org.xmlstructure;

/**
 * Stores information pertaining to a point (x,y coordinates) on a slide.
 * @author Mark Mellar
 *
 */
public final class XMLPoint {

	/**
	 * x ordinate of the point.
	 */
	private int x;
	/**
	 * y ordinate of the point.
	 */
	private int y;


	/**
	 * Constructor.
	 */
	public XMLPoint(){
	}


	/**
	 * Copy constructor.
	 *
	 * @param def The point to be copied.
	 */
	public XMLPoint(XMLPoint def) {
		this.x = def.getX();
		this.y = def.getY();
	}


	/**
	 * Overloaded constructor to take integers.
	 *
	 * @param x is the x coordinate.
	 * @param y is the y coordinate.
	 */
	public XMLPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}


	/**
	 * Returns x.
	 *
	 * @return The x ordinate of the point.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns y.
	 *
	 * @return The y ordinate of the point
	 */
	public int getY() {
		return y;
	}


	/**
	 * Set x to x.
	 *
	 * @param x the x ordinate of the point.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Set y to y.
	 *
	 * @param y The y ordinate of the point.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 *
	 * Sets the x, y coordinates.
	 * @param x is the x coordinate.
	 * @param y is the y coordinate.
	 */
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
}