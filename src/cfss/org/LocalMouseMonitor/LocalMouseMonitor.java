/*------------------------------------------------------------------------------
 		Crab Factory Software Solutions

	Software:		SafetyNet
	Module:		LocalMouseMonitor

	Author:		Michael Angus

	Contributers: n/a
	Testers:

 Description: A class used for monitoring mouse events in a way that may be
 	adapted by the parent class, to monitor only the desired types of mouse
 	event.
 	The system is based upon defining zones on-screen that the parent has
 	an interest in. If an event type for which the parent has registered an
 	interest occurs in one of these zones, the parent's implementation of the
 	zoneEventOccurred method will be invoked and supplied with information
 	about the type of event and the zone in which it occurred.

 	The zones may be resized and repositioned as necessary to match any scaling
 	or repositioning of the parent component. This may be achieved in a number
 	of different ways as detailed below.

 Associations: Interface MouseMonitorListener must be implemented by any class
 	that wishes to use LocalMouseMonitor
 				Class ZoneEvents holds constants for the various types of mo

 Usage: The LocalMouseMonitor must be instantiated with arguments indicating
 	the class in which the MouseMonitorListener interface is implemented, and
 	the screen area that the mouse monitor is required to watch.

 	The monitored area can be defined by passing a Container, or passing an
 	origin, width and height, depending on usage scenario.

 	Zones are defined relative to this reference area. For example, a zone
 	located at the top-left of the reference area would have coordinates (0,0)

 	ZoneEvents.java contains enumerated constants for the various event types.
 	For example, to check if an event type was a mouse click, this form could
 	be used: if(eventType == ZoneEvents.CLICK) <do something>

 	The zones can be resized and repositioned in various ways to accommodate
 	most usage scenarios.
 	They may be just repositioned, just rescaled, or have a scale factor applied
 	to both positioning and sizing. In all cases it is possible to apply rhe
 	operation in a single direction (horizontal or vertical) or in both
 	directions

 	1)invoke reposition(int,int) passing in the new x,y position of monitored
 		area.
 		Keep one argument the same as original for single-direction shifting

 	2)invoke rescale(int,int) passing in the new width and height of monitored
 		area.
 		Keep one argument the same as original for single-direction scaling.

 	3)invoke applyScaleFactor(double,boolean,boolean) passing in a scale factor
 		and flags indicating whether to scale horizontally, vertically or both.
 		This will scale both position and size of each zone, to match component
 		changes as a result of window resizing, for example

 	4)invoke 'shiftPosition' and/or 'scale' for an individual zone, to perform
 		independent repositioning/resizing of the zone
 		To do this the zone must first be accessed. This is facilitated by the
 		'retrieveZone' method, which returns a zone given the name of the zone
------------------------------------------------------------------------------*/

package cfss.org.LocalMouseMonitor;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Vector;
import javax.swing.JPanel;
import cfss.org.flags.Debug;
import cfss.org.safetynet.gui.Gui;

