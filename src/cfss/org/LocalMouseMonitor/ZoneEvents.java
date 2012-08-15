/*------------------------------------------------------------------------------
 		Crab Factory Software Solutions
 		
 	Software:		SafetyNet
 	Module:			ZoneEvents (for LocalMouseMonitor)
 	
 	Author:			Michael Angus
 	
 	Contributors:	n/a
 	Testers:		
 	
 	Description:	This is a partner class for the LocalMouseMonitor, providing
 		enumerated constants for mouse event types, for ease of use by
 		programmers using the LocalMouseMonitor.
 		For example, to check if event is a click the following expression could
 		be used: if(int eventType == ZoneEvents.CLICK)
 		This is equivalent to (if int eventType == 0), but clearer.
 	
 -----------------------------------------------------------------------------*/

package cfss.org.LocalMouseMonitor;

/**
 * This is a partner class for the LocalMouseMonitor, providing
 *	enumerated constants for mouse event types, for ease of use by
 *	programmers using the LocalMouseMonitor.<p>
 *
 *	For example, to check if event is a click the following expression could
 *	be used: <code>if(eventType == ZoneEvents.CLICK)</code><br>
 *	This is equivalent to: <code>(if eventType == 0)</code>, but clearer.
 * 
 * 
 * @author Michael Angus
 *
 */

public final class ZoneEvents {
	
	/**
	 * Event type for mouse click (button press and immediate release)
	 */
	public static final int CLICK = 0;
	
	/**
	 * Event type for mouse press (button press without immediate release)
	 */
	public static final int PRESS = 1;
	
	/**
	 * Event type for mouse release (button released from pressed state)
	 */
	public static final int RELEASE = 2;
	
	/**
	 * Event type for cursor movement into parent area (if into a zone within
	 * that area). This does not detect movement from one zone to another. For
	 * this, {@link #MOVEMENT MOVEMENT} events should be used. <br>
	 * 
	 * It should be noted that the detection of this event is not 100% reliable 
	 * and is sometimes not triggered if the mouse is moving rapidly <br>
	 * 
	 * A more reliable method (though with greater processing overhead) is to
	 * detect {@link #MOVEMENT MOVEMENT} events, since the region of detection
	 * is generally larger than the edge of the parent area
	 */
	public static final int ENTER = 3;
	
	/**
	 * Event type for cursor movement out of parent area, (if exit point is also
	 * within a zone). This does not detect movement from one zone to another.
	 * For this, {@link #MOVEMENT MOVEMENT} events should be used. <br>
	 * 
	 * As with {@link #ENTER ENTER}, this event is not reliably detected. The
	 * detection success rate is noticeably lower in this case. Detection 
	 * improves with slower mouse movements <br>
	 * 
	 * A suggested workaround if rapid motion is unavoidable is to increase the
	 * size of the parent area to allow a margin around the main zones, and 
	 * define an exit zone (or zones) within this boundary, detecting either
	 * {@link #ENTER ENTER} or {@link #MOVEMENT MOVEMENT}
	 */
	public static final int EXIT = 4;
	
	/**
	 * Event type for cursor movement within a zone. Zone-to-zone transitions
	 * may be detected using this type, by looking for changes in its associated
	 * zone between successive events.
	 */
	public static final int MOVEMENT = 5;
	
}
