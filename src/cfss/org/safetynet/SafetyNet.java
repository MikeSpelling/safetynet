/************************************************
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		SafetyNet
 *
 *  Author: 		Mark Wrightson
 *
 *  Contributers:	Mike Spelling, Mark Mellar
 *  Testers:		.
 *
 *  Description:	This module creates and controls the main JPanel,
 *  				top level class, mainly deals with holding other classes
 *  				together
 *
 ***********************************************/

package cfss.org.safetynet;

import cfss.org.flags.Debug;
import cfss.org.safetynet.gui.Gui;
import cfss.org.safetynet.retrieve.LocalXMLRetriever;
import cfss.org.safetynet.retrieve.RemoteFileRetriever;
import cfss.org.xmlstructure.SlideShow;
import javax.swing.JOptionPane;

import java.net.MalformedURLException;

/**
 * This module creates and controls the main JPanel,
 * top level class, mainly deals with holding other classes together
 * @author Mark Wrightson
 *
 */
public class SafetyNet {


	// Path should be in config not safetynet
	/**
	 * The path to get the current presentation file (remote if file loaded from
	 * remote location)
	 */
	private static String path;
	/**
	 * The local path to the current presentation file
	 */
	private static String fileLocation;

	/**
	 * Instance of the LocalXML Retriever
	 */
	private static LocalXMLRetriever lxr = new LocalXMLRetriever();
	/**
	 * Instance of the RemoteXML Retriever
	 */
	private static RemoteFileRetriever rxr = new RemoteFileRetriever();

	/**
	 * Instance Variable for the SlideShow data structure
	 */
	public static SlideShow slideShow = null;

	/**
	 * Instance of the GUI
	 */
	public static Gui gui;

