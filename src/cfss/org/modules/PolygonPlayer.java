/**
 ** Company Name: 	Touchware Systems
 ** Product Name: 	infoHub
 ** Author: 		Christopher Booth
 ** Contributors: 	Rob Aston, Daniel Woodford
 ** Created: 		13/05/2010
 ** Modified: 		04/06/2010
 ** Version: 		2.0
 **/

/**
 * 13/06/10 - Resize function modified
 * Programmer: Michael Angus (Crab Factory Software Solutions)
 * Modification: Scaling is now always performed using the original path
 * 		coordinates of the shape, with scale factor derived relative to original
 * 		slide dimensions. This is to avoid cumulative rounding errors caused by
 * 		multiple previous-value-relative scaling operations
 */
package cfss.org.modules;

import cfss.org.flags.Debug;
import cfss.org.safetynet.*;
import cfss.org.safetynet.gui.Gui;
import cfss.org.xmlstructure.XMLPoint;
import cfss.org.xmlstructure.XMLPolygon;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.util.Vector;
import java.lang.Integer;
import javax.swing.*;
import cfss.org.LocalMouseMonitor.LocalMouseMonitor;
import cfss.org.LocalMouseMonitor.MouseMonitorListener;
import cfss.org.LocalMouseMonitor.ZoneEvents;



/** This class creates a Shape based on x and y coordinates given
 ** and places it in a JPanel that can be used by an upper level class.
 **/
public class PolygonPlayer extends Entity{

	private double alpha;
	private int thickness;
	private int originalThickness;
	private int fill_type;
	private Boolean fill;
	private int x_at_color1;
	private int y_at_color1;
	private int x_at_color2;
	private int y_at_color2;
	private int fillColor;
	private int lineColor;
	private int color1;
	private int color2;
	private Vector<Integer> xPath;
	private Vector<Integer> yPath;

	private Boolean is_cyclic;
	private int x;
	private int y;
	private int originalX;
	private int originalY;
	private int onclick;
	private String onclickurl;
	private int starttime;
	private int endtime;

	private XMLPolygon initialPolygon;

	JPanel shape;
	private int nextslideid;
	private LocalMouseMonitor shapeLMM;

	public PolygonPlayer(){

	}
	/** Constructor for new Shape Object **/
	public PolygonPlayer(XMLPolygon aPolygon){

		// sets z order
		super.setZOrder(aPolygon.getZOrder());

		starttime = aPolygon.getStartTime();
		endtime = aPolygon.getEndTime();
		fillColor = aPolygon.getColor().getRGB() + 16777216;
		lineColor = aPolygon.getColor().getRGB() + 16777216;
		alpha = aPolygon.getAlpha();
		thickness = aPolygon.getThickness();
		fill = aPolygon.isFill();
		onclick = aPolygon.getOnClick();
		onclickurl = aPolygon.getOnClickUrl();
		x = aPolygon.getStartPoint().getX();
		y = aPolygon.getStartPoint().getY();

		color1 = aPolygon.getColor1().getRGB() + 16777216;
		color2 = aPolygon.getColor2().getRGB() + 16777216;
		x_at_color1 = aPolygon.getxAtColour1();
		x_at_color2 = aPolygon.getxAtColour2();
		y_at_color1 = aPolygon.getyAtColour1();
		y_at_color2 = aPolygon.getyAtColour2();
		is_cyclic = aPolygon.isCyclic();
		fill_type = aPolygon.getFillType();


		originalX = x;
		originalY = y;
		originalThickness = thickness;

		xPath = new Vector<Integer>();
		yPath = new Vector<Integer>();


		int i;
		for(i=0; i<aPolygon.getShapePath().size();i++)
		{
			xPath.add(aPolygon.getShapePath().get(i).getX());
			yPath.add(aPolygon.getShapePath().get(i).getY());
		}

		//Store the initial polygon parameters for use with resizing
		initialPolygon = aPolygon;

	}

	/** Method returns a JPanel with a shape drawn on it **/
	public void display() {
		// Sets it to active
		super.setIsActive(true);

		shape = new DrawShape();
		// shape.setBounds(x, y, getObjectWidth(), getObjectHeight());
		shape.setOpaque(false);

		if(onclick >=0 && null==shapeLMM){
			shapeLMM = new LocalMouseMonitor(new MouseMonitor(),
					shape.getLocation(), shape.getSize());
			shapeLMM.setNotifications(true, false, false, true, false,
					false);

			shapeLMM.addZone("polygon", new Point(0, 0),
					shape.getSize());
		}
		resize(1);
		Gui.getSlidePanel().add(shape, new Integer(super.getZOrder()));

	}

