/************************************************
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		Entity
 *
 *  Author: 		Philip Day
 *
 *  Contributers:	.
 *  Testers:		.
 *
 *  Description:	Class which is the basis for all media players.
 *
 ***********************************************/

package cfss.org.safetynet;

/**
 * Class which includes methods used for all media players.
 *
 * @author Philip Day
 */
public class Entity {
	private boolean isActive;
	private long startTime;
	private long endTime;
	private int zOrder; // default

	/**
	 * Blank constructor.
	 */
	public Entity(){
	}

	/**
	 * Places the entity on screen.  No functionality here, only for inheritance purposes.
	 */
	public void display(){

	}

	/**
	 * Removes the entity from screen.  No functionality here, only for inheritance purposes.
	 */
	public void unDisplay(){

	}

	/**
	 * Resizes the entity according to the scaleFactor.  No functionality here, only for inheritance purposes.
	 */
	public void resize(double scaleFactor){

	}

	/**
	 * Returns the time on the current slide that this entity should appear.
	 * No functionality here, only for inheritance purposes.
	 *
	 * @return startTime is the long to return.
	 */
	public long getStartTime()
	{
		return startTime;
	}

	/**
	 * Returns the time on the current slide that this entity should be removed.
	 * No functionality here, only for inheritance purposes.
	 *
	 * @return endTime is the long to return.
	 */
	public long getEndTime()
	{
		return endTime;
	}

	/**
	 * Returns a boolean to indicate if the entity is currently displayed.
	 *
	 * @return isActive is the boolean to return.
	 */
	public boolean getIsActive()
	{
		return isActive;
	}

	/**
	 * Sets a boolean value to indicate if the entity is currently displayed.
	 * @param anIsActive
	 */
	public void setIsActive(boolean anIsActive)
	{
		isActive = anIsActive;
	}

	/**
	 * Sets the time on the current slide that the entity should be displayed.
	 * @param anStartTime
	 */
	public void setStartTime(long anStartTime)
	{
		startTime = anStartTime;
	}

	/**
	 * Sets the time on the current slide that the entity should be removed.
	 * @param anEndTime
	 */
	public void setEndTime(long anEndTime)
	{
		endTime = anEndTime;
	}

	/**
	 * This sets the z order value.
	 *
	 * @param aZOrder
	 * 			value the z order is to be set
	 */
	public void setZOrder(int aZOrder)
	{
		zOrder = aZOrder;
	}

	/**
	 * This returns the value of the z order.
	 * @return zOrder
	 */
	public int getZOrder()
	{
		return zOrder;
	}
}