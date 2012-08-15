/************************************************
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		XMLFilter
 *
 *  Author: 		Callum Goddard
 *
 *  Contributers:	Mark Mellar, Mark Wrightson
 *  Testers:		.
 *
 *
 *  Description:	This is a filter that prevents the user
 *  from selecting at file that is not an XML file.
 *
 *  Code was originally copied from:
 *  http://www.javaworld.com/javaworld/javatips/jw-javatip85.html
 *  Link last checked 6/6/2010.
 *
 ***********************************************/

package cfss.org.safetynet.retrieve;

import java.io.File;
import javax.swing.filechooser.FileFilter;


/**
 *	This is a filter that prevents the user
 *  from selecting at file that is not an XML file.
 *
 *  Code was originally copied from:
 *  http://www.javaworld.com/javaworld/javatips/jw-javatip85.html
 *  Link last checked 6/6/2010.
 *
 * @author Callum Goddard
 *
 */
public class XMLFilter extends FileFilter
{

    /** This is the one of the methods that is declared in
     * the abstract class.
     *
     * This checks to see if the file type is a directory or an XML file.
     *
     * @return boolean - this will be true if the file is a directory
     * or has a .xml file extension, else it will be false.
     */
  public boolean accept(File f)
  {

    //if it is a directory -- we want to show it so return true.
    if (f.isDirectory())
      return true;

    //get the extension of the file
    String extension = getExtension(f);

    //check to see if the extension is equal to "xml"
    if (extension.equals("xml"))
       return true;

    //default -- fall through. False is return on all
    //occasions except:
    //a) the file is a directory
    //b) the file's extension is what we are looking for.
    return false;
  }

  	/**
     * Again, this is declared in the abstract class
     *
	 * This gives the description that appears in the drop downbox in the
	 * file browser of the filter that is being used
     *
     * @return "XML Files" - description
     */
  public String getDescription()
  {
      return "XML files";
  }

    /**
     *
     * Method to get the extension of the file, in lowercase
     *
     * @return file extension, if no file extension is found  "" is returned.
     */
  private String getExtension(File f)
  {
    String s = f.getName();
    int i = s.lastIndexOf('.');
    if (i > 0 &&  i < s.length() - 1)
      return s.substring(i+1).toLowerCase();
    return "";
  }
}