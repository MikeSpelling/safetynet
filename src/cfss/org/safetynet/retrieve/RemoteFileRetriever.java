/************************************************
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		RemoteFileRetriever
 *
 *  Author:			Callum Goddard
 *
 *  Contributers:	Mark Mellar, Mark Wrightson
 *  Testers:		.
 *
 *
 *  Description:	This downloads a file from a URL and returns
 *  				the file path of where the object was downloaded to.
 *
 ***********************************************/

package cfss.org.safetynet.retrieve;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import javax.swing.JOptionPane;
import cfss.org.flags.Debug;
import cfss.org.safetynet.SafetyNet;
import cfss.org.safetynet.gui.Gui;
/**
 * 	This downloads a file from a URL and returns
 *  the file path of where the object was downloaded to.
 *
 * @author Callum Goddard
 *
 */
public class RemoteFileRetriever {

	private static String VALIDFILEEXTENSION = "xml";
	private String url = null;

	/**
	 * Class Constructor
	 */
	public RemoteFileRetriever() {

	}


	/**
	 * This method displays a dialog box that the user can type in the URL of
	 * the file that is to be downloaded.  The file is then downloaded.
	 * If the URL is not a valid address a malformed url error will occur.
	 *
	 * @return url - this is a String that contains the file path of the file
	 * that has been downloaded.
	 */
	 public String downloadFile(){

		//JoptionPane must be passed its parent to ensure it displays in the
		//right place


	 String userInputURL = JOptionPane.showInputDialog(Gui.getContentPane(),
				  "Please Enter the URL of the File that is to be downloaded",
				  "Enter the URL",
				  JOptionPane.QUESTION_MESSAGE);

	if(userInputURL == null){

		// Debug if no URL was entered.
		if(Debug.remoteXML)System.out.println("No URL was Entered");
		return null;
	}

		//Test case to check the user input from dialog box
		if(Debug.remoteXML)System.out.println("URl entered by the User: " + userInputURL + "\n");

		// Works out the file extension of the file to be downloaded.
		String fileExtension = userInputURL.substring(userInputURL.lastIndexOf(".")+1, userInputURL.length());

		// Debug to check the what file extension was obtained from the URL.
		if(Debug.remoteXML)System.out.println(fileExtension);

		// Validation Check to make sure that the file selected was infact an XML file.
		if(VALIDFILEEXTENSION.equals(fileExtension)){

			try {

				SafetyNet.setPath(userInputURL);		//set the path to be stored by config
				url = RemoteFileRetriever.downloadFile(userInputURL);
				url = url.replace('\\', '/');

			} catch (MalformedURLException e) {

				if(Debug.remoteXML)System.out.println("Malformed URL Exception Caught");
				return null;
				//e.printStackTrace();
			}
			catch (NullPointerException e) {

				System.out.println("Couldn't find the file to download");
				JOptionPane.showMessageDialog(Gui.getContentPane(), "Couldn't find the file to download");
				return null;
				//e.printStackTrace();
			}


			url = url.replace('\\', '/');
		}else{
			JOptionPane.showMessageDialog(Gui.getContentPane(), "A Valid XML Presentation File was not selected");
			url = null;
		}

		// Test case to see if url is passed out.
		if(Debug.remoteXML)System.out.println("file downloaded path =" +url);

		return url;

	}

	/**
	 * This method takes a fileURL input string -
	 * which is the url of the file that is needed to be downloaded.
	 *
	 * @return path  - this is a string of the absolute file path of the
	 * downloaded file
	 */
	public static String downloadFile(String fileURL) throws MalformedURLException{

		if(fileURL.equals(null)){
			return null;
		}

		// Check the URL passed in contains http:// if not add http://
		if(!fileURL.substring(0, 7).equals("http://")){

			fileURL = "http://" + fileURL;

			// Debug to Check http:// added
			if(Debug.remoteXML)System.out.println("file URL with HTTP:// added = " + fileURL);

		}
		URL fileDownloadURL = new URL(fileURL);

		// Debug to confirm the file URL
		if(Debug.remoteXML)System.out.println("fileDownloadURL: " + fileDownloadURL +"\n");

		ReadableByteChannel readInBytes;

		String URLString = null;
		File downloadedFile = null;

		try {
			URLString = URLDecoder.decode(fileURL, "UTF-8");


		} catch (UnsupportedEncodingException e1) {

			//if decoding fails.
			System.out.println("URL Decoding has caused an exception");
		}

		// takes the decoded URL and works out that the file extension of the file to be downloaded.
		String fileExtension = URLString.substring(URLString.lastIndexOf('.')+1,
																URLString.length());

		//Debug for File Extension
		if(Debug.remoteXML)System.out.println("File extension of file is: " + fileExtension);

		// takes decoded URL and gets the filename of the file that is to be downloaded. - replaces and %20 with a space.
		String fileName = URLString.substring(URLString.lastIndexOf('/')+1,
				URLString.lastIndexOf('.'));

		// Debug
		if(Debug.remoteXML)System.out.println("File name is: " + fileName +"\n");


		// combines the filename and the file extension and makes a file
		downloadedFile = new File("download/"+fileName + "." + fileExtension);


		//test case to see if variable takes right value
		if(Debug.remoteXML)System.out.println("The path the file will be downloaded to: " + downloadedFile.getAbsolutePath());

		//makes a URL from the passed in string fileURL


		 String path = null;
	try {

		// readablebyte channel - reads in the bytes from a URL stream of the
		// location of the file to be downloaded.
		readInBytes = Channels.newChannel(fileDownloadURL.openStream());

		// Makes the output file Stream and then writes the file.
		FileOutputStream fos = new FileOutputStream(downloadedFile);

		fos.getChannel().transferFrom(readInBytes, 0, 1 << 24);

		path = downloadedFile.getAbsolutePath();
		path = path.replace('\\', '/');

		if(Debug.remoteXML)System.out.println("File has been downloaded to:" +downloadedFile.getAbsolutePath());

	} catch (IOException e) {

		System.out.println("Something went wrong downloading the File");
		//JOptionPane.showMessageDialog(Gui.getContentPane(), "Something went wrong downloading the File");
		//path = null;
	}

//	String path = downloadedFile.getAbsolutePath();
//	path = path.replace('\\', '/');

	// Test case to see if url is passed out.
	if(Debug.remoteXML)System.out.println("Print out of what is returned: " + path);

	return path;

	}
}