	/**
	 * Makes shape invisible.
	 */
	public void unDisplay() {
		if(shapeLMM != null)
			shapeLMM.destroy();
		// Sets it to false
		super.setIsActive(false);

		shape.setVisible(false);
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
				if(0 == onclick)
				{
					//TODO MAKE ON CLICK WORK
//					java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
					try
					{
//						java.net.URI uri = new java.net.URI( onclickurl);
						BrowserLoader.openURL(onclickurl);//added by Mark Mellar
//				        desktop.browse( uri );
					    return;
				    }
					 catch( Exception e1 ) {
					     System.err.println( e1.getMessage() );
				     }
				}

				//set next slide id
				if(onclick >0)
				{

					// nextslideid = onclick;

					Engine.gotoSlide(onclick);//added by mark mellar
					if(Debug.poly) System.out.println("the next slide ID = " + nextslideid);
				}
		}
	}
	}


	/** Find the maximum height of the Shape **/
	public int getObjectHeight(){
		int obj_Height = 0;
		int obj_HeightTemp = 0;
		int i;

		for(i=0; i<yPath.size(); i++)
	    {
			obj_HeightTemp = yPath.elementAt(i);//Temp element

			//if Temp element is bigger than max height
		    if(obj_HeightTemp > obj_Height)
		    {
		    	obj_Height=obj_HeightTemp;	//set a new max height
		    }
	    }
		return obj_Height+this.thickness;
	}

	/** Find the maximum width of the Shape **/
	public int getObjectWidth(){
		int obj_Width = 0;
		int obj_WidthTemp = 0;
		int i;

		for(i=0; i<xPath.size(); i++)
	    {
			obj_WidthTemp = xPath.elementAt(i);	//Temp element

			//if Temp element is bigger than max Width
		    if(obj_WidthTemp > obj_Width)
		    {
		    	obj_Width=obj_WidthTemp;		//set a new max Width
		    }
	    }
		return obj_Width+this.thickness;
	}

	/** This class creates a shape that is drawn onto a JPanel **/
	class DrawShape extends JPanel {

		private static final long serialVersionUID = 1L;

		public DrawShape(){

		}

		/** Overriding paintComponent for the Object **/
		@Override public void paintComponent(Graphics g2) {
			Graphics2D g = (Graphics2D) g2; // Create Graphics Object
	        super.paintComponent(g); // Paint Background

			// Get the size of the x path vectors
	        int size_of_xpath = xPath.size();

	        // Create array of integers
	        int[] xPoints = new int[size_of_xpath];
	 		int[] yPoints = new int[size_of_xpath];

	 		// Add Vector Elements to the Array
	 		for (int j = 0; j < size_of_xpath; j++)
	 		{
	 			xPoints[j] = xPath.elementAt(j);
	 			yPoints[j] = yPath.elementAt(j);
	 		}

			//create polygon
	 		Polygon poly = new Polygon(xPoints, yPoints, size_of_xpath);

	 		//move polygon to centre of JPanel
	 	    poly.translate(thickness/2, thickness/2);
	 	    if (fill==true)
	 	    {
		 	     if(fill_type<=0 || fill_type>=3)//if fill is not range
		 		{
			 		fill_type = 1;
		 		}
		 		else
		 		{
		 			if(fill_type==1)
			 		{
			 			Color fillC = new Color(fillColor);
			 			g.setPaint(fillC);//Colour main body
			 		}
			 		else if(fill_type==2)
			 		{
			 			Color grad_color1 = new Color(color1);
						Color grad_color2 = new Color(color2);
						//gradient
				 		GradientPaint fillcol = new GradientPaint(x_at_color1, y_at_color1,
				 								grad_color1, x_at_color2, y_at_color2,
				 								grad_color2, is_cyclic);
						g.setPaint(fillcol);//Gradient main body
			 		}

			 		if(alpha>=0 & alpha<=1)
			 		{
			 			g.setComposite(AlphaComposite.getInstance
			 							(AlphaComposite.SRC_OVER, (float)alpha));
			 		}
					g.fill(poly);	//create main body
		 		}
	 	    }

	 		if(thickness!=0)
	 		{
	 			Color lineC = new Color(lineColor);
	 			//set edge thickness
		 	    BasicStroke edgeThickness = new BasicStroke(thickness,
		 	    							BasicStroke.CAP_ROUND,
		 	    							BasicStroke.JOIN_ROUND);
	 			g.setStroke(edgeThickness); //set line thickness
	 			g.setPaint(lineC);			//Colour line
	 			if(alpha>=0 & alpha<=1)
	 			{
	 				g.setComposite(AlphaComposite.getInstance
	 								(AlphaComposite.SRC_OVER, (float)alpha));
	 			}
	 			g.drawPolygon(poly);	//create line round shape
	 		}
	    }
	}


	/** Get Shape Alpha **/
	public double getAlpha() {
		return alpha;
	}

	/** Set Shape Alpha **/
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	/** Get Shape Thickness **/
	public int getThickness() {
		return thickness;
	}

	/** Set Shape Thickness **/
	public void setThickness(int thickness) {
		this.thickness = thickness;
	}

	/** Get Shape Fill_Type **/
	public int getFill_type() {
		return fill_type;
	}

	/** Set Shape Fill_Type **/
	public void setFill_type(int fill_type) {
		this.fill_type = fill_type;
	}

	/** Get Shape X_at_color1 **/
	public int getX_at_color1() {
		return x_at_color1;
	}

	/** Set Shape X_at_color1 **/
	public void setX_at_color1(int xAtColor1) {
		x_at_color1 = xAtColor1;
	}

	/** Get Shape Y_at_color1 **/
	public int getY_at_color1() {
		return y_at_color1;
	}

	/** Set Shape Y_at_color1 **/
	public void setY_at_color1(int yAtColor1) {
		y_at_color1 = yAtColor1;
	}

	/** Get Shape X_at_color2 **/
	public int getX_at_color2() {
		return x_at_color2;
	}

	/** Set Shape X_at_color2 **/
	public void setX_at_color2(int xAtColor2) {
		x_at_color2 = xAtColor2;
	}

	/** Get Shape Y_at_color2 **/
	public int getY_at_color2() {
		return y_at_color2;
	}

	/** Set Shape Y_at_color2 **/
	public void setY_at_color2(int yAtColor2) {
		y_at_color2 = yAtColor2;
	}

	/** Get Shape FillColor **/
	public int getFillColor() {
		return fillColor;
	}

	/** Set Shape FillColor **/
	public void setFillColor(int fillColor) {
		this.fillColor = fillColor;
	}

	/** Get Shape LineColor **/
	public int getLineColor() {
		return lineColor;
	}

	/** Set Shape LineColor **/
	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}

	/** Get Shape Colour1 **/
	public int getColour1() {
		return color1;
	}

	/** Set Shape Colour1 **/
	public void setColor1(int color1) {
		this.color1 = color1;
	}

	/** Get Shape Colour2 **/
	public int getColor2() {
		return color2;
	}

	/** Set Shape Color2 **/
	public void setColor2(int color2) {
		this.color2 = color2;
	}

	/** Set Shape xPath **/
	public void addxPoint(int xcoord) {
		this.xPath.addElement(new Integer(xcoord));
	}

	/** Set Shape yPath **/
	public void addyPoint(int ycoord) {
		this.yPath.addElement(new Integer(ycoord));
	}

	/** Get Shape Is_Cyclic **/
	public Boolean getIs_Cyclic() {
		return is_cyclic;
	}

	/** Set Shape Is_Cyclic **/
	public void setIs_Cyclic(Boolean is_cyclic) {
		this.is_cyclic = is_cyclic;
	}

	/**
	 * Gets x value.
	 *
	 * @return x the integer x.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets x value.
	 *
	 * @param x the integer to set.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Gets y value.
	 *
	 * @return y the integer to return.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets y value.
	 *
	 * @param y the integer to set.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Gets onclick variable.
	 *
	 * @return onclick the integer to return.
	 */
	public int getOnclick() {
		return onclick;
	}

	/**
	 * Sets onclick variable.
	 *
	 * @param onclick the integer to set.
	 */
	public void setOnclick(int onclick) {
		this.onclick = onclick;
	}

	/**
	 * Gets URL.
	 *
	 * @return onclickurl the String containing URL information.
	 */
	public String getOnclickurl() {
		return onclickurl;
	}

	/**
	 * Sets URL.
	 *
	 * @param onclickurl the String to set the URL to.
	 */
	public void setOnclickurl(String onclickurl) {
		this.onclickurl = onclickurl;
	}

	/**
	 * Gets the start time.
	 *
	 * @return starttime the integer to return.
	 */
	public int getStarttime() {
		return starttime;
	}

	/**
	 * Sets the start time.
	 *
	 * @param startTime the integer to set start time to.
	 */
	public void setStarttime(int startTime) {
		this.starttime = startTime;
	}

	/**
	 * Gets the end time.
	 *
	 * @return endtime the integer to return.
	 */
	public int getEndtime() {
		return endtime;
	}

	/**
	 * Sets the end time.
	 *
	 * @param endTime the integer to set end time to.
	 */
	public void setEndtime(int endTime) {
		this.endtime = endTime;
	}

	/**
	 * Sets whether shape is filled or not.
	 *
	 * @param fill the boolean to determine fill.
	 */
	public void setFill(Boolean fill) {
		this.fill = fill;
	}

	/**
	 * Gets fill variable.
	 *
	 * @return fill the boolean to return.
	 */
	public Boolean getFill(){
		return fill;
	}

	/**
	 * Sets original x position.
	 *
	 * @param anX the integer to set original x to.
	 */
	public void setOriginalX(int anX){
		originalX = anX;
	}

	/**
	 * Sets original y position.
	 *
	 * @param anY the integer to set original y to.
	 */
	public void setOriginalY(int anY){
		originalX = anY;
	}

	/**
	 * Sets original thickness.
	 *
	 * @param aThickness is the integer to set it to.
	 */
	public void setOriginalThickness(int aThickness){
		originalThickness = aThickness;
	}

	/**
	 * Gets the x path.
	 *
	 * @return xPath the Vector of integers to return.
	 */
	public Vector<Integer> getXPath(){
		return xPath;
	}

	/**
	 * Gets the y path.
	 *
	 * @return yPath the vector vector of integers to return.
	 */
	public Vector<Integer> getYPath(){
		return yPath;
	}

	/**
	 * Method to resize polygon.
	 */
	public void resize(double scaleFactor){
		shape.setVisible(false);

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
		double initialRelativeScaleFactor =
			((double)newSlideWidth)/originalSlideWidth;

		// Thickness needs to be scaled aswell
		thickness = (int) (originalThickness * initialRelativeScaleFactor);

		// added in to get the resize method to work.
		setX((int) (initialRelativeScaleFactor * originalX));
		setY((int) (initialRelativeScaleFactor * originalY));

		//Remove current path values
		xPath.clear();
		yPath.clear();

		//Scale all original path points by the initial-relative scale factor
		//and repopulate the path vectors with the scaled values
		 int i;
			for(i=0; i<initialPolygon.getShapePath().size();i++)
			{
				xPath.add(i,(int)(initialPolygon.getShapePath().get(i).getX()
									*initialRelativeScaleFactor));
				yPath.add(i,(int)(initialPolygon.getShapePath().get(i).getY()
									*initialRelativeScaleFactor));
			}

			//resize the shape
			shape.setBounds(x, y, getObjectWidth(), getObjectHeight());
			shape.setVisible(true);

			//resize the mouse monitor
			if(shapeLMM != null) { // Stops null pointer exception
				// Scale the clickable area
				if(shapeLMM.retrieveZone("polygon") != null) {
					shapeLMM.reposition(shape.getLocation().x, shape.getLocation().y);
					shapeLMM.rescale(shape.getSize().width, shape.getSize().height);
				}
			}
	}

	//TEST CODE - TO BE REMOVED================================================
	public static void main(String[] args) {
		// Instantiate SafetyNet without producing warnings
		@SuppressWarnings("unused")
		SafetyNet myApp = new SafetyNet();


		XMLPolygon tempPolygon = new XMLPolygon();

		// Create a square

		tempPolygon.getShapePath().add(new XMLPoint());
		tempPolygon.getShapePath().lastElement().setLocation(100, 100);



		tempPolygon.getShapePath().add(new XMLPoint());
		tempPolygon.getShapePath().lastElement().setLocation(200, 100);


		tempPolygon.getShapePath().add(new XMLPoint());
		tempPolygon.getShapePath().lastElement().setLocation(200, 200);


		tempPolygon.getShapePath().add(new XMLPoint());
		tempPolygon.getShapePath().lastElement().setLocation(100, 200);

		tempPolygon.setStartTime(0);
		tempPolygon.setEndTime(0);
		tempPolygon.setColor(Color.red);
		tempPolygon.setAlpha(1);
		tempPolygon.setFill(false);
		tempPolygon.setThickness(5);
		tempPolygon.setOnClick(3);
		tempPolygon.setOnClickUrl("www.google.co.uk");
		tempPolygon.setZOrder(100);

		PolygonPlayer tempPolygonPlayer = new PolygonPlayer(tempPolygon);
		tempPolygonPlayer.display();

		tempPolygonPlayer.resize(2);

		//tempPolygonPlayer.resize(2);


		// Stops unused warning

		// Run scrollPane tests if true

	}
	public JPanel getShape() {
		return shape;
	}

}