/**
 * A class used for monitoring mouse events in a way that may be
 	adapted by the parent class, to monitor only the desired types of mouse
 	event, and only if they occur within a specified area of interest - the
 	<i>parent area.</i> <br>
 	This class exists to provide versatility, flexibility and simplicity to
 	mouse handling, allowing implementation of dynamically variable mouse
 	functionality of arbitrary complexity with a small set of simple commands.<p>

 	Any class wishing to utilise the LocalMouseMonitor must implement its
 	partner interface {@link MouseMonitorListener MouseMonitorListener} to
 	respond to event notifications. The various event types used in these
 	notifications are enumerated in {@link ZoneEvents ZoneEvents} for ease of
 	identification<p>

 	The system is based upon defining detection zones on-screen that the parent has
 	an interest in. If an event type for which the parent has registered an
 	interest occurs in one of these zones, the parent's implementation of the
 	{@link MouseMonitorListener#zoneEventOccurred(String, int) zoneEventOccurred}
 	method will be invoked and supplied with information about the type of event
 	and the zone in which it occurred.<br>
 	This is of particular use when required detection regions are not adequately
 	defined by 'active' objects (such as in a custom graphical interface where
 	components are defined by images)<p>

 	The size of the 'parent area' defined should be kept to the minimum required
 	for the particular application. This reduces processing overhead, in particular for
 	mouse movement detection, since events are only triggered when relevant. This
 	reduces the possibility of interference between different areas of mouse
 	functionality.<p>

	'Interest' in particular event types is achieved by invocation of the
	{@link #setNotifications(Boolean, Boolean, Boolean, Boolean, Boolean, Boolean) setNotifications}
	method. This may be re-invoked at any point to accommodate changing
	requirements.<p>

	Zones are defined through use of the
	{@link #addZone(String, Point, Dimension) addZone} method, and are
	identified by a textual 'name' for unambiguous, human-oriented identification.<br>
	Any number of zones may be added within the boundaries of the parent area.
	Composite zones may be created by assigning the same name to multiple zones,
	allowing definition of any zone shape that may be expressed as a combination
	 of rectangles.<br>
	It should be noted that zones are defined relative to the parent area, i.e.
	for zone positioning purposes, (0,0) is the top-left corner of the parent
	area<p>

	Zones may be removed at any point through the use of
	{@link #removeZone(String) removeZone}<br>
 	The parent area and its zones may be resized and/or repositioned as
 	necessary, either all at once or on a zone-specific basis, horizontally
 	and/or vertically. To apply this to all elements of the mouse monitor, the
 	methods {@link #applyScaleFactor(double, Boolean, Boolean) applyScaleFactor},
 	{@link #reposition(int, int) reposition}, or
 	{@link #rescale(int, int) rescale} may be used.<br>

 	For zone-specific modification, a zone may be retrieved by name using
 	{@link #retrieveZone(String) retrieveZone}, (which returns a {@link Zone Zone}
 	object) and altered through use of
 	{@link Zone#shiftPosition(int, int) shiftPosition},
 	{@link Zone#scaleDimensions(int, int) scaleDimensions} or
 	{@link Zone#scale(double, double) scale}<p>

 	@see LocalMouseMonitor.Zone

 *
 * @author Michael Angus
 * */

@SuppressWarnings("serial")
public class LocalMouseMonitor extends JPanel implements MouseListener {


	//Flags to indicate which events the parent is interested in
	private Boolean monitorClick = false;
	private Boolean monitorPress = false;
	private Boolean monitorRelease = false;
	private Boolean monitorEnter = false;
	private Boolean monitorExit = false;
	private Boolean monitorMovement = false;

	//Flag to indicate that the mouse is currently being dragged
	private Boolean mouseIsDragging = false;

	//Parent origin and dimensions (the invisible panel which defines the
	//area being monitored by the LocalMouseMonitor)
	private Point origin;
	private Dimension dimensions;

	//Vector of defined zone areas
	private Vector <Zone> zones;

	//Implementation of MouseMonitorListener interface which will respond to
	//zone events from LocalMouseMonitor
	private MouseMonitorListener listener;

	//The instantiation of the motion tracker inner class
	private MotionTracker movementMonitor;
	private JPanel mouseMonitorPanel;

	/**
	 *Alternative constructor, supporting the case where the area of interest
	 *is defined by a single container.
	 *This derives an origin and dimensions from the container and calls the
	 *main constructor
	 *@param parent this is a reference to the object which implements the
	 *MouseMonitorListener interface
	 *@param parentArea container defining the area in which all the zones to
	 *be monitored are enclosed (the aforementioned 'area of interest')
	 **/
	public LocalMouseMonitor(MouseMonitorListener parent, Container parentArea)
	{
		//Derive the container origin and dimensions to pass to main
		//constructor. 'this' calls the main LocalMouseMonitor constructor below
		//passing it the arguments derived
		this(parent, new Point(parentArea.getX(), parentArea.getY()),
				new Dimension(parentArea.getWidth(), parentArea.getHeight()));
	}

