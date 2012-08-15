/**
 * 		Crab Factory Software Solutions
 *
 * 		Software: 		SafetyNet
 * 		Module: 		Circle
 *
 * 		Author: 		Mark Mellar
 *
 * 		Contributers: 	Callum Goddard
 * 		Testers:
 *
 * 		Description:
 * 		Stores information pertaining to a circle which is to be displayed in
 * 		a slide
 *
 */

package cfss.org.xmlstructure;

/**
 * Stores information pertaining to a circle which is to be displayed in
 * a slide
 * @author Mark Mellar
 */

public final class Circle extends Shape{

	/**
	 * The radius of the circle in pixels.
	 */
	private int radius;

	/**
	 *  Constructor.
	 */
	public Circle(){
		super();
	}

	/**
	 * Copy constructor.
	 *
	 * @param def the Circle to copy
	 */
	public Circle(Circle def) {
		super(def);
		radius = def.getRadius();
	}

	/**
	 * Returns radius.
	 *
	 * @return the radius of the circle (pixels)
	 */
	public int getRadius() {
		return radius;
	}

	/**
	 * Set radius to radius.
	 *
	 * @param radius the radius of the circle (pixels)
	 */
	public void setRadius(int radius) {
		this.radius = radius;
	}
}