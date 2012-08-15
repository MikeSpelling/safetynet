/**
 * 		Crab Factory Software Solutions
 *
 * 		Software: SafetyNet
 * 		Module: QuizSlide
 *
 * 		Author: Mark Mellar
 *
 * 		Contributers: Callum Goddard
 * 		Testers:
 *
 * 		Description:
 * 		Stores information pertaining to a quiz slide and all the elements within
 * 		it (in a vector of SlideEntity)
 *
 */

package cfss.org.xmlstructure;

import java.awt.Color;
import java.util.*;
/**
 * Stores information pertaining to a quiz slide and all the elements within
 * it (in a vector of SlideEntity)
 * @author Mark Mellar
 *
 */
public final class QuizSlide extends Slide{

	//areas the user can click in order to answer a question
	private Vector<AnswerArea> answerAreas;
	//the id of the correct answer area
	private float correctAlpha;
	//the correct answer to the question
	private int correctAnswer;
	//transparrency of the background of the correct answer text (displayed when user has answered the question)
	private float correctBkAlpha;
	//colour of background of the correct answer text
	private Color correctBkcol;
	//colour of the correct answer text
	private Color correctColour;
	//font of the correct answer text
	private String correctFont;
	//size of the correct answer text
	private float correctSize;
	//position of the correct answer text
	private XMLPoint correctStart;

	//vector of entity's to be displayed once the question has been answered
	private Vector<SlideEntity> feedback;

	//the answer the user has given (if any)
	private int givenAnswer = -1;

	//transparency of the displayed score text
	private float scoreAlpha;
	//background transparency of displayed score text
	private float scoreBkAlpha;
	//background colour of displayed score text
	private Color scoreBkCol;
	//colour of displayed score text
	private Color scoreColour;
	//font of displayed score text
	private String scoreFont;
	//font size of displayed score text
	private float scoreSize;
	//position of displayed score text
	private XMLPoint scoreStart;

	/**
	 *  Constructor
	 */
	public QuizSlide(){
		answerAreas = new Vector<AnswerArea>();
		feedback = new Vector<SlideEntity>();
		correctStart = new XMLPoint();
		scoreStart = new XMLPoint();
	}
	/**
	 * copy constructor
	 * @param def the QuizSlide to be copied
	 */
	public QuizSlide(QuizSlide def){
		super(def);

		this.correctAlpha = def.getCorrectAlpha();
		this.correctAnswer = def.getCorrectAnswer();
		this.givenAnswer = def.getGivenAnswer();
		this.correctBkAlpha = def.getCorrectBkAlpha();
		this.correctBkcol = def.getCorrectBkcol();
		this.correctColour = def.getCorrectColour();
		this.correctFont = def.getCorrectFont();
		this.correctSize = def.getCorrectSize();
		this.scoreAlpha = def.getScoreAlpha();
		this.scoreBkAlpha = def.getScoreBkAlpha();
		this.scoreBkCol = def.getScoreBkCol();
		this.scoreColour = def.getScoreColour();
		this.scoreFont = def.getScoreFont();
		this.scoreSize = def.getScoreSize();


		//copy feedback vector
		feedback = new Vector<SlideEntity>();
		SlideEntity current;
		if (null != def.getEntities()){
			for (int i = 0; i < def.getEntities().size(); i++){
				current = def.getEntities().get(i);
				if (current instanceof Audio)
					feedback.add(new Audio((Audio)current));
				else if (current instanceof Circle)
					feedback.add(new Circle((Circle)current));
				else if (current instanceof Midi)
					feedback.add(new Midi((Midi)current));
				else if (current instanceof XMLPolygon)
					feedback.add(new XMLPolygon((XMLPolygon)current));
				else if (current instanceof ScrollPane)
					feedback.add(new ScrollPane((ScrollPane)current));
				if (current instanceof Text)
					feedback.add(new Text((Text)current));
				if (current instanceof Video)
					feedback.add(new Video((Video)current));
				if (current instanceof XMLImage)
					feedback.add(new XMLImage((XMLImage)current));
			}
		}
		//copy answer vector
		answerAreas = new Vector<AnswerArea>();
		if (null != def.getAnswerAreas()){
			for (int i = 0; i < def.getAnswerAreas().size(); i++){
				answerAreas.add(def.getAnswerAreas().get(i));
			}
		}
		//copy score and corect positions
		if (null != def.getCorrectStart())
			correctStart = new XMLPoint(def.getCorrectStart());
		else
			correctStart = new XMLPoint();

		if (null != def.getScoreStart())
			scoreStart = new XMLPoint(def.getScoreStart());
		else
			scoreStart = new XMLPoint();
	}

	/**
	 * adds an answer to the vector
	 * @param anAnswerArea An answer area to be added to the slide
	 */
	public void addAnswerArea(AnswerArea anAnswerArea){
		this.answerAreas.add(anAnswerArea);
	}

	/**
	 * adds an entity to the feedback vector
	 * @param feedbackEntity An entity to be added to the feedback vector
	 */
	public void addFeedback(SlideEntity feedbackEntity){
		feedback.add(feedbackEntity);
	}

	/**
	 * returns the vector of answer areas
	 * @return the vector of answer areas
	 */
	public Vector<AnswerArea> getAnswerAreas(){

		return answerAreas;

	}

	/**
	 * returns correctAlpha
	 * @return the transparrency of the text in the correctness feedback box.
	 */
	public float getCorrectAlpha() {
		return correctAlpha;
	}

	/**
	 * return correctAnswer
	 * @return the id of the answer area which when clicked indicates a correct answer
	 */
	public int getCorrectAnswer(){
		return correctAnswer;
	}

	/**
	 * returns correctbkalpha
	 * @return the background alpha of the correctness feedback box.
	 */
	public float getCorrectBkAlpha() {
		return correctBkAlpha;
	}