	/**
	 * Main constructor. This sets up the mouse monitor area as the area of
	 * interest and instantiates the mouse listener.
	 * This constructor may be used to define any arbitrary rectangular area,
	 * and should be used if the area is not completely defined by a single
	 * Container.
	 * NOTE: This works by creating an invisible JPanel. If any mouse detection
	 * area is overlapped by the defined area of interest, the overlapped area
	 * will not be able to detect mouse events
	 *
	 * @param eventTarget this is a reference to the object which implements the
	 *MouseMonitorListener interface
	 *
	 * @param parentOrigin a Point (x,y) representing the top-left corner of the
	 * area of interest
	 *
	 * @param parentDimensions a Dimension(width,height) defining the width and
	 * height of the area of interest
	 **/
	public LocalMouseMonitor(MouseMonitorListener eventTarget, Point parentOrigin,
			Dimension parentDimensions)
{
		this(eventTarget, parentOrigin,parentDimensions,Gui.getMouseMonitorPanel());

}
	/**
	 * Alternative Constructor.
	 *
	 * Allows the default panel that the MouseMonitor will be added to be
	 * overridden.  By default the Mouse monitor adds itself to:
	 * <code>Gui.getMouseMonitorPanel();</code>
	 * This sets up the mouse monitor area as the area of
	 * interest and instantiates the mouse listener.
	 * This constructor may be used to define any arbitrary rectangular area,
	 * and should be used if the area is not completely defined by a single
	 * Container.
	 * NOTE: This works by creating an invisible JPanel. If any mouse detection
	 * area is overlapped by the defined area of interest, the overlapped area
	 * will not be able to detect mouse events
	 *
	 * @param eventTarget this is a reference to the object which implements the
	 *MouseMonitorListener interface
	 *
	 * @param parentOrigin a Point (x,y) representing the top-left corner of the
	 * area of interest
	 *
	 * @param parentDimensions a Dimension(width,height) defining the width and
	 * height of the area of interest
	 *
	 * @param hostPanel - a JPanel that the mouse Monitor will add
	 * itself to. The host defines the coordinate space to be used - usually the
	 * slide area, but the entire content pane for GUI components. The panel
	 * given here must be elevated above anything which may obstruct mouse
	 * events from reaching the mouse monitor; generally accomplished using a 
	 * JLayeredPane.
	 **/
	public LocalMouseMonitor(MouseMonitorListener eventTarget, Point parentOrigin,
							Dimension parentDimensions,JPanel hostPanel)
	{
		//Create a panel to define the parent area
		super();

		//store the reference to the passed in panel that will contain the mouse Monitor
		mouseMonitorPanel = hostPanel;
		
		//if debug is enabled, make the parent area visible (green)
		if(Debug.localMouse)
		{
			setOpaque(true);
			setBackground(new Color(0,255,0,100));
		}
		//Otherwise, the parent area must be invisible
		else
		{
			setOpaque(false);
		}

		//add the mouseMonitor to the panel
		mouseMonitorPanel.add(this);

		//Match size and positioning indicated by arguments
		origin = new Point();
		origin = parentOrigin;
		dimensions = new Dimension();
		dimensions = parentDimensions;

		setLocation(origin);
		setSize(dimensions);

		//Debug message to verify correct size/position
		if(Debug.localMouse) System.out.println("LocalMouseMonitor created to watch" +
				" region at: "+getX()+","+getY()+" of size: "+
						getWidth()+"x"+getHeight());

		//Add mouse listener (for mouse button events)
		addMouseListener(this);

		//Register the parent as the listener for LocalMouseMonitor
		listener = eventTarget;

	}

	/**
	 *
	 * Zone class which contains the properties of a zone (name, position and
	 * dimensions), providing methods for defining and modifying zones.
	 * An instance of this class is created by
	 * {@link cfss.org.LocalMouseMonitor.LocalMouseMonitor#addZone(String, Point, Dimension) addZone}
	 *
	 * An individual zone can be modified by invoking the method
	 * {@link cfss.org.LocalMouseMonitor.LocalMouseMonitor#retrieveZone(String)}
	 * in LocalMouseMonitor and then using the methods provided by the Zone
	 * class to reposition and/or resize the zone
	 *
	 * @author Michael Angus
	 *
	 */
	public class Zone
	{
		/**Zone name*/
		String name;

