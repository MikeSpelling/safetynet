/**
 * Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		QuizHandler
 *
 *  Author: 		Mark Mellar
 *
 *  Contributers:	.
 *  Testers:		Michael Spelling
 *
 *  Description:	interfaces between the program and the config file, which
 *  stores information pertaining to slideshows which can be resumed, their quiz
 *  data and the file path of the slideshow previously loaded
 *
 */
package cfss.org.safetynet;

import java.awt.Color;
import java.util.Vector;
import cfss.org.safetynet.gui.*;

import cfss.org.xmlstructure.QuizSlide;
/**
 * interfaces between the program and the config file, which stores information
 * pertaining to slideshows which can be resumed, their quiz data and the file
 * path of the slideshow previously loaded
 * @author Mark Mellar
 */
public class QuizHandler {

	/**
	 * the number of questions the user has answered
	 */
	private static int questionsAnswered = 0;
	/**
	 * the number of questions the user got correct
	 */
	private static int questionsCorrect = 0;
	/**
	 * a vector containing quiz datas
	 * a quiz data is a vector of 3 ints slide ID, correctness (-1 not answered , 0 incorrect, 1 correct)and given answer ID respectively
	 */
	private static Vector<Vector<Integer>> quizData = new Vector<Vector<Integer>>();

	/**
	 * sets quizData to the passed vector of vectors
	 * @param data a vector containing quiz datas a quiz data is a vector of 3 ints slide ID, correctness (-1 not answered , 0 incorrect, 1 correct)and given answer ID respectively
	 */
	public static void setQuizData(Vector<Vector<Integer>> data){
		questionsAnswered = data.size();
		int totCorrect = 0;
		for(int i = 0; i < data.size(); i++){
			if(1 == data.get(i).get(1))
				totCorrect++;
		}
		questionsCorrect = totCorrect;
		quizData = data;
		initialiseGui();
	}


	/**
	 * adds a single quizData to the quizData vector
	 * @param slideId the id of the slide the quizData pertains to
	 * @param answerId the answer the user gave
	 */
	public static void addQuizData(int slideId, int answerId){
		int correctness = markQuizData(slideId, answerId);
		Vector<Integer> answer = new Vector<Integer>();
		answer.add(slideId);
		answer.add(correctness);
		answer.add(answerId);
		quizData.add(answer);


		questionsAnswered++;
		if (correctness == 1){
			questionsCorrect++;
			Gui.getSectionNav().setColor(slideId, Color.GREEN);
		}
		else
		{
			Gui.getSectionNav().setColor(slideId, Color.RED);
		}
	}

	/**
	 * returns the number of questions the user has answered
	 * @return int the number of questions the user has answered
	 */
	public static int getQuestionsAnswered(){
		return questionsAnswered;
	}

	/**
	 * returns the number of correct answers the user has
	 * @return int the number of correct answers the user has
	 */
	public static int getAnswersCorrect(){
		return questionsCorrect;
	}

	/**
	 * returns the vector of quiz data vectors (Vector<Vector<int>> where Vector<int> represents slide id, correctness and given answer respectively
	 * @return the vector of quiz data vectors (Vector<Vector<int>> where Vector<int> represents slide id, correctness and given answer respectively
	 */
	public static Vector<Vector<Integer>> getQuizData(){
		return quizData;

	}

	/**
	 * Updates the sectionNav colourings of quizSlides to match the data in quizdata
	 * slides which are correct are coloured green, incorrect slides red.
	 */
	private static void initialiseGui(){
		for (int i = 0; i < quizData.size(); i++){
			QuizSlide curSlide = (QuizSlide)SafetyNet.getSlideShow().getTree().get(quizData.get(i).get(0));
			curSlide.setGivenAnswer(quizData.get(i).get(2));
			if (1 == quizData.get(i).get(1)){
				Gui.getSectionNav().setColor(quizData.get(i).get(1),Color.GREEN);
			}else if (0 == quizData.get(i).get(1)){
				Gui.getSectionNav().setColor(quizData.get(i).get(1),Color.RED);
			}
		}
	}

	/**
	 * marks a single slide in section nav according to whether the given answer is correct or not
	 * correct answers are marked green, incorrect red
	 * @param anID int the id of the slide to be marked
	 * @param anAns int the id of the answer the user submitted
	 * @return returns the correctness of the answer 1 if correct, 0 if incorrect
	 */
	public static int markQuizData(int anID, int anAns){
		QuizSlide slide = (QuizSlide)SafetyNet.getSlideShow().getTree().get(anID);
		int correctAns = slide.getCorrectAnswer();

		if (anAns == correctAns){
			Gui.getSectionNav().setColor(anID,Color.GREEN);
			return 1;
		}else{
			Gui.getSectionNav().setColor(anID,Color.RED);
			return 0;
		}
	}

	/**
	 * reinitialises all member fields
	 */
	public static void resetQuizData()	{
		quizData = new Vector<Vector<Integer>>();
		questionsAnswered = 0;
		questionsCorrect = 0;
		QuizSlide tempQuizSlide;

		int i;
		if (SafetyNet.getSlideShow() != null)
		{
			for(i=0;i<SafetyNet.getSlideShow().getTree().size();i++)
			{
				// Go through the datastructure and reset all the GivenAnswer fields.
				if (SafetyNet.getSlideShow().getTree().get(i) instanceof QuizSlide)
				 {
					tempQuizSlide = (QuizSlide) SafetyNet.getSlideShow().getTree().get(i);
					tempQuizSlide.setGivenAnswer(-1);
				 }
			}
		}
	}
}