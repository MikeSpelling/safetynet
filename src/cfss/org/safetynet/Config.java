/**
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		Config
 *
 *  Author: 		Mark Mellar
 *
 *  Contributers:	.
 *  Testers:		Michael Spelling
 *
 *  Description:	Manages access to config.txt which stores data
 *  				used to resume slideshows previously played in the software.
 */


package cfss.org.safetynet;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import javax.swing.JOptionPane;
import cfss.org.flags.Debug;
import cfss.org.safetynet.gui.Gui;


/**
 *  Manages access to config.txt which stores data
 *  used to resume slideshows previously played in the software.
 *
 * @author Mark Mellar
 *
 */
public final class Config {

	/**
	 * the path to the config file where resume data is stored
	 */
	private static String CONFIG_PATH = "src/config.txt";
	/**
	 * the ID of the slide to go to on resuming (read from the config file)
	 */
	private static int resumedSlideID;
	/**
	 * the quiz data of the presentation being resumed (read from the config file)
	 */
	private static Vector<Vector<Integer>> resumedQuizData;

	/**
	 * finds if a file has been opened before, if it has, loads the previous
	 * slide id and quiz data into local fields DOES NOT UPDATE ENGINE ETC!!
	 * @param presentationLocation String The path to the presentation file in question
	 * @return boolean true if file has a previous state saved in the config file else false
	 */
	public static boolean checkCurrentPresentation(String presentationLocation){
		//get an identifier for the file
		long hash = hash(presentationLocation);

		try {
			File conFile = new File(CONFIG_PATH);
			if(conFile.exists()){
				FileInputStream fileStream = new FileInputStream(conFile);
				DataInputStream dataStream = new DataInputStream(fileStream);
				BufferedReader config = new BufferedReader(new InputStreamReader(dataStream));
				//not interested in first line (previous slideshow path)
				String inString = config.readLine();

				if (!inString.equals("!")){
				//if we arent already at the end (no entries)
					inString = config.readLine();
					while (!inString.equals("!")){
					//while we arent at the end of the file
						if (!inString.equals("?")){
						//beginning of an entry

							//get entry identifier (or '!' if its the end of the file)
							if (inString.equals(String.valueOf(hash))){
							//if its the one we're looking for
								//get the info
								resumedSlideID = Integer.valueOf(config.readLine());
								inString = config.readLine();
								Vector<Vector<Integer>> quizData = new Vector<Vector<Integer>>();
								while(!inString.equals("?")){
								//loop through quiz data till the end of the entry
									StringTokenizer quiz = new StringTokenizer(inString, " ");
									Vector<Integer> quizVector = new Vector<Integer>();
									quizVector.add(Integer.valueOf(quiz.nextToken()));
									quizVector.add(Integer.valueOf(quiz.nextToken()));
									quizVector.add(Integer.valueOf(quiz.nextToken()));
									quizData.add(quizVector);
									inString = config.readLine();
								}
								resumedQuizData = quizData;
								//entry was found
								config.close();
								return true;
							}
						}
						inString = config.readLine();
					}
				}
			}
		}catch (Exception e) {
			System.out.println("Exception searching config " + e.getMessage());
			e.printStackTrace();
		}
		//entry wasnt found
		return false;
	}

	/**
	 * updates the engine to the current value of resumedSlideID
	 */
	public static void updateResumedSlideID(){
		Engine.gotoSlide(resumedSlideID);
		SafetyNet.updateGui();
	}

	/**
	 * updates the quiz handler to the current value of resumedQuizData
	 */
	public static void updateResumedQuizData(){
		QuizHandler.setQuizData(resumedQuizData);
	}

