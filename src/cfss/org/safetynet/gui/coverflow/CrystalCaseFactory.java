package cfss.org.safetynet.gui.coverflow;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import cfss.org.safetynet.SafetyNet;

/*************************************************
 *	Crab Factory Software Solutions
 *
 *	Software: 			SafetyNet
 *	Module: 			CrystalCaseFactory
 *
 *  Author:				http://sourceforge.net/projects/jtunes4/
 *
 *  Contributers:		Michael Spelling, Mark Wrightson
 *
 *  Description:
 *
 * Based on Source:
 * http://sourceforge.net/projects/jtunes4/
 *************************************************/

/**
 * Class that implements graphics for cover flow.
 */
public class CrystalCaseFactory {
    private static CrystalCaseFactory instance = null;

    /**
     * These values should really be generated in Image Flow
     * By Changing IMAGE_HEIGHT, to be greater than the height of the original
     * image, it adds a gap between the image and the reflection
     */
    //private static int IMAGE_WIDTH = 180;
    //private static int IMAGE_HEIGHT = 180;
    private static int IMAGE_WIDTH = 262;
    private static int IMAGE_HEIGHT = 233;

    private BufferedImage mask;

    /**
     * Gets instance.
     *
     * @return instance is the CrystalCaseFactory to return.
     */
    public static CrystalCaseFactory getInstance() {
        if (instance == null) {
            instance = new CrystalCaseFactory();
        }
        return instance;
    }

    /**
     * Constructor.
     */
    private CrystalCaseFactory() {
        mask = createGradientMask(IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    /**
     * Method to create buffered image.
     *
     * @param cover is the image to create.
     * @return crystal is the BuffereDImage to return.
     */
    public BufferedImage createCrystalCase(Image cover) {
        BufferedImage crystal = new BufferedImage(IMAGE_WIDTH,
                                                  IMAGE_HEIGHT,
                                                  BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = crystal.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int width = cover.getWidth(null);
        int height = cover.getHeight(null);

        float scale;

        if (width > height)
            scale = (float) IMAGE_WIDTH / (float) width;
        else
            scale = (float) IMAGE_HEIGHT / (float) height;

        int scaledWidth = (int) ((float) width * scale);
        int scaledHeight = (int) ((float) height * scale);

        int x = (IMAGE_WIDTH  - scaledWidth) / 2;
        int y = (IMAGE_HEIGHT  - scaledHeight) / 2;

        g2.drawImage(cover, x, y, scaledWidth, scaledHeight, null);

        g2.dispose();

        return crystal;
    }


    /**
     * Creates the image with reflection.
     *
     * @param avatar is the BufferedImage to create picture from.
     * @return the BufferedImage with reflection.
     */
    public BufferedImage createReflectedPicture(BufferedImage avatar) {
        return createReflectedPicture(avatar, mask);
    }

    /**
     * Creates reflected image with specified opacity.
     *
     * @param avatar the BufferedImage to create reflection from.
     * @param alphaMask the BufferedImage with alpha.
     * @return the BufferedImage with reflection.
     */
    public BufferedImage createReflectedPicture(BufferedImage avatar,
    		BufferedImage alphaMask) {
        int avatarWidth = avatar.getWidth();
        int avatarHeight = avatar.getHeight();

        //call createReflection to create the inverted image
        BufferedImage buffer = createReflection(avatar,
        		avatarWidth, avatarHeight);

        //apply an alpha mask to the reflection
        applyAlphaMask(buffer, alphaMask, avatarWidth, avatarHeight);

        return buffer;
    }

    /**
     * Applies the alpha value to the image.
     *
     * @param buffer is the BufferedImage to apply opacity to.
     * @param alphaMask BufferedImage with alpha.
     * @param avatarWidth is the integer width.
     * @param avatarHeight is the integer height.
     */
    private void applyAlphaMask(BufferedImage buffer,
    		BufferedImage alphaMask, int avatarWidth, int avatarHeight) {

        Graphics2D g2 = buffer.createGraphics();
        g2.setComposite(AlphaComposite.DstOut);
        g2.drawImage(alphaMask, null, 0, avatarHeight);
        g2.dispose();
    }

    /**
     * Method to create reflection.
     *
     * @param avatar the BufferedImage to use.
     * @param avatarWidth the integer width.
     * @param avatarHeight the integer height.
     * @return the BufferedImage to return.
     */
    private BufferedImage createReflection(BufferedImage avatar,
    	int avatarWidth, int avatarHeight) {

    	//create a buffered image twice the size of the original image
    	BufferedImage buffer = new BufferedImage(avatarWidth, avatarHeight << 1,
        	BufferedImage.TYPE_INT_ARGB);

    	//create the grtaphics component
    	Graphics2D g = buffer.createGraphics();

    	g.translate(0, (int)(avatarHeight*0.1));
    	//draw the original image into the new buffered image
        g.drawImage(avatar, null, null);
        g.translate(0, (int)(-avatarHeight*0.1));
        //change position of the origin within the graphics component
        //g.translate(0, avatarHeight << 1);
        g.translate(0, (int)(avatarHeight*1.9));

        //reflect the image
        AffineTransform reflectTransform = AffineTransform.getScaleInstance(
        		1.0, -1.0);
        //add the reflected image to the graphics object
        g.drawImage(avatar, reflectTransform, null);

        //reset origin within the graphics component
        //g.translate(0, -(avatarHeight << 1));
        g.translate(0, (int)(-avatarHeight*1.9));

        g.dispose();

        return buffer;
    }

    /**
     * Method to create gradient mask.
     *
     * @param avatarWidth the integer width of the avatar.
     * @param avatarHeight the integer height of the avatar.
     * @return gradient is the BufferedImage to return.
     */
    public BufferedImage createGradientMask(int avatarWidth, int avatarHeight) {
        BufferedImage gradient = new BufferedImage(avatarWidth, avatarHeight,
                                                   BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gradient.createGraphics();
        GradientPaint painter = new GradientPaint(0.0f, 0.0f,
                                                  new Color(1.0f, 1.0f, 1.0f, 0.5f),
                                                  0.0f, avatarHeight / 2.0f,
                                                  new Color(1.0f, 1.0f, 1.0f, 1.0f));
        g.setPaint(painter);
        g.fill(new Rectangle2D.Double(0, 0, avatarWidth, avatarHeight));

        g.dispose();

        return gradient;
    }


	/****************
	 * main
	 *
	 * test main method
	 ****************/
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		SafetyNet myApp = new SafetyNet();
	}
}