		/**x coordinate of zone origin (top-left corner)*/
		int x;

		/**y coordinate of zone origin (top-left corner)*/
		int y;

		/**Zone width*/
		int width;
		/**Zone height*/
		int height;

		/**
		Vector defining zone boundaries (edges).
		Stored in this order: left edge, right edge, top edge, bottom edge
		*/
		Vector <Integer> zoneBounds;

		/**
		*Zone constructor, initialises all Zone fields and creates a boundary
		*vector, so that the zone is ready for use
		*
		*@param zoneName the name of the zone. This is how zones are identified;
		*the reference by which zones are accessed, and is also the identifier
		*passed to the listener-implementing class when an event occurs
		*@see MouseMonitorListener#zoneEventOccurred(String, int)
		*
		*@param zoneOrigin	The top-left corner of the zone
		*
		*@param zoneDimensions The width and height of the zone
		*/
		public Zone(String zoneName, Point zoneOrigin, Dimension zoneDimensions)
		{
			//Initialise fields from given arguments
			name = zoneName;
			x = zoneOrigin.x;
			y = zoneOrigin.y;
			width = zoneDimensions.width;
			height = zoneDimensions.height;

			//Populate the boundary vector
			defineBoundaries(x, y, width, height);
		}

		/**This method allows shifting of individual zones, for the case where
		*a zone must be independently repositioned
		*
		*@param newX The required new X position of the zone
		*@param newY The required new Y position of the zone
		*/
		public void shiftPosition(int newX, int newY)
		{
			//Update positioning fields for the zone
			x = newX;
			y = newY;

			//Redefine boundaries with new information
			defineBoundaries(x, y, width, height);
		}

		/**This method allows independent scaling of zone dimensions only, for
		*the case where the zone must change size but not position, such as
		*when there is only one zone
		*
		*@param newWidth The required new width of the zone
		*@param newHeight The required new height of the zone
		*/
		public void scaleDimensions(int newWidth, int newHeight)
		{
			//Determine scaled width and height
			width = newWidth;
			height = newHeight;

			//Redefine boundaries with new dimensions
			defineBoundaries(x, y, width, height);
		}

		/**This method allows independent scaling (size and position) of a zone.
		*This is used for all zones by the LocalMouseMonitor resizing methods
		*
		*@param scaleFactorX The scale factor to be applied in the horizontal
		*direction. To scale vertically only, set this to 1.0
		*
		*@param scaleFactorY The scale factor to be applied in the vertical
		*direction. To scale horizontally only, set this to 1.0
		*/
		public void scale(double scaleFactorX, double scaleFactorY)
		{
			//Determine scaled width and height
			width = (int)(scaleFactorX*width);
			height = (int)(scaleFactorY*height);

			//Determine scaled position
			x = (int)(scaleFactorX*x);
			y = (int)(scaleFactorY*y);

			//Redefine boundaries with new information
			defineBoundaries(x, y, width, height);
		}

		//Given the position and size given in the arguments, this generates a
		//boundary vector representing the edges of the region
		private void defineBoundaries(int x, int y, int width, int height)
		{
			//Derive edges from given origin and dimensions
			int leftEdge = x;
			int rightEdge = x + width;
			int topEdge = y;
			int bottomEdge = y + height;

			//Create a 4-element vector of integers and populate with edges
			zoneBounds = new Vector<Integer>(4);

			zoneBounds.add(0,leftEdge);
			zoneBounds.add(1,rightEdge);
			zoneBounds.add(2,topEdge);
			zoneBounds.add(3,bottomEdge);
		}
	}