	/**
	 * removes any entry for the current file, and appends a new, updated one
	 */
	public static void saveData(){
		//get data to store
		int id = Engine.getCurrentSlideID();
		String orginalPath = SafetyNet.getPath();
		String currentPresentationLocation = SafetyNet.getFileLocation();

		if (orginalPath != null){//must be a file to save
			Vector<Vector<Integer>> quizData = QuizHandler.getQuizData();

			//get entry identifier
			long hash = hash(currentPresentationLocation);
			try{
				//get buffered file for input (config) and output (tmp)
				Writer tempWriter;
				File tempFile = new File("tmp");
				tempWriter = new BufferedWriter(new FileWriter(tempFile));

				File configFile = new File(CONFIG_PATH);

				if (!configFile.exists())
					createNewConfig(configFile);

				FileInputStream fileStream = new FileInputStream(configFile);
				DataInputStream dataStream = new DataInputStream(fileStream);
				BufferedReader config = new BufferedReader(new InputStreamReader(dataStream));

				//not interested in first line
				config.readLine();
				//insert current path into previous presentation field (first line of file)
				tempWriter.write(orginalPath + '\n');
				String inString = config.readLine();

				//copy config file to tmp, excluding any entry with a matching hash
				while (!inString.equals("!") && (!(inString == null))){
					if (!inString.equals(String.valueOf(hash))){
						tempWriter.write("?\n");
						while (!inString.equals("?")){
							tempWriter.write(inString + '\n');
							inString = config.readLine();
						}
						tempWriter.write("?\n");
					}else{
						//skip entry
						while (!inString.equals("?"))
							inString = config.readLine();
					}
					inString = config.readLine();
				}

				//write a new entry
				tempWriter.write(String.valueOf(hash) + '\n');
				tempWriter.write(String.valueOf(id) + '\n');

				for (int i = 0; i < quizData.size(); i++){
					tempWriter.write(String.valueOf(quizData.get(i).get(0))
							+ ' ' + String.valueOf(quizData.get(i).get(1))
							+ ' ' + String.valueOf(quizData.get(i).get(2)) + '\n');
				}

				tempWriter.write("?\n");
				//end file
				tempWriter.write("!\n");
				tempWriter.close();
				config.close();
				//get rid of old file
				config = null;
				System.gc();
				boolean success = configFile.delete();

			    if (!success && Debug.config)
			    	   System.out.println("Deletion failed.");
				//replace with new file
				tempFile.renameTo(configFile);
			}catch(Exception e){
				JOptionPane.showMessageDialog(Gui.getContentPane(),
						"Error saving state. You may not be able to resume this session.");
			}
		}
	}

	/**
	 * returns the the previous presentation to be played on the software
	 * @return String The location of the file (local or remote) last played in the software, null if none present.
	 */
	public static String loadPreviousPresentation(){
		try{
			//get the first line of config
			FileInputStream buff = new FileInputStream(CONFIG_PATH);
			DataInputStream stream = new DataInputStream(buff);
			BufferedReader config = new BufferedReader(new InputStreamReader(stream));
			String originalPath = config.readLine();
			config.close();
			return originalPath;
		}catch(Exception e){
			System.out.println("No previous presentations avaliable "
					+ e.getMessage());
		}
		return null;
	}

	/**
	 * loads resume data for the file path stored in SafetyNet.getFileLocation()
	 * and updates the engine
	 */
	public static void loadPreviousSlide(){
		String currentPresentationLocation = SafetyNet.getFileLocation();
		checkCurrentPresentation(currentPresentationLocation);
		updateResumedSlideID();
		updateResumedQuizData();
	}

	/**
	 * artifact of previous design decisions, provides another interface to updateResumedQuizData()
	 */
	public static void loadPreviousQuiz(){
		updateResumedQuizData();
	}

	/*
	 * returns the CRC checksum of the file at the passed location
	 * @param presentationLocation String, the location of the file to be hashed.
	 * @return long the crc hash of the file, -1 if an error occurs
	 */
	private static long hash(String presentationLocation) {
		try{
			//get the file
			File inFile = new File(presentationLocation);
			if (inFile.exists()){
				FileInputStream input = new FileInputStream(inFile);
			    CheckedInputStream crcdInput = new CheckedInputStream(input, new CRC32());
			    BufferedInputStream in = new BufferedInputStream(crcdInput);

			    //read it all in
			    while (in.read() != -1)
			    //CRC it
			    return crcdInput.getChecksum().getValue();
			}
		}catch(Exception e){
			System.out.println("CRC exception encountered " + e.getMessage());
			e.printStackTrace();
		}
		return  -1;
	}

	private static void createNewConfig(File config){
		try{
			Writer tempWriter = new BufferedWriter(new FileWriter(config));
			tempWriter.write("noprev\n!");
			tempWriter.close();
		}catch(Exception e){
			if (Debug.config)
				System.out.println("Error creating new config file" + e.getMessage());
		}
	}

	public static void main(String[] args) {
		Config.checkCurrentPresentation("C:/Users/bob/Desktop/Test XML/defaultoverwritetest.xml");
		Config.checkCurrentPresentation("C:/Users/bob/Desktop/Test XML/g1_test_v1.4.1.xml");

	}
}
