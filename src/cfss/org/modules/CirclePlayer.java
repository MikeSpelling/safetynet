/**
 ** Company Name: 	Touchware Systems
 ** Product Name: 	infoHub
 ** Author: 		Christopher Booth
 ** Contributors: 	Daniel Woodford, Michael Spelling
 ** Created: 		17/05/2010
 ** Modified: 		04/06/2010
 ** Version: 		2.0
 **/

package cfss.org.modules;

import cfss.org.safetynet.*;
import cfss.org.safetynet.gui.Gui;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.swing.*;
import cfss.org.xmlstructure.*;
import cfss.org.LocalMouseMonitor.LocalMouseMonitor;
import cfss.org.LocalMouseMonitor.MouseMonitorListener;
import cfss.org.LocalMouseMonitor.ZoneEvents;

/** This class creates a circle and displays it in a
 ** JPanel that can be used by an upper level class.
 **/
public class CirclePlayer extends Entity {

	private double alpha;
	private int thickness;
	private int originalThickness;
	private int fill_type;
	private Boolean fill;
	private int radius;
	private int orginalRadius;
	private int x_at_color1;
	private int y_at_color1;
	private int x_at_color2;
	private int y_at_color2;
	private int fillColor;
	private int lineColor;
	private int color1;
	private int color2;
	private Boolean is_cyclic;
	private int x;
	private int y;
	private int originalX;
	private int originalY;
	private int onclick;
	private String onclickurl;
	private int starttime;
	private int endtime;

	JPanel newCircle;
	private LocalMouseMonitor circleLMM;

	/** Constructor for new Circle Object **/
	public CirclePlayer(Circle aCircle){
		fillColor = aCircle.getColor().getRGB() + 16777216;
		lineColor = aCircle.getColor().getRGB() + 16777216;
		alpha = aCircle.getAlpha();
		thickness = aCircle.getThickness();
		fill = aCircle.isFill();
		onclick = aCircle.getOnClick();
		x = aCircle.getStartPoint().getX();
		y = aCircle.getStartPoint().getY();
		radius = aCircle.getRadius();
		onclick = aCircle.getOnClick();
		onclickurl = aCircle.getOnClickUrl();
		starttime= aCircle.getStartTime();
		endtime= aCircle.getEndTime();
		originalThickness = thickness;
		originalX = x;
		originalY = y;
		orginalRadius = radius;

		color1 = aCircle.getColor1().getRGB() + 16777216;
		color2 = aCircle.getColor2().getRGB() + 16777216;
		x_at_color1 = aCircle.getxAtColour1();
		x_at_color2 = aCircle.getxAtColour2();
		y_at_color1 = aCircle.getyAtColour1();
		y_at_color2 = aCircle.getyAtColour2();
		is_cyclic = aCircle.isCyclic();
		fill_type = aCircle.getFillType();

		// sets z order
		super.setZOrder(aCircle.getZOrder());

	}

	/** Returns a JPanel with a Circle Drawn on it **/
	public void display() {
		// Sets it to active
		super.setIsActive(true);

		newCircle = new DrawCircle();
		newCircle.setOpaque(false);
		if(onclick >=0 && null==circleLMM){
			circleLMM = new LocalMouseMonitor(new MouseMonitor(),
					newCircle.getLocation(), newCircle.getSize());
			circleLMM.setNotifications(true, false, false, true, false,
					false);

			circleLMM.addZone("polygon", new Point(0, 0),
					newCircle.getSize());
		}
		resize(1);
		Gui.getSlidePanel().add(newCircle,new Integer(super.getZOrder()));
	}


	/**
	 * Hides the circle.
	 */
	public void unDisplay(){
		if(circleLMM != null)
			circleLMM.destroy();
		// Sets it to false
		super.setIsActive(false);

		newCircle.setVisible(false);

	}

	/**
	 * Class to capture mouse events for clickable text.
	 *
	 * @author Michael Spelling
	 */
	private class MouseMonitor implements MouseMonitorListener {
		/**
		 * Blank implementation of unneeded drag monitoring method
		 */
		public void dragEventOccurred(String s, int x, int y,
			Boolean complete){}

		/**
		 * Called if mouse clicks in a monitored zone.
		 */
		public void zoneEventOccurred(String eventZone, int eventType){
			// If within the bounds of the text, go to the link

			if(eventType==ZoneEvents.ENTER && eventZone.equals("polygon")){
					Gui.changeCursor(Cursor.HAND_CURSOR);

			}
			else if (eventType==ZoneEvents.CLICK && eventZone.equals("polygon")){
				// Added by Mike S
				if(onclick > 0)
					Engine.gotoSlide(onclick);
				else if(onclick == 0);
					BrowserLoader.openURL(onclickurl);
			}
		}
	}

	/** Class DrawCircle draws a circle on a JPanel which allows for the
	 ** Circle class to return a JPanel to upper classes.
	 **/
	class DrawCircle extends JPanel {
		private static final long serialVersionUID = 1L;

