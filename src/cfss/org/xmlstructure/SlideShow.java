/**
 * 		Crab Factory Software Solutions
 *
 * 		Software: SafetyNet
 * 		Module: SlideShow
 *
 * 		Author: Mark Mellar
 *
 * 		Contributers: Callum Goddard
 * 		Testers:
 *
 * 		Description:
 * 		Top level of class structure read from XML. Stores information pertaining
 *  	to the slideshow and everything within it
 *
 */

package cfss.org.xmlstructure;

import java.util.*;
/**
 * Top level of class structure read from XML. Stores information pertaining*
 * to the slideshow and everything within it
 * @author Mark Mellar
 *
 */
public final class SlideShow {
	//object wich holds general presentation data
	private DocumentInfo docInf;
	//vector of slides quizSlides and sections
	private Vector<TreeEntity> tree = new Vector<TreeEntity>();

	/**
	 *  constructor
	 */
	public SlideShow(){
		tree = new Vector<TreeEntity>();
		//insert dummy slide to keep slide ID equal to index in vector
		tree.add(new Slide());

	}

	/**
	 *  return docInf
	 * @return the Document info for this slideshow
	 */
	public DocumentInfo getDocInf() {
		return docInf;
	}

	/**
	 *  return tree
	 * @return a vector of the slides in this slideshow
	 */
	public Vector<TreeEntity> getTree() {
		return tree;
	}

	/**
	 * set docInf to docInf
	 * @param docInf the Document info for this slideshow
	 */
	public void setDocInf(DocumentInfo docInf) {
		this.docInf = docInf;
	}

	/**
	 *  adds treeEntity to tree
	 * @param slide a treeEntity to be added to the slideshow
	 */
	public void addTreeEntity(TreeEntity slide) {
		this.tree.add(slide);
	}
}