/**
 * 		Crab Factory Software Solutions
 *
 * 		Software: SafetyNet
 * 		Module: Polygon
 *
 * 		Author: Mark Mellar
 *
 * 		Contributers: Callum Goddard
 * 		Testers:
 *
 * 		Description:
 * 		Stores information pertaining to a polygon which is to be displayed on a
 * 		slide (corners defined as Points)
 *
 */

package cfss.org.xmlstructure;

import java.util.*;
/**
 * Stores information pertaining to a polygon which is to be displayed on a
 * slide (corners defined as Points)
 * @author Mark Mellar
 */
public final class XMLPolygon extends Shape {

	/**
	 * Vector of points defining the vertices of the shape
	 */
	private Vector<XMLPoint> shapePath;

	/**
	 *  Constructor
	 */
	public XMLPolygon(){
		super();
		this.shapePath = new Vector<XMLPoint>();
	}

	/**
	 * copy constructor
	 * @param def the polygon to copy
	 */
	 public XMLPolygon(XMLPolygon def) {
		super(def);
		this.shapePath = new Vector<XMLPoint>();
		Vector<XMLPoint> tempPath = def.getShapePath();
		//copy all points in vector
		if (null != tempPath){
			for (int i = 0; i < tempPath.size(); i++){
				this.shapePath.add(new XMLPoint(tempPath.elementAt(i)));
			}
		}
	}

	/**
	 *  returns the shapePath
	 * @return vector of points, each a vertex of the shape
	 */
	public Vector<XMLPoint> getShapePath() {
		return shapePath;
	}

	 /**
	  * add a point to shapePath
	  * @param point adds the passed point to the vector of shape points
	  */
	public void addPoint(XMLPoint point) {
		this.shapePath.add(point);
	}
}