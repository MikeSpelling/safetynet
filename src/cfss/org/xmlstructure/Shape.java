/**
 * 		Crab Factory Software Solutions
 *
 * 		Software: 		SafetyNet
 * 		Module: 		Shape
 *
 * 		Author: 		Mark Mellar
 *
 * 		Contributers: 	Callum Goddard
 * 		Testers:
 *
 * 		Description:
 * 		Stores information pertaining to a shape (circle or polygon) which is to
 * 		be displayed on a slide
 *
 */

package cfss.org.xmlstructure;

import java.awt.*;
/**
 * Stores information pertaining to a shape (circle or polygon) which is to
 * be displayed on a slide
 * @author Mark Mellar
 *
 */
public abstract class Shape extends ClickableEntity {

	//transparency of the shape
	private float alpha;
	//colour of shape
	private Color bkColor;
	//colour transition starts at
	private Color color1 = new Color(0);
	//colour transition ends at
	private Color color2 = new Color(0);
	//true if the transition cycles
	private boolean cyclic;
	//true if shape is filled
	private boolean fill;
	//the fill type
	private int fillType;
	//thickness of shape edge (pixels)
	private int thickness;
	//x position transition starts at
	private int xAtColour1;
	//x position transition ends at
	private int xAtColour2;
	//y position transition starts at
	private int yAtColour1;
	//y position transition ends at
	private int yAtColour2;

	/**
	 *  constructor
	 */
	public Shape(){
		super();
	}

	/**
	 * copy constructor
	 * @param def the shape to be copied
	 */
	public Shape(Shape def){
		super(def);
		//primitives
		this.fill = def.isFill();
		this.thickness = def.getThickness();
		this.alpha = def.getAlpha();
		//immutable
		this.bkColor = def.getColor();
	}

	/**
	 *  return alpha
	 * @return the transparency of the shape
	 */
	public float getAlpha() {
		return alpha;
	}

	/**
	 *  returns color
	 * @return the background colour of the shape
	 */
	public Color getColor() {
		return bkColor;
	}

	/**
	 * returns colour1
	 * @return the colour the shape background transitions from
	 */
	public Color getColor1() {
		return color1;
	}

	/**
	 * returns colour2
	 * @return the colour the shape background transitions to
	 */
	public Color getColor2() {
		return color2;
	}

	/**
	 * returns fill type 1 denotes solid fill (of bkColor), 2 denotes fading
	 * fill between color1 and color2.
	 *
	 * @return fillType is the integer to return.
	 */
	public int getFillType() {
		return fillType;
	}

	/**
	 * returns thickness
	 * @return the thickness of the outline of the shape (pixels)
	 */
	public int getThickness() {
		return thickness;
	}
	/**
	 * returns x position of where colour 1 starts when fade filling
	 * @return x position of where colour 1 starts when fade filling
	 */
	public int getxAtColour1() {
		return xAtColour1;
	}

	/**
	 * returns x position of where colour 2 ends when fade filling
	 * @return x position of where colour 2 ends when fade filling
	 */
	public int getxAtColour2() {
		return xAtColour2;
	}

	/**
	 * returns y position of where colour 1 starts when fade filling
	 * @return y position of where colour 1 starts when fade filling
	 */
	public int getyAtColour1() {
		return yAtColour1;
	}

	/**
	 * returns y position of where colour 2 ends when fade filling
	 * @return y position of where colour 2 ends when fade filling
	 */
	public int getyAtColour2() {
		return yAtColour2;
	}
	/**
	 *  returns cyclic
	 * @return true if the shape is to be filled cyclicly else false
	 */
	public boolean isCyclic() {
		return cyclic;
	}
	/**
	 *  returns fill
	 * @return true if the shape is to be filled, else false
	 */
	public boolean isFill() {
		return fill;
	}
	/**
	 *  sets alpha to alpha
	 * @param alpha transparency of the shape
	 */
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	/**
	 *  sets color to color
	 * @param color the colour of the shape
	 */
	public void setColor(Color color) {
		this.bkColor = color;
	}
	/**
	 * sets color1 to color1
	 * @param color1 the colour the transition starts at
	 */
	public void setColor1(Color color1) {
		this.color1 = color1;
	}

	/**
	 * sets color2 to color2
	 * @param color2 the colour the transition ends at
	 */
	public void setColor2(Color color2) {
		this.color2 = color2;
	}


	/**
	 * sets cyclic to cyclic
	 * @param cyclic true if the shape is to be filled, cyclicly else false
	 */
	public void setCyclic(boolean cyclic) {
		this.cyclic = cyclic;
	}


	/**
	 *  sets fill to fill
	 * @param fill true if the shape is to be filled, else false
	 */
	public void setFill(boolean fill) {
		this.fill = fill;
	}

	public void setFillType(int fillType) {
		this.fillType = fillType;
	}

	/**
	 *  sets thickness to thickness
	 * @param thickness the thickness of the outline of the shape (pixels)
	 */
	public void setThickness(int thickness) {
		this.thickness = thickness;
	}


	/**
	 * sets xAtColour1 to xAtColour1
	 * @param xAtColour1 x position transition starts at
	 */
	public void setxAtColour1(int xAtColour1) {
		this.xAtColour1 = xAtColour1;
	}
	/**
	 * sets xAtColour2 to xAtColour2
	 * @param xAtColour2 x position transition ends at
	 */

	public void setxAtColour2(int xAtColour2) {
		this.xAtColour2 = xAtColour2;
	}


	/**
	 * sets yAtColour1 to yAtColour1
	 * @param yAtColour1 y position transition starts at
	 */
	public void setyAtColour1(int yAtColour1) {
		this.yAtColour1 = yAtColour1;
	}

	/**
	 * sets yAtColour2 to yAtColour2
	 * @param yAtColour2 y position transition ends at
	 */

	public void setyAtColour2(int yAtColour2) {
		this.yAtColour2 = yAtColour2;
	}
}