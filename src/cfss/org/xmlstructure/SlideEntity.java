/**
 * 		Crab Factory Software Solutions
 *
 * 		Software: SafetyNet
 * 		Module: SlideEntity
 *
 * 		Author: Mark Mellar
 *
 * 		Contributers: Callum Goddard
 * 		Testers:
 *
 * 		Description:
 * 		Stores information pertaining to an entity which is to be displayed on a
 * 		slide (abstract, everything which can go on a slide inherits from this)
 *
 */

package cfss.org.xmlstructure;

/**
 * Stores information pertaining to an entity which is to be displayed on a
 * slide (abstract, everything which can go on a slide inherits from this)
 * @author Mark Mellar
 *
 */
public abstract class SlideEntity{

	// The time the entity should be removed from the screen
	// (Milliseconds, from beginning of SLIDE not element)
	private int endTime;
	// The position on the screen the entity should be displayed at
	private XMLPoint startPoint = new XMLPoint(0,0);
	// The time the entity should be displayed
	// (Milliseconds, from beginning of slide startPoint)
	private int startTime;
	// Stores the z order of the slideEntitiy
	private int zOrder = 1;


	/**
	 *  Constructor.
	 */
	public SlideEntity(){
		startPoint = new XMLPoint();
	}

	/**
	 * Copy constructor.
	 *
	 * @param def the entity to copy
	 */
	public SlideEntity(SlideEntity def) {
		// Primitives
		this.startTime = def.getStartTime();
		this.endTime = def.getEndTime();

		// Non immutable objects
		this.startPoint = new XMLPoint(def.getStartPoint());
	}


	/**
	 * Returns endTime.
	 *
	 * @return the time (from the beginning of the slide) the entity should end
	 * (milliseconds)
	 */
	public int getEndTime() {
		return endTime;
	}

	/**
	 * Returns startPoint.
	 *
	 * @return point defining the position of the top left hand corner of the
	 * entity.
	 */
	public XMLPoint getStartPoint() {
		return startPoint;
	}

	/**
	 * Returns startTime.
	 *
	 * @return the time (from the beginning of the slide) that the element
	 * should be displayed (milliseconds).
	 */
	public int getStartTime() {
		return startTime;
	}

	/**
	 * Sets endTime to endTime.
	 *
	 * @param endTime the time (from the beginning of the slide) the entity
	 * should end (milliseconds).
	 */
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	/**
	 * Sets startPoint to startPoint.
	 *
	 * @param startPoint point defining the position of the top left hand
	 * corner of the entity.
	 */
	public void setStartPoint(XMLPoint startPoint) {
		this.startPoint = startPoint;
	}

	/**
	 * Sets startTime to startTime.
	 *
	 * @param startTime the time (from the beginning of the slide) that the
	 * element should be displayed (milliseconds).
	 */
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}


	/**
	 * Set the z order.
	 *
	 * @param aZOrder is the layered z order to set it to.
	 */
	public void setZOrder(int aZOrder) {
		zOrder = aZOrder;
	}


	/**
	 * Returns the Z order.
	 * @return zOrder is the integer z order which defines it's layer.
	 */
	public int getZOrder() {
		return zOrder;
	}
}