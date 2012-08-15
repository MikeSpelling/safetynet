/*------------------------------------------------------------------------------
 		Crab Factory Software Solutions
 		
 	Software:		SafetyNet
 	Module:			MouseMonitorListener (for LocalMouseMonitor)
 	
 	Author:			Michael Angus
 	
 	Contributors:	n/a
 	Testers:		
 	
 	Description:	This is the partner interface for the LocalMouseMonitor
 		Any class wishing to use the LocalMouseMonitor must implement this
 		interface. The LocalMouseMonitor calls these methods when mouse events 
 		occur, allowing the implementing class to respond to relevant events.
 	
 -----------------------------------------------------------------------------*/

package cfss.org.LocalMouseMonitor;
/**
 * Companion interface for {@link LocalMouseMonitor LocalMouseMonitor}, 
 * mandating methods for responding to zone events detected by the mouse monitor
 * 
 * The object which implements this interface must be passed as the
 * <code>eventTarget</code> argument of the LocalMouseMonitor constructor
 * 
 * @author Michael Angus
 *
 */
public interface MouseMonitorListener {

	/**
	 * Method invoked by the LocalMouseMonitor when a mouse event type occurs
	 * for which the parent class has registered interest using
	 * {@link LocalMouseMonitor#setNotifications(Boolean, Boolean, Boolean, Boolean, Boolean, Boolean) setNotifications}.
	 * 
	 *  This method is only called if the event occurred in one of the zones
	 *  defined by the parent using {@link LocalMouseMonitor#addZone(String, java.awt.Point, java.awt.Dimension) addZone}.
	 * 
	 * @param eventZone The string identifier of the zone in which the event
	 * occurred
	 * @param eventType The event type as enumerated in 
	 * {@link ZoneEvents ZoneEvents}
	 */
	public void zoneEventOccurred(String eventZone, int eventType);
	
	/**
	 * Method invoked by the LocalMouseMonitor when the start, end or
	 * progression of a drag event is detected.
	 * 
	 * This method is first invoked at the start of a drag event. With each 
	 * detected update to the dragging movement, the method is re-invoked. The
	 * final invocation for a given drag operation occurs when the mouse button
	 * is released.
	 * 
	 * @param startZone The string identifier assigned to the zone in which the 
	 * drag event was started. 
	 * 
	 * @param mouseX The current horizontal position of the mouse
	 * @param mouseY The current vertical position of the mouse
	 * 
	 * @param dragComplete A flag which is <code>false</code> until the mouse 
	 * button is released to end the drag operation, at which point it is set to
	 * <code>true</code>
	 */
	public void dragEventOccurred(String startZone, int mouseX, int mouseY, 
														Boolean dragComplete);
}
