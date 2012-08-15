/**
 * 		Crab Factory Software Solutions
 *
 * 		Software: SafetyNet
 * 		Module: TreeEntity
 *
 * 		Author: Mark Mellar
 *
 * 		Contributers: Callum Goddard
 * 		Testers:
 *
 * 		Description:
 * 		Stores information pertaining to a tree entity, in the slideshow
 * 		(Slide and QuizSlide and inherit from this)
 *
 */

package cfss.org.xmlstructure;

/**
 * Stores information pertaining to a tree entity, in the slideshow
 * (Slide and QuizSlide and inherit from this)
 * @author Mark Mellar
 *
 */
public abstract class TreeEntity {

	/**
	 * the heading to be displayed for this slide/section in the sidePane
	 */
	private String heading;
	/**
	 * the unique id of this slide/section
	 */
	private int id;
	/**
	 * the unique id of the slide to go to after this
	 * 0 disables next slide, -1 indicates end of presentation
	 */
	private int next;
	/**
	 * Set to true if the slide represents a section header
	 */
	private boolean isSection;
	/**
	 * the path to the image which provides a preview of this slide
	 */
	private String prevPath;

	/**
	 *  constructor
	 */
	public TreeEntity(){
		isSection = false;
	}

	/**
	 * copy constructor
	 * @param def the TreeEntity to be copied
	 */
	public TreeEntity(TreeEntity def){
		this.id = def.getId();
		this.next = def.getNext();
		//TODO:are headings in xml?
		this.heading = def.getHeading();
	}

	/**
	 *  returns heading
	 * @return The heading of the slide
	 */
	public String getHeading() {
		return heading;
	}

	/**
	 *  returns id
	 * @return The unique slide ID
	 */
	public int getId() {
		return id;
	}

	/**
	 *  returns next
	 * @return The unique ID of the slide the player should display after this one
	 */
	public int getNext() {
		return next;
	}

	/**
	 *  sets heading to heading
	 * @param heading The heading of the slide
	 */
	public void setHeading(String heading) {
		this.heading = heading;
	}

	/**
	 *  sets id to id
	 * @param id The unique ID of the slide
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 *  sets next to next
	 * @param next The unique ID of the slide the player should display after this one.
	 */
	public void setNext(int next) {
		this.next = next;
	}

	/**
	 * sets section flag to isSection
	 * @param isSection True if the TreeEntity represents a section header else false
	 */
	public void setSection(boolean isSection) {
		this.isSection = isSection;
	}

	/**
	 * returns section
	 * @return True if the TreeEntity represents a section header else false
	 */
	public boolean isSection() {
		return isSection;
	}

	/**
	 * overloaded toString to return heading
	 * @return heading The heading of this SlideEntity
	 */
	public String toString(){
		return heading;
	}

	/**
	 * gets prevPath
	 * @return prevPath path to an image file which gives a preview of the slide
	 */
	public String getPrevPath() {
		return prevPath;
	}

	/**
	 * sets prevPath to prevPath
	 * @param prevPath path to an image file which gives a preview of the slide
	 */

	public void setPrevPath(String prevPath) {
		this.prevPath = prevPath;
	}
}