	/**Called by the user to indicate which types of mouse event they are
	*interested in. If a flag is 'false', its associated event type will be
	*ignored.
	*
	*@param notifyClick	for when click events (rapid mouse press and release)
	*are to be monitored
	*
	*@param notifyPress for when mouse button press events are to be monitored
	*
	*@param notifyRelease for when mouse button release events are to be
	*monitored
	*
	*@param notifyEnter If this is set, an Enter event will be triggered when
	*the mouse cursor enters the boundaries of the parent area, if there is also
	*a zone defined at the entry point
	*
	*@param notifyExit Notify of parent area exiting. The same rules apply as
	*with the <code>notifyEnter</code> flag
	*
	*@param trackMovement Notify of movement and dragging. Movement in a zone
	*will trigger the listener method
	*{@link MouseMonitorListener#zoneEventOccurred(String, int) zoneEventOccurred}
	*with the event type as {@link ZoneEvents#MOVEMENT MOVEMENT}. Any mouse dragging
	*action will trigger the listener method
	*{@link MouseMonitorListener#dragEventOccurred(String, int, int, Boolean) dragEventOccurred}
	*
	*/
	public void setNotifications(Boolean notifyClick, Boolean notifyPress,
			Boolean notifyRelease, Boolean notifyEnter, Boolean notifyExit,
				Boolean trackMovement)
	{
		monitorClick = notifyClick;
		monitorPress = notifyPress;
		monitorRelease = notifyRelease;
		monitorEnter = notifyEnter;
		monitorExit = notifyExit;
		monitorMovement = trackMovement;

		//If movement is to be monitored, add the motion listener object
		if(trackMovement)
		{
			movementMonitor = new MotionTracker();
			addMouseMotionListener(movementMonitor);
		}
	}

	/**Invoked by the object that is using LocalMouseMonitor, used to add a new
	*zone for which to monitor mouse events
	*
	*@param name A string which will be used to identify the zone. This
	*	identifier is what is used to access the zone using
	*	{@link #retrieveZone(String) retrieveZone}, and is passed to the listener
	*	methods when an event occurs in that zone.
	*
	*@param origin The coordinates of the top-left corner of the zone
	*@param dimensions The width and height of the zone
	*/
	public void addZone(String name, Point origin, Dimension dimensions)
	{
		//Initialise the zone vector if not yet created
		if(null == zones)
			zones = new Vector <Zone>(1);

		//Create a zone object from the given dimensions
		Zone newZone = new Zone(name, origin, dimensions);

		//Add this zone to the zone vector
		zones.add(newZone);
	}

	/**
	 * This removes the zone with the specified name from the LocalMouseMonitor
	 * The zone is removed from the 'zones' vector and is hence no longer
	 * reference and eligible for garbage collection.
	 *
	 * @param zoneName The name of the zone to be removed
	 */
	public void removeZone(String zoneName)
	{
		//Intermediate zone variable for use when iterating
		Zone currentZone;

		//The index of the zone to be removed. Initialised at 999 so that if
		//no zone match is found, there is no risk of inadvertently removing
		//an unintended zone (as would be the case if initialised to a low
		//number)
		int zoneToRemove = 999;

		//Iterate through zones vector, comparing the given zoneName with the
		//current zone name. When a match is found, the loop is broken and the
		//index of the zone to be removed is defined
		for(int i=0; i<zones.size();i++)
		{
			currentZone = zones.get(i);
			if(currentZone.name == zoneName)
			{
				zoneToRemove = i;
				break;
			}
		}

		//If a matching zone was found, remove the zone.
		if(zoneToRemove != 999)
		{
			zones.remove(zoneToRemove);
			zones.trimToSize();
		}
		else{
			if(Debug.localMouse)
				System.out.println("Zone of name '"+zoneName+"' could not be " +
						"removed - name not found");
		}
	}

	/**This method allows access to an individual zone, so as to directly modify
	*its properties or to call its {@link Zone#scale(double, double) scale} or
	*{@link Zone#shiftPosition(int, int) shiftPosition} methods. <br>
	*The method prints an error message and returns <code>null</code> if the
	*zoneName given does not match any of the zone names in the zones vector
	*/
	public Zone retrieveZone(String zoneName)
	{
		if(zones != null)
		{
			//Iterate through zones, returning the zone with name specified by
			//zoneName. Once a zone is returned the method terminates
			for(int i=0; i< zones.size(); i++)
			{
				if(zones.get(i).name == zoneName)
					return zones.get(i);
			}
		}

		//This is only executed if no zone is found
		if(Debug.localMouse)
			System.out.println("No zone of that name found");
		return null;
	}

