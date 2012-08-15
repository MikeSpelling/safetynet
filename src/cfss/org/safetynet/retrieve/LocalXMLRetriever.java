/************************************************
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		LocalXMLRetriever
 *
 *  Author: 		Callum Goddard
 *
 *  Contributers:	Mark Mellar, Mark Wrightson
 *  Testers:		.
 *
 *
 *  Description:	This opens a local file browser and allows
 *  				the user to select a presentation on their local machine
 * 					to load into SafetyNet.
 *
 ***********************************************/

package cfss.org.safetynet.retrieve;

import java.io.*;
import javax.swing.*;
import cfss.org.flags.Debug;
import cfss.org.safetynet.SafetyNet;
import cfss.org.safetynet.gui.Gui;

/**
 * 	This opens a local file browser and allows
 *  the user to select a presentation on their local machine
 *  to load into safetynet.
 *
 * @author Callum Goddard
 */
public class LocalXMLRetriever {

	private File xmlFilePath = null; 	// The file path for the XML file
	private File curDir = new File("src/cfss/org/XML");	//remember the last directory accessed

	// Static file extension check to ensure only XML presentations are selected.
	private static String VALIDFILEEXTENSION = "xml";


	/**
	 * Constructor
	 */
	public LocalXMLRetriever(){

	}

	/**
	 * Creates a file chooser, which will display in the center of the screen.
	 *
	 * By default the browser will start in the last directory accessed by the user
	 *
	 * A filter is added to the file browser window which will only allow the
	 * user to select a directory or an XML file.
	 *
	 * @return path -  this returns the file path of the file selected as
	 * a String, which has been optimised to unix format.
	 */
	public String retrieveXML(){

		// create file chooser and ensure it displays in the center of the main window
		JFileChooser xmlBrowser = new JFileChooser();

		//sets xml filter
		xmlBrowser.setFileFilter(new XMLFilter());
		if(curDir!=null){
			//if jFileChooser has been used before, load to previous directory
			xmlBrowser.setCurrentDirectory(curDir);
		}

		int returnVal = xmlBrowser.showOpenDialog(Gui.getContentPane());

		if(returnVal == JFileChooser.APPROVE_OPTION){
			xmlFilePath = xmlBrowser.getSelectedFile();		//get the selected filename
			curDir = xmlBrowser.getCurrentDirectory();		//save the last used directory


			String path = xmlFilePath.getAbsolutePath();

			String fileExtension = path.substring(path.lastIndexOf(".")+1, path.length());

			if(Debug.localXML)System.out.println(fileExtension);



			// Checks the file extension of the file selected, if it is not an XML file
			// a dialog box will appear informing the user that they have not selected
			// a valid xml presentation file.
			if(VALIDFILEEXTENSION.equals(fileExtension)){
				SafetyNet.setPath(path);		//set the path to be stored by config
				path = path.replace('\\', '/');
			}else{
				JOptionPane.showMessageDialog(Gui.getContentPane(), "A Valid XML Presentation File was not selected");
				path = null;
			}


			//Test case to check current directory updates
			if(Debug.localXML)System.out.println("current directory is" + curDir);

			//Test case to check string of file.
			if(Debug.localXML)System.out.println("file path is" + path);
			return path;
		}
		else
			return null;
	}
}