	/**
	 * Constructor
	 */
	public SafetyNet() {
		gui = new Gui();

		//needs to be instantiated because of local mouse monitor in slide renderer
		new Engine();

		String fileLocation = "src/cfss/org/XML/default.xml";
		System.out.println(fileLocation);
		Parser parser = new Parser();
		slideShow = parser.parse(fileLocation);
		if (slideShow != null){//successfully parsed
			Engine.playSlideshow();
		}
		else{//error parsing slideshow
			//display error message
			JOptionPane.showMessageDialog(Gui.getContentPane(),
				    "The slideshow file you selected is either corrupt or invalid.",
				    "Parser Error",
				    JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Any custom code that wants to be called when the Gui is resized should be placed in here
	 * @param scaleFactor
	 */
	public static void resize(float scaleFactor){
    	SlideRenderer.resizeAll(scaleFactor);

    	//VideoPlayer.video.resize(scaleFactor);		//leave this here please - MW!

	}

	/****************
	 * resumePrevious
	 *
	 * Retrieve path to previous presentation, determine type and
	 * send to parser to populate slideshow
	 ****************/
	public static void resumePrevious(){
		//get path of previous presentation from config
		String temp = Config.loadPreviousPresentation();
		if (null != temp){
			path = temp.replace('\\', '/');
			if (null != path){
				if (path.substring(0, 7).equals("http://")){
					if(Debug.safetyNet) System.out.println("retreiving remote presentation");
					try {
						fileLocation = RemoteFileRetriever.downloadFile(path);
					} catch (MalformedURLException e) {
						System.out.println("Could not retreive remote xml" + e.getMessage());
					}

				}
				else{
					fileLocation = path;
				}
				Parser parser = new Parser();
				slideShow = parser.parse(fileLocation);

				if (slideShow != null){
					loadGui();

					Config.loadPreviousSlide();

				}
			}
			if (slideShow != null){
				//Play the slideshow
				Engine.playSlideshow();
			}
			else{//error parsing slideshow
				//display error message
				JOptionPane.showMessageDialog(Gui.getContentPane(),
					    "The slideshow file you selelected is either corrupt or invalid.",
					    "Parser Error",
					    JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	/**
	 * Allows user to choose a (remote or local) file to load.
	 *
	 * @param isRemote true if user want to open a remote presentation file.
	 */
	public static void findPresentation(boolean isRemote){
		Parser parser = new Parser();

		if(isRemote){
			//request url from user

			fileLocation = rxr.downloadFile();
			if(fileLocation!=null){
				//only parse & update components if the file Location is valid
				slideShow = parser.parse(fileLocation);

				if (slideShow != null){//slideshow was parsed successfully
					//Set presentation to first slide, clear quiz data and update gui contents

					QuizHandler.resetQuizData();

					loadGui(); // Loads the slideshow data to the gui components.

					Engine.gotoSlide(1);
					if (Config.checkCurrentPresentation(fileLocation)){
						int response;

					   response = JOptionPane.showConfirmDialog(Gui.getContentPane(),
							   "Resume slideshow from previous position?"
							   ,"Resume?",JOptionPane.YES_NO_OPTION);
					   if(response == JOptionPane.YES_OPTION){
						   Config.updateResumedSlideID();
						   Config.updateResumedQuizData();
					   }
					}
					//Play the slideshow
					Engine.playSlideshow();
				}
				else{//error parsing slideshow
					//display error message
					JOptionPane.showMessageDialog(Gui.getContentPane(),
						    "The slideshow file you selelected is either corrupt or invalid.",
						    "Parser Error",
						    JOptionPane.ERROR_MESSAGE);
				}

			}
		}
		else{
			fileLocation = lxr.retrieveXML();
			if(fileLocation!=null){
				//only parse & update components if the file Location is valid
				slideShow = parser.parse(fileLocation);


				if (slideShow != null){//parsed correctly
					path = fileLocation;
					//Set presentation to first slide, clear quiz data and update gui contents

					QuizHandler.resetQuizData();

					loadGui(); // Loads the slideshow data to the gui components.
					Engine.gotoSlide(1);

					if (Config.checkCurrentPresentation(fileLocation)){
						int response;
					   response = JOptionPane.showConfirmDialog(Gui.getContentPane()
							   , "Resume slideshow from previous position?",
							   "Resume?",JOptionPane.YES_NO_OPTION);
					   if (response == JOptionPane.YES_OPTION){
						   Config.loadPreviousSlide();
						   Config.loadPreviousQuiz();
					   }

					}
					//Play the slideshow
					Engine.playSlideshow();
				}else{//error parsing slideshow
					//display error message
					JOptionPane.showMessageDialog(Gui.getContentPane(),
						    "The slideshow file you selelected is either corrupt or invalid.",
						    "Parser Error",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
			else{
				if(Debug.safetyNet) System.out.println("no file was selected for loading\n " +
						"Cancel button in JFileChoose probably pressed");
			}
		}
	}

	/**Loads slideshow and quiz data into gui components. Triggers them to update.
	 *
	 */
	public static void loadGui(){

		Gui.getSectionNav().updateTree();
		Gui.getCoverFlow().initialise();

		/*TODO: implement this in transport
		transport.setCurrentSlideID(engine.currentSlideID);
		 */

		// Set the window title according to the presentation title.
		Gui.getMainFrame().setTitle(SafetyNet.slideShow.getDocInf().getHeading());

	}

	/**
	 * Refreshes the GUI components to the current state
	 */
	public static void updateGui(){

		/* TODO: Pass in the slide you want to go to...
		//sectionNav.updateCurrent();
		coverFlow.updateCurrentSlide();
		/* TODO: implement this
		transport.setCurrentSlideID(engine.currentSlideID);
		*/

		// Keep this here, it updates coverflow to display the current slide
		// whenever the current slide is changed.
		Gui.getCoverFlow().updateToSlide();
	}

	/**
	 * Returns the local path to the current presentation XML
	 * @return the local path to the current presentation XML
	 */
	public static String getFileLocation() {
		return fileLocation;
	}


	/**
	 *Sets the resource path of the current presentation XML (Is remote when presentation opened from remote source)
	 * @param path - the resource path of the current presentation XML (Is remote when presentation opened from remote source)
	 */
	public static void setPath(String path) {
		SafetyNet.path = path;
	}


	/**
	 *Gets the resource path of the current presentation XML (Is remote when presentation opened from remote source)
	 * @return path  - the resource path of the current presentation XML (Is remote when presentation opened from remote source)
	 */
	public static String getPath() {
		return path;
	}

	/**
	 * Returns the slideshow datastructure
	 * @return slideShow - The slideshow datastructure
	 */
	public static SlideShow getSlideShow() {
		return slideShow;
	}


	public static void main(String[] args) {
		new SafetyNet();
	}
}