	/**This method allows the user to reposition all zones.
	 *
	 * @param newX X coordinate of the top-left corner of the parent area (the
	 * area in which all the zones are defined)
	*  @param newY Y coordinate of the parent area
	*/
	public void reposition(int newX, int newY)
	{
		//Update origin position
		origin.x = newX;
		origin.y = newY;

		//Reposition monitored area
		setLocation(origin);
		//Debug message to verify rescaling
		if(Debug.localMouse) System.out.println(
				"All Zones repositioned to "+origin.x+
					" x "+origin.y);

	}

	/**This method allows the user to resize all zones, without scaling position
	*
	*@param newWidth the desired new width of the parent area
	*@param newHeight the desired new height of the parent area
	*/
	public void rescale(int newWidth, int newHeight)
	{
		//Variable to hold reference to current zone
		Zone currentZone;

		//Determine horizontal and vertical scale factors
		double scaleFactorX = (double)newWidth/dimensions.width;
		double scaleFactorY = (double)newHeight/dimensions.height;

		//Update size of monitored area
		dimensions.width = newWidth;
		dimensions.height = newHeight;

		//Resize monitored area
		setSize(dimensions);

		//Resize all zones
		for(int i=0; i < zones.size(); i++)
		{
			currentZone = zones.get(i);
			currentZone.scale(scaleFactorX,scaleFactorY);
			zones.set(i, currentZone);

			//Debug message to verify rescaling
			if(Debug.localMouse) System.out.println(
					"Zone "+currentZone.name+" scaled to "+currentZone.width+
						" x "+currentZone.height);
		}

	}

	/**
	 * This applies a scale factor to all zones, modifying both size and position
	 *
	 * @param scaleFactor the scale factor to apply
	 *
	 * @param scaleHorizontally flag to indicate that the scale factor is to be
	 * 	applied in the horizontal direction. Set as <code>false</code> for
	 * 	vertical-only scaling
	 *
	 * @param scaleVertically flag to indicate that the scale factor is to be
	 * 	applied in the vertical direction. Set as <code>false</code> for
	 * 	horizontal-only scaling
	 */
	public void applyScaleFactor(double scaleFactor, Boolean scaleHorizontally,
									Boolean scaleVertically)
	{
		int newX, newY;
		int newWidth, newHeight;

		//If horizontal scaling is required, calculate resultant new x and width
		if(scaleHorizontally)
		{
			newX = (int)((double)(origin.x*scaleFactor));
			newWidth = (int)((double)(dimensions.width*scaleFactor));

			//Reposition and rescale in horizontal direction only
			reposition(newX, origin.y);
			rescale(newWidth, dimensions.height);
		}

		//If vertical scaling is required, calculate resultant new y and height
		if(scaleVertically)
		{
			newY = (int)((double)(origin.y*scaleFactor));
			newHeight = (int)((double)(dimensions.height*scaleFactor));

			//Reposition and rescale in vertical direction only
			reposition(origin.x, newY);
			rescale(dimensions.width, newHeight);
		}
	}
	
	/**
	 * Informs the listener that a click event has occurred, passing the name
	 * of the zone in which the event occurred and the event type
	 * {@link ZoneEvents#CLICK CLICK}<br>
	 * No action is performed if the event did not occur in any of the
	 * registered zones<br>
	 * The listener is only informed if the notifyClick flag has been set by
	 * {@link #setNotifications(Boolean, Boolean, Boolean, Boolean, Boolean, Boolean) setNotifications}
	 */
	public void mouseClicked(MouseEvent me)
	{
		if(Debug.localMouse) System.out.println("Mouse event occurred at: "+
				me.getX()+","+me.getY());

		if(monitorClick)
		{
			Zone activeZone = checkActiveZone(new Point(me.getX(),me.getY()));
			if(null != activeZone)
				listener.zoneEventOccurred(activeZone.name, ZoneEvents.CLICK);
		}
	}

