/**
 * 		Crab Factory Software Solutions
 *
 * 		Software: SafetyNet
 * 		Module: DocumentInfo
 *
 * 		Author: Mark Mellar
 *
 * 		Contributers: Callum Goddard
 * 		Testers:
 * 		Intergrators:
 *
 * 		Description:
 * 		Stores information pertaining to the slideshow itself (author version etc)
 *
 */

package cfss.org.xmlstructure;

import java.util.Date;

/**
 * Stores information pertaining to the slideshow itself (author version etc)
 * @author Mark Mellar
 *
 */
public final class DocumentInfo {

	/**
	 * the title of the presentation
	 */
	private String heading = "Presentation";
	/**
	 *  a description of the presentation
	 */
	private String comment = "";
	/**
	 *  date the presentation was published
	 */
	Date date;
	/**
	 * the author of the presentation
	 */
	private String author = "";
	/**
	 * the version number of the presentation
	 */
	private String version = "";

	/**
	 *  constructor
	 */
	public DocumentInfo(){
		//blank
	}

	/**
	 *  return comment
	 * @return the comment associated with the slideshow this object is owned by
	 */
	public String getComment() {
		return comment;
	}

	/**
	 *  return date
	 * @return date the slideshow was published
	 */
	public Date getDate() {
		return date;
	}

	/**
	 *  return name
	 * @return the name of the author slideshow
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * return version
	 * @return version number of the slideshow
	 */
	public String getVersion() {
		return version;
	}

	/**
	 *  set comment to comment
	 * @param comment the comment associated with the slideshow this object is owned by
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * set date to date
	 * @param date date the slideshow was published
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * set name to name
	 * @param author the name of the author slideshow
	 */
	public void setName(String author) {
		this.author = author;
	}

	/**
	 *  set version to version
	 * @param version Version number of the slideshow.
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * sets heading to passed heading
	 * @param heading the title of the slideshow
	 */
	public void setHeading(String heading) {
		this.heading = heading;
	}

	/**
	 * returns the heading of the slideshow
	 * @return the heading of the slideshow
	 */
	public String getHeading() {
		return heading;
	}
}