		public DrawCircle() {
		}

		/** Overriding paintComponent module for the Object */
		@Override public void paintComponent(Graphics g2) {

	        Graphics2D g = (Graphics2D) g2; // Create Graphics Object
	        super.paintComponent(g); // Paint Background

			//move circle to centre of JPanel
			g.translate(thickness/2, thickness/2);

			if(fill==true)
			{
		 	if(fill_type<=0 || fill_type>=3)//if fill_type is not range
		 		{
			 		fill_type=1;
		 		}
		 			if(fill_type==1)
			 		{
			 			Color fillC = new Color(fillColor);
			 			g.setPaint(fillC);//Colour main body
			 		}
			 		else if(fill_type==2)
			 		{
			 			Color grad_col1 = new Color(color1);
						Color grad_col2 = new Color(color2);
						// gradient paint
				 		GradientPaint gradcol = new GradientPaint(x_at_color1, y_at_color1,
				 								grad_col1, x_at_color2, y_at_color2,
				 								grad_col2, is_cyclic);
						g.setPaint(gradcol);//Gradient main body
			 		}

			 		if(alpha>=0 & alpha<=1)
			 		{
			 			g.setComposite(AlphaComposite.getInstance(
			 					AlphaComposite.SRC_OVER, (float)alpha));
			 		}
					g.fillOval(0, 0, radius*2, radius*2);	//create main body
			}

	 		if(thickness!=0)
	 		{
	 			Color lineC = new Color(lineColor);
	 			//set edge thickness
		 	    BasicStroke edgeThickness = new BasicStroke(thickness);
	 			g.setStroke(edgeThickness); //set line thickness
	 			g.setPaint(lineC);			//Colour line
	 			if(alpha>=0 & alpha<=1)
	 			{
	 				g.setComposite(AlphaComposite.getInstance(
	 						AlphaComposite.SRC_OVER, (float)alpha));
	 			}
	 			//create line round shape
	 			g.drawOval(0, 0, radius*2, radius*2);
	 		}
	    }
	}

	/** Get Circle Alpha **/
	public double getAlpha() {
		return alpha;
	}

	/** Set Circle Alpha **/
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	/** Get Circle Thickness **/
	public int getThickness() {
		return thickness;
	}

	/** Set Circle Thickness **/
	public void setThickness(int thickness) {
		this.thickness = thickness;
	}

	/** Get Circle Fill_Type **/
	public int getFill_type() {
		return fill_type;
	}

	/** Set Circle Fill_Type **/
	public void setFill_type(int fill_type) {
		this.fill_type = fill_type;
	}

	/** Get Circle X_at_color1 **/
	public int getX_at_color1() {
		return x_at_color1;
	}

	/** Set Circle X_at_color1 **/
	public void setX_at_color1(int xAtColor1) {
		x_at_color1 = xAtColor1;
	}

	/** Get Circle Y_at_color1 **/
	public int getY_at_color1() {
		return y_at_color1;
	}

	/** Set Circle Y_at_color1 **/
	public void setY_at_color1(int yAtColor1) {
		y_at_color1 = yAtColor1;
	}

	/** Get Circle X_at_color2 **/
	public int getX_at_color2() {
		return x_at_color2;
	}

	/** Set Circle X_at_color2 **/
	public void setX_at_color2(int xAtColor2) {
		x_at_color2 = xAtColor2;
	}

	/** Get Circle Y_at_color2 **/
	public int getY_at_color2() {
		return y_at_color2;
	}

	/** Set Circle Y_at_color2 **/
	public void setY_at_color2(int yAtColor2) {
		y_at_color2 = yAtColor2;
	}

	/** Get Circle FillColor **/
	public int getFillColor() {
		return fillColor;
	}

	/** Set Circle FillColor **/
	public void setFillColor(int fillColor) {
		this.fillColor = fillColor;
	}

	/** Get Circle LineColor **/
	public int getLineColor() {
		return lineColor;
	}