	/**
	 * Informs the listener that a press event has occurred, passing the name
	 * of the zone in which the event occurred and the event type
	 * {@link ZoneEvents#PRESS PRESS}<br>
	 * No action is performed if the event did not occur in any of the
	 * registered zones<br>
	 * The listener is only informed if the notifyPress flag has been set by
	 * {@link #setNotifications(Boolean, Boolean, Boolean, Boolean, Boolean, Boolean) setNotifications}
	 */
	public void mousePressed(MouseEvent me)
	{
		if(monitorPress)
		{
			Zone activeZone = checkActiveZone(new Point(me.getX(),me.getY()));
			if(null != activeZone)
				listener.zoneEventOccurred(activeZone.name, ZoneEvents.PRESS);
		}

		//If drag events are to be monitored, the point at which the mouse is
		//first pressed to start the drag motion must be used to check which
		//zone the operation started in. If there is no zone defined at this
		//point, drag functionality is assumed to not be required for this event
		//since the area is not prepared for event handling.
		if(monitorMovement)
		{
			Zone activeZone = checkActiveZone(new Point(me.getX(),me.getY()));
			
			//Set the defined start zone for the potential drag event only if
			//the relevant zone exists
			if(null != activeZone)
				movementMonitor.startZone = activeZone;
		}		
	}

	/**
	 * Informs the listener that a release event has occurred, passing the name
	 * of the zone in which the event occurred and the event type
	 * {@link ZoneEvents#RELEASE RELEASE}<br>
	 * No action is performed if the event did not occur in any of the
	 * registered zones<br>
	 * The listener is only informed if the <code>notifyRelease</code> flag has been set by
	 * {@link #setNotifications(Boolean, Boolean, Boolean, Boolean, Boolean, Boolean) setNotifications}<p>
	 * If a mouse drag event is in progress, this calls
	 * {@link MouseMonitorListener#dragEventOccurred(String, int, int, Boolean) dragEventOccurred}
	 */
	public void mouseReleased(MouseEvent me)
	{
		if(monitorRelease)
		{
			Zone activeZone = checkActiveZone(new Point(me.getX(),me.getY()));
			if(null != activeZone)
				listener.zoneEventOccurred(activeZone.name, ZoneEvents.RELEASE);
		}

		//If drag events are being monitored, the mouse release will need to be
		//known to signify the end of dragging, even if mouse release events are
		//not in general being monitored
		if(mouseIsDragging)
		{
			//Inform the listener of the final drag event (drag operation end)
			//passing 'true' in the actionComplete argument (indicating finish)
			listener.dragEventOccurred(movementMonitor.startZone.name, me.getX(),
					me.getY(), true);

			//Reset the drag-in-progress flag and the drag start zone, ready for
			//future new drag operations
			mouseIsDragging = false;
			movementMonitor.startZone = null;

		}
	}

	/**
	 * Informs the listener that an Enter event has occurred, passing the name
	 * of the zone in which the event occurred and the event type
	 * {@link ZoneEvents#ENTER ENTER}<br>
	 * No action is performed if the event did not occur in any of the
	 * registered zones<br>
	 * The listener is only informed if the notifyEnter flag has been set by
	 * {@link #setNotifications(Boolean, Boolean, Boolean, Boolean, Boolean, Boolean) setNotifications}<br>
	 * Some constraints apply to the detectability of this event type - see
	 * {@link ZoneEvents ZoneEvents} for more details
	 */
	public void mouseEntered(MouseEvent me)
	{
		if(monitorEnter)
		{
			Zone activeZone = checkActiveZone(new Point(me.getX(),me.getY()));
			if(null != activeZone)
				listener.zoneEventOccurred(activeZone.name, ZoneEvents.ENTER);
		}
	}

