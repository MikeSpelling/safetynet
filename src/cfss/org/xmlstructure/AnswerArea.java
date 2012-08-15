/**
 * 	Crab Factory Software Solutions
 *
 * 	Software: 		SafetyNet
 * 	Module: 		AnswerArea
 *
 * 	Author: 		Mark Mellar
 *
 * 	Contributers: 	Callum Goddard
 * 	Testers:
 *
 * 	Description:
 * 	Stores information pertaining to an area on screen which, when clicked,
 * 	should be interpreted as an answer to a quizSlide
 *
 */


package cfss.org.xmlstructure;
/**
 * Stores information pertaining to an area on screen which, when clicked,
 * should be interpreted as an answer to a quizSlide.
 *
 * @author Mark Mellar
 */
public final class AnswerArea extends ClickableEntity{
	/**
	 * height of area in pixels
	 */
	private int height;
	/**
	 * width of area in pixels
	 */
	private int width;
	/**
	 * the answer id this area refers to
	 */
	private int id;

	/**
	 *  Constructor
	 */
	public AnswerArea(){
		super();
	}

	/**
	 * copy constructor
	 * @param def the AnswerArea to be copied
	 */
	public AnswerArea(AnswerArea def) {
		super(def);
		this.id = def.getId();
		this.height = def.getHeight();
		this.width = def.getWidth();
	}

	/**
	 * returns id of the answer area
	 * @return int id of the answer area
	 */
	private int getId() {
		return id;
	}

	/**
	 *  returns height of answerArea
	 * @return int height of answerArea
	 */
	public int getHeight() {
		return height;
	}

	/**
	 *  returns width of answerArea
	 * @return int width of answerArea
	 */
	public int getWidth() {
		return width;
	}

	/**
	 *  sets height of answer area to height
	 * @param height of answer area
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 *  sets width of answer area to width
	 * @param width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * sets id to anId
	 * @param anId the id the answer area is to be refrenced by
	 */
	public void setId(Integer anId) {
		this.id = anId;
	}
}