	/** Set Circle LineColor **/
	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}

	/** Get Circle Colour1 **/
	public int getColour1() {
		return color1;
	}

	/** Set Circle Colour1 **/
	public void setColor1(int color1) {
		this.color1 = color1;
	}

	/** Get Circle Colour2 **/
	public int getColor2() {
		return color2;
	}

	/** Set Circle Color2 **/
	public void setColor2(int color2) {
		this.color2 = color2;
	}

	/** Get Circle Is_Cyclic **/
	public Boolean getIs_Cyclic() {
		return is_cyclic;
	}

	/** Set Circle Is_Cyclic **/
	public void setIs_Cyclic(Boolean is_cyclic) {
		this.is_cyclic = is_cyclic;
	}

	/** Get the radius of the Circle **/
	public int getRadius() {
		return radius;
	}

	/** Set the radius of the circle **/
	public void setRadius(int radius) {
		this.radius = radius;
	}

	/** Get the diameter of the circle **/
	public int getCircleDiameter() {
		return radius*2+thickness;
	}

	/**
	 * Gets x value.
	 * @return x, integer x.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets x value.
	 *
	 * @param x the integer to set x to.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Gets y value.
	 *
	 * @return y the integer y to return.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets y value.
	 *
	 * @param y the integer to set y to.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Gets first color.
	 *
	 * @return Color1 the first Color.
	 */
	public int getColor1() {
		return color1;
	}

	/**
	 * Gets the onclick variable.
	 *
	 * @return onclick the integer to return.
	 */
	public int getOnclick() {
		return onclick;
	}

	/**
	 * Sets onclick variable.
	 *
	 * @param onClick the integer to set variable to.
	 */
	public void setOnclick(int onClick) {
		this.onclick = onClick;
	}

	/**
	 * Gets the URL.
	 *
	 * @return onClickUrl the String for the URL.
	 */
	public String getOnclickurl() {
		return onclickurl;
	}

	/**
	 * Sets the URL string.
	 *
	 * @param onClickURL the String to set the URL to.
	 */
	public void setOnclickurl(String onClickURL) {
		this.onclickurl = onClickURL;
	}

	/**
	 * Gets the start time.
	 *
	 * @return startTime the integer time to start.
	 */
	public int getStarttime() {
		return starttime;
	}

	/**
	 * Sets the start time.
	 *
	 * @param startTime is the integer to set start time to.
	 */
	public void setStarttime(int startTime) {
		this.starttime = startTime;
	}

	/**
	 * Gets the end time.
	 *
	 * @return endTime the integer time to end.
	 */
	public int getEndtime() {
		return endtime;
	}

	/**
	 * Sets the end time.
	 *
	 * @param endTime is the integer to set end time to.
	 */
	public void setEndtime(int endTime) {
		this.endtime = endTime;
	}

	/**
	 * Sets whether to fill or not
	 *
	 * @param fill is the boolean variable which is true if circle is filled.
	 */
	public void setFill(Boolean fill) {
		this.fill = fill;
	}

	/**
	 * Gets the fill variable.
	 * @return fill is the boolean which determines whether circle is filled.
	 */
	 public Boolean getFill(){
		 return fill;
	 }

	 /**
	  * Resize method resizes the circle.
	  */
	public void resize(double scaleFactor){
		newCircle.setVisible(false);

		//Original slide width. Change this value if initial width is to be
		//changed from 800 pixels
		int originalSlideWidth = 800;

		//Derive scale factor from new slide panel size vs original size 800x600
		//This is to minimise the effect of cumulative rounding errors
		//Aspect ratio is always maintained for slide panel, hence either
		//dimension may be used to derive scale factor. Here, horizontal used.
		int newSlideWidth = Gui.getSlidePanel().getWidth();

		//Alternative scale factor to be used - relative to initial slide
		//dimensions (800x600) and used with initial shape dimensions (from XML)
		scaleFactor = ((double)newSlideWidth)/originalSlideWidth;

		// Scale centre coordinates
		x = (int)(originalX * scaleFactor);
		y = (int)(originalY * scaleFactor);

		// Scale radius
		radius = (int) (orginalRadius * scaleFactor);

		// Thickness scaled aswell
		thickness = (int) (originalThickness * scaleFactor);

		newCircle.setBounds(x, y, getCircleDiameter(), getCircleDiameter());

		newCircle.setVisible(true);

		//resize the mouse monitor
		if(circleLMM != null) { // Stops null pointer exception
			// Scale the clickable area
			if(circleLMM.retrieveZone("polygon") != null) {
				circleLMM.reposition(newCircle.getLocation().x, newCircle.getLocation().y);
				circleLMM.rescale(newCircle.getSize().width, newCircle.getSize().height);
			}
		}
	}

	/**
	 * Main method - generally used for testing.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		// Instantiate SafetyNet without producing warnings
		@SuppressWarnings("unused")
		SafetyNet myApp = new SafetyNet();

		XMLPoint tempPoint = new XMLPoint();
		Circle tempCircle = new Circle();
		tempCircle.setColor(Color.RED);
		tempCircle.setAlpha(1);
		tempCircle.setThickness(5);
		tempCircle.setOnClick(0);
		tempCircle.setFill(false);
		tempCircle.setOnClick(-1);
		tempPoint.setX(100);
		tempPoint.setY(100);
		tempCircle.setStartPoint(tempPoint);
		tempCircle.setRadius(10);
		tempCircle.setStartTime(0);
		tempCircle.setZOrder(100);

		CirclePlayer circlePlayer = new CirclePlayer(tempCircle);
		circlePlayer.display();

		circlePlayer.resize(2);


		// Stops unused warning

		// Run scrollPane tests if true

	}
}