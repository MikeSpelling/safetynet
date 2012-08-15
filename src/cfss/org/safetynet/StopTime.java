package cfss.org.safetynet;

/************************************************
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		StopTime
 *
 *  Author: 		Philip Day
 *
 *  Contributers:	.
 *  Testers:		.
 *
 *  Description:	Stores time information.
 *
 ***********************************************/

/**
 * Stores timing information.
 *
 * @author Philip Day
 */
public class StopTime {
	private long timeStopped = 0;

	/**
	 * Unused method.
	 */
	public StopTime(){
	}

	/**
	 * Set stop time.
	 *
	 * @param aStopTime the long to set stop time to.
	 */
	public void setTimeStopped(long aStopTime ) {
		timeStopped = aStopTime;
	}

	/**
	 * Gets stop time.
	 * @return timeStopped the long to return.
	 */
	public long getTimeStopped() {
		return timeStopped;
	}
}