	/**
	 * returns correctBkCol
	 * @return the background colour of the correctness feedback box.
	 */
	public Color getCorrectBkcol() {
		return correctBkcol;
	}

	/**
	 * returns correctcolour
	 * @return the colour of the text in the correctness feedback box.
	 */
	public Color getCorrectColour() {
		return correctColour;
	}

	/**
	 * returns correctfont
	 * @return the font of the text in the correctness feedback box.
	 */
	public String getCorrectFont() {
		return correctFont;
	}

	/**
	 * returns correctsize
	 * @return the size of the text in the correctness feedback box.
	 */
	public float getCorrectSize() {
		return correctSize;
	}

	/**
	 * returns correctStart
	 * @return the start point of the correctness feedback box
	 */
	public XMLPoint getCorrectStart() {
		return correctStart;
	}

	/**
	 *  returns feeback
	 * @return the vector of feedback entities
	 */
	public Vector<SlideEntity> getFeedback() {
		return feedback;
	}

	/**
	 *  returns GivenAnswer
	 * @return the answer (if any) the user gave to this question
	 */
	public int getGivenAnswer() {
		return givenAnswer;
	}

	/**
	 * returns scoreAlpha
	 * @return the transparency of the text in the score feedback box
	 */
	public float getScoreAlpha() {
		return scoreAlpha;
	}

	/**
	 * returns scorebkalpha
	 * @return the transparency of the background of the score feedback box
	 */
	public float getScoreBkAlpha() {
		return scoreBkAlpha;
	}

	/**
	 * returns scorebkcol
	 * @return the colour of the background of the score feedback box
	 */
	public Color getScoreBkCol() {
		return scoreBkCol;
	}

	/**
	 * returns scorecolour
	 * @return the colour of the text in the score feedback box
	 */
	public Color getScoreColour() {
		return scoreColour;
	}

	/**
	 * returns scorefont
	 * @return the font of the text in the score feedback box
	 */
	public String getScoreFont() {
		return scoreFont;
	}

	/**
	 * retuns scoresize
	 * @return the size of the text in the score feedback box
	 */
	public float getScoreSize() {
		return scoreSize;
	}

	/**
	 * returns scorestart
	 * @return the start point of the score feedback box
	 */
	public XMLPoint getScoreStart() {
		return scoreStart;
	}

	/**
	 * sets correct alpha
	 * @param anAlpha The transparency of the text in the correctness feedback box
	 */
	public void setCorrectAlpha(float anAlpha) {
		this.correctAlpha = anAlpha;
	}

	/**
	 *  sets correctAnswer to correctAnswer
	 * @param correctAnswer the id of the AnswerArea which indicates a correct answer
	 */
	public void setCorrectAnswer(int correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	/**
	 * sets correctbkalpha
	 * @param anAlpha the transparency of the background of the correctness feedback box
	 */
	public void setCorrectBkAlpha(float anAlpha) {
		this.correctBkAlpha = anAlpha;
	}

	/**
	 * sets correctbkcol
	 * @param aColor the colour of the background of the correctness feedback box
	 */
	public void setCorrectBkColour(Color aColor){
		this.correctBkcol = aColor;
	}

	/**
	 * sets correctcolour
	 * @param aColour the colour of the text in the correctness feedback box
	 */
	public void setCorrectColour(Color aColour) {
		this.correctColour = aColour;
	}

	/**
	 * sets correctfont
	 * @param aFont the font of the text in the correctness feedback box
	 */
	public void setCorrectFont(String aFont) {
		this.correctFont = aFont;
	}

	/**
	 * sets correct size
	 * @param aSize the size of the text in the correctness feedback box
	 */
	public void setCorrectSize(float aSize) {
		this.correctSize = aSize;
	}

	/**
	 * sets correctstart
	 * @param correctStart point defining the position of the top left hand corner of the correctness feedback box
	 */
	public void setCorrectStart(XMLPoint correctStart) {
		this.correctStart = correctStart;
	}

	/**
	 *  set givenAnswer to givenAnswer
	 * @param givenAnswer the ID of the answer area which the user clicked
	 */
	public void setGivenAnswer(int givenAnswer) {
		this.givenAnswer = givenAnswer;
	}

	/**
	 * sets scorealpha
	 * @param anAlpha the transparency of the text in the score feedback box
	 */
	public void setScoreAlpha(float anAlpha) {
		this.scoreAlpha = anAlpha;
	}

	/**
	 * sets scorebkalpha
	 * @param anAlpha the transparency of the background of the score feedback box
	 */
	public void setScoreBkAlpha(float anAlpha){
		this.scoreBkAlpha = anAlpha;
	}

	/**
	 * sets scorebkcol
	 * @param scoreBkCol the colour of the background of the score feedback box
	 */
	public void setScoreBkCol(Color scoreBkCol) {
		this.scoreBkCol = scoreBkCol;
	}

	/**
	 * sets scorecolour
	 * @param aColour the colour of the text in the score feedback box
	 */
	public void setScoreColour(Color aColour) {
		this.scoreColour = aColour;
	}

	/**
	 * sets scorefont
	 * @param aFont the font of the text in the score feedback box
	 */
	public void setScoreFont(String aFont) {
		this.scoreFont = aFont;
	}

	/**
	 * sets scoresize
	 * @param aScoreSize the size of the text in the score feedback box
	 */
	public void setScoreSize(float aScoreSize) {
		this.scoreSize = aScoreSize;
	}

	/**
	 * sets scorestart
	 * @param scoreStart a point defining the position of the top left hand corner of the score feedback box
	 */
	public void setScoreStart(XMLPoint scoreStart) {
		this.scoreStart = scoreStart;
	}
}