	/**
	 * Informs the listener that an exit event has occurred, passing the name
	 * of the zone in which the event occurred and the event type
	 * {@link ZoneEvents#EXIT EXIT}<br>
	 * No action is performed if the event did not occur in any of the
	 * registered zones<br>
	 * The listener is only informed if the notifyExit flag has been set by
	 * {@link #setNotifications(Boolean, Boolean, Boolean, Boolean, Boolean, Boolean) setNotifications}<br>
	 * Some constraints apply to the detectability of this event type - see
	 * {@link ZoneEvents ZoneEvents} for more details
	 */
	public void mouseExited(MouseEvent me)
	{
		if(monitorExit)
		{
			Zone activeZone = checkActiveZone(new Point(me.getX(),me.getY()));
			if(null != activeZone)
				listener.zoneEventOccurred(activeZone.name, ZoneEvents.EXIT);
		}
	}

	/**
	 * Method to disable the LocalMouseMonitor. All zone information is deleted,
	 * mouse listeners removed, the internal reference to the parent class
	 * deleted, the parent area removed and <code>finalize()</code> called.
	 */
	public void destroy()
	{
		try 
		{
			//Stop listening for events and get rid of stored zones
			removeMouseListener(this);
			zones.clear();

			//Stop listening for motion and/or drag events if currently doing so
			if(movementMonitor != null)
				removeMouseMotionListener(movementMonitor);

			//Disconnect from listening object, detach from mouse monitor panel
			//and finalize the remaining parent area panel
			listener = null;
			mouseMonitorPanel.remove(this);

		}
		//If something goes wrong in mouse monitor destruction, print error
		//message and associated stack trace (if debugging enabled)
		catch (Throwable e) 
		{
			if(Debug.localMouse){
				System.out.println("LocalMouseMonitor could not be destroyed");
			e.printStackTrace();
			}
		}
	}

	//This class is for handling mouse drag events, It is only instantiated if
	//the 'monitorMovement' flag is set
	private class MotionTracker extends MouseMotionAdapter
	{
		//Variable to indicate which zone the drag event began in, and a flag
		//indicating whether the drag operation is complete
		Zone startZone;

		//Event handling for mouse drag events. The listener is informed of the
		//drag event, the zone in which the drag began and the current X,Y
		//positioning of the mouse
		public void mouseDragged(MouseEvent me)
		{
			//Dragging can only occur if the drag event has been started by a 
			//mouse press within a zone. If not, the 'startZone' will be null.
			if(startZone != null)
			{
				mouseIsDragging = true;
				listener.dragEventOccurred(startZone.name, me.getX(),
													me.getY(), false);
			}
		}

		public void mouseMoved(MouseEvent me)
		{
			if(monitorMovement)
			{
				Zone activeZone = checkActiveZone(new Point(me.getX(),me.getY()));
				
				if(activeZone != null)
					listener.zoneEventOccurred(activeZone.name,
							ZoneEvents.MOVEMENT);

			}
		}
	}

	//This method iterates through the zones, checking whether or not the point
	//'eventLocation' lies within any of their boundaries. If the click is found
	//to be within the boundaries of a zone, that zone is returned
	private Zone checkActiveZone(Point eventLocation)
	{
		//Zone that event location is currently being checked against
		Zone currentZone = null;

		//Concise form of the event location
		int x = eventLocation.x;
		int y = eventLocation.y;

		//Flag which is set if event is found to be within a zone
		Boolean withinZone = false;

		//Look at each zone in turn, checking if the event occurred within its
		//boundaries. Once one is found, stop looking and return that zone
		for(int i=0; i< zones.size(); i++)
		{
			currentZone = zones.get(i);

			//Check if between left and right edges
			Boolean xTrue = ((x >= currentZone.zoneBounds.get(0)) &&
								(x <= currentZone.zoneBounds.get(1)));

			//Check if between top and bottom edges
			Boolean yTrue = ((y >= currentZone.zoneBounds.get(2)) &&
					(y <= currentZone.zoneBounds.get(3)));

			if(xTrue && yTrue)
			{
				withinZone = true;
				break;
			}
		}

		//If event was found to be within a zone, return that zone
		//If no relevant zone found, return null
		if(withinZone)
			return currentZone;
		else
			return null;

	}
}
