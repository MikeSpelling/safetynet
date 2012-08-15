package cfss.org.safetynet.gui.coverflow;

import java.awt.Image;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import cfss.org.xmlstructure.TreeEntity;
import java.util.*;

/*************************************************
 *	Crab Factory Software Solutions
 *
 *	Software: 			SafetyNet
 *	Module: 			ImageFlowItem
 *
 *  Author:				http://sourceforge.net/projects/jtunes4/
 *
 *  Contributers:		Michael Spelling, David Walker, Mark Wrightson
 *
 *  Description: Creates a list of images to be used in coverflow.
 *
 * Based on Source:
 * http://sourceforge.net/projects/jtunes4/
 *
 * @author http://sourceforge.net/projects/jtunes4/
 *************************************************/
public class ImageFlowItem
{
    private BufferedImage image = null;
    private String label;

    private File file;

    public ImageFlowItem(String fileName, String label) {
        this(new File(fileName), label);
    }

    public ImageFlowItem(File file, String label) {
        this.file = file;
        this.label = label;
    }

    public ImageFlowItem(InputStream is, String label) throws IOException {
        this.label = label;
        loadImage(is);
    }

    /**
     * Load the Images from a Specified Directory
     * The Slide Name can be specified in here
     * @param directory
     * @return an array list of items
     */
    public static List<ImageFlowItem> loadFromDirectory(File directory) {
        List<ImageFlowItem> list = new ArrayList<ImageFlowItem>();

        if (!directory.isDirectory())	return list; // Return empty list if directory not found

        File[] files = directory.listFiles();

        for(int index = 0; index < files.length; index++) {
        	if(files[index].isDirectory()) continue;	// If a folder is found ignore it

        	//The name to be loaded into the coverflow i.e. the Slide Name can be specified here
        	String imageJPGName = files[index].getName();
        	String imageName = imageJPGName.substring(0, imageJPGName.length()-4);
        	ImageFlowItem item = new ImageFlowItem(files[index], imageName);
            list.add(item);
        }
        return list;
    }

	/**
	 * Takes a vector of TreeEntity and uses the previewpath data within the
	 * data to create an arraylist of images defined in the data.
	 *
	 * @param treeEntityVector is the vector of Tree Entities which should
	 * contain the previewpath and headings to be displayed
	 * @return an arraylist of items
	 */
    public static List<ImageFlowItem> loadFromTreeVector(Vector <TreeEntity> treeEntityVector) {
        List<ImageFlowItem> list = new ArrayList<ImageFlowItem>();

        File filename = null;
        for(int i = 1; i < treeEntityVector.size(); i++) {
    		if(treeEntityVector.get(i).getPrevPath()!= null) {
    			filename = new File(treeEntityVector.get(i).getPrevPath());
    			if(filename.isFile()) {
		        	ImageFlowItem item = new ImageFlowItem(filename,
		        			treeEntityVector.get(i).getHeading());
		        	list.add(item);
    			}
    		}
        }
        return list;
    }


    /**
     * Loads an image.
     *
     * @throws FileNotFoundException if cannot find image file.
     * @throws IOException.
     */
    private void loadImage()  throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(file);
        loadImage(fis);
    }

    /**
     * Loads image from the InputStream
     *
     * @param is the InputStram to load the image from.
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void loadImage(InputStream is)
    	throws FileNotFoundException, IOException  {
        image = ImageIO.read(is);

        CrystalCaseFactory fx = CrystalCaseFactory.getInstance();
        image = fx.createReflectedPicture(fx.createCrystalCase(image));
    }


    /**
     * Returns the image.
     *
     * @return the image.
     */
    public Image getImage() {
        if (image == null) {
            try{loadImage();}
            catch (Exception e){e.printStackTrace();}
        }
        return image;
    }


    /**
     * Returns the label.
     *
     * @return the label.
     */
    public String getLabel(){
        return label;
    }
}