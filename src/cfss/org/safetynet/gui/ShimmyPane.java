/************************************************
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		ShimmyPane
 *
 *  Author: 		Mark Wrightson
 *
 *  Contributers:	.
 *  Testers:		.
 *  Integrators:	.
 *
 *  Description:	The panel in which the Coverflow,
 *  SectionNavigation and MenuBar is loaded. A shimmypane
 *  provides the animation of sliding in and sliding out
 *
 ***********************************************/

package cfss.org.safetynet.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;
import javax.swing.Timer;

import cfss.org.flags.Debug;
import cfss.org.safetynet.SafetyNet;

/**
 * The panel in which the Coverflow, SectionNavigation and MenuBar is loaded.
 * A shimmypane provides the animation of sliding in and sliding out.
 *
 * @author Mark Wrightson
 *
 */
public class ShimmyPane{
	/**
	 * The shimmy panel that content is added to
	 */
	protected JPanel panel;
	/**
	 * The tab that appears on the edge of the screen
	 */
	protected JPanel tagPanel;			//shimmy grab tab
	/**
	 * Timer used to make the shimmy show effect
	 */
	protected Timer showTimer;
	/**
	 * Timer used to make the shimmy hide effect
	 */
    protected Timer hideTimer;
    /**
     * The direction from which the shimmy pane appears.
     * Possible values: "left","top","bottom"
     */
    private String direction=null;
    /**
     * variable used to specify the pixel offset of the shimmypane
     * as it moves
     */
    private int offset = 0;
    /**
     * The interval in milliseconds the timers use when showing or hiding
     * the shimmy pane
     */
    private int timerInterval = 1;
    /**
     * The number of pixels the shimmypane moves each time
     * the timer interrupts.
     */
    private int pixelDelta = 4;

    /**
     * whether a shimmy pane is visible or not
     */
    private boolean visible = false;

    @SuppressWarnings("unused")
	private boolean shimmyMoving = false;

    private boolean shimmyEnabled = true;


    /**
     * Constructor
     *
     * @param interval - set the timer interval
     * @param delta	- sets the number of pixels the shimmypane moves each timer interrupt
     * @param dir  -sets the direction of the movement
     */
    public ShimmyPane(int interval, int delta, String dir){
    	timerInterval = interval;
		pixelDelta = delta;
		direction = dir;
		if(Debug.shimmy)System.out.println("Shimmy Pane");
    	//instantiate the shimmypane
		panel = new JPanel();
		panel.setLayout(null);
		panel.setOpaque(false);
		//must be set as invisible to prevent it 'flashing' on 1st animation
		//panel.setVisible(true);
		visible = false;

		//instantiate the grab tab
		tagPanel = new JPanel();
		tagPanel.setLayout(null);
		//set tagPanel as transparent
		//tagPanel.setBackground(new Color(255, 255, 255,0));
		tagPanel.setOpaque(false);
		tagPanel.setVisible(true);

		Gui.getLayeredPane().add(panel,new Integer(400));
		//ensure tagPanel displays on top
		Gui.getLayeredPane().add(tagPanel,new Integer(401));

		//instantiate the timers
		showTimer = new ShowTimer();
	    hideTimer = new HideTimer();
	}

    /**
     * method called when the shimmyIn commences
     */
    public void shimmyInStarted() {
    	//Methods to be overridden by child class
	}

    /**
     * method called when then ShimmyOut commences
     */
    public void shimmyOutStarted() {
    	//Methods to be overridden by child class
	}

    /**
     * method called when then ShimmyIn finished
     */
    public void shimmyInStopped() {
    	//Methods to be overridden by child class
	}

    /**
     * method called when then ShimmyOut finishes
     */
    public void shimmyOutStopped() {
    	//Methods to be overridden by child class
	}

	/****************
	 * setPosition
	 *
	 * moves the panel by a specified offset to make a shimmy pane
	 * move in and out
	 ****************/
	private void setPostion(final int offset){
        int x = panel.getX();
        int y = panel.getY();
        int x2 = tagPanel.getX();
        int y2 = tagPanel.getY();
        if(Debug.shimmy)System.out.println("   offset = " + offset);

        if(direction=="left" || direction=="right"){
        		x=offset;
        		x2=offset+panel.getWidth();
        }
        else if(direction=="up"){
        	y=offset;
        }
        else if(direction=="down"){
        	y=offset;
        	y2 = offset-tagPanel.getHeight();
        }

        panel.setLocation(new Point(x,y));
        tagPanel.setLocation(new Point(x2,y2));
        panel.repaint();

        if(Debug.shimmy)System.out.println("Set Location="+ x+","+y);
        if(Debug.shimmy)System.out.println("Set Location2="+ x2+","+y2);
    }

	/****************
	 * showTimer
	 *
	 * this is the timer that makes a shimmy pane display
	 ****************/
    private class ShowTimer extends Timer implements ActionListener
    {
        /**
		 *
		 */
		private static final long serialVersionUID = 1L;


		public ShowTimer(){
            super(timerInterval, null);   // call back in milliseconds
            addActionListener(this);
        }


    	/**
    	 * Starts the Timer,
         * causing it to start sending action events
         * to its listeners.
    	 ****************/
        @Override
		public void start()
        {
        	if(isRunning()){
        		return;
        	}
        	hideTimer.stop();
        	if(direction=="left") offset = -panel.getWidth();
        	if(direction=="down") offset = Gui.getContentPane().getHeight();
        	if(direction=="up") offset = -panel.getHeight();
            //panel.setVisible(true);
        	visible = true;
            tagPanel.setVisible(false);
            shimmyMoving = true;
            shimmyInStarted();
            super.start();
        }

        /**
         * Stops the timer.
         */
        public void stop(){
        	if(!super.isRunning())return;
        	shimmyMoving = false;
        	shimmyInStopped();
        	super.stop();
        }


        /**
         * Timer interrupt method
         */
        public void actionPerformed(ActionEvent e)
        {
        	if(Debug.shimmy)System.out.println("ShowTimer.actionPerformed() Offset: " + offset);


            if(direction=="left" || direction=="up"){
	            if(offset <= 0)
	            	setPostion(offset);
	            else stop();

	            offset += pixelDelta;
            }
            if(direction=="down"){
	            if(offset >= Gui.getContentPane().getHeight()-panel.getHeight())
	            	setPostion(offset);
	            else stop();

	            offset -= pixelDelta;
             }
        }
    }

	/****************
	 * hideTimer
	 *
	 * this is the timer that makes a shimmy pane hide
	 ****************/
    private class HideTimer extends Timer implements ActionListener{

		private static final long serialVersionUID = 1L;

		public HideTimer(){
	        // first param is callback interval in milliseconds
	        super(timerInterval, null);   // call back in millis
	        addActionListener(this);
        }
    	@Override
		public void start()
        {
    		if(isRunning()){
        		return;
        	}
    		showTimer.stop();
    		if(direction=="left") offset = panel.getX();
    		else if(direction=="down") offset = panel.getY();
    		else if(direction=="up") offset = panel.getY();
    		if(Debug.shimmy)System.out.println("hide start. offset:" + offset);
    		shimmyMoving = true;
    		shimmyOutStarted();
            super.start();
        }

        @Override
        /**
         * stops the timer
         */
		public void stop()
        {
        	if(!super.isRunning())return;
            //panel.setVisible(false);
        	visible = false;
            tagPanel.setVisible(true);
            shimmyMoving = false;
            shimmyOutStopped();
            super.stop();
        }

        /**
         * timer interrupt method
         */
        public void actionPerformed(ActionEvent e)
        {
        	if(Debug.shimmy)System.out.println("HideTimer.actionPerformed() horizOffset: " + offset);

            if(direction =="left"){
	            if(offset > -panel.getWidth()){
	            	offset -= pixelDelta;
	                setPostion(offset);

	            }
	            else stop();
            }
            if(direction =="down"){

            	if(offset < Gui.getContentPane().getHeight())
	            {
	                offset += pixelDelta;
	                setPostion(offset);
	            }
	            else stop();
            }
            if(direction =="up"){

            	if(offset > -panel.getHeight()){
	                offset -= pixelDelta;
	                setPostion(offset);
	            }
	            else stop();
            }
        }
    }

	/****************
	 * LoadImage
	 *
	 * this class loads in the image to display the grab tabs
	 ****************/
/*	protected class LoadImage extends Component {

	    BufferedImage img;
	    public LoadImage(String s) {
	       try {
	           img = ImageIO.read(new File(s));
	       } catch (IOException e) {
	    	   System.out.println("File not Found");
	       }
	    }

	    public Dimension getPreferredSize() {
	        if (img == null) {
	             return new Dimension(100,100);
	        } else {
	           return new Dimension(img.getWidth(null), img.getHeight(null));
	       }
	    }

	    public void paint(Graphics g) {
	        g.drawImage(img, 0, 0, getWidth(),getHeight(),null);
	    }
	}*/

	/****************
	 * LoadImage
	 *
	 * this class loads in the image to display the grab tabs
	 ****************/
	protected class LoadImage extends Component{

		private static final long serialVersionUID = 1L;
		private MediaTracker imageTracker;
		Image img;
		/**
		 * Loads image.
		 * @param s a String.
		 */
	    public LoadImage(String s) {
	       //img = ImageIO.read(new File(s));
	    	//img = Toolkit.getDefaultToolkit().createImage(s);
	    	img = Toolkit.getDefaultToolkit().createImage(s);
	    	imageTracker = new MediaTracker(this);
	    	imageTracker.addImage(img,0);

			if(false == imageTracker.checkID(0))
			{
				try {
					if(Debug.shimmy)System.out.println("loading");
					imageTracker.waitForID(0);	//This line loads the image
					if(Debug.shimmy)System.out.println("loaded");
				} catch (InterruptedException e) {
					System.out.println("originalImage loading interrupted");
					e.printStackTrace();
				}
			}
	    }
	    /**
	     * Gets preferred size.
	     *
	     * @return The preferred dimension.
	     */
	    public Dimension getPreferredSize() {
	        if (img == null) {
	             return new Dimension(100,100);
	        } else {
	           return new Dimension(img.getWidth(null), img.getHeight(null));
	        }
	    }

	    /**
	     * Overriden paint method.
	     */
	   public void paint(Graphics g) {
	        g.drawImage(img, 0, 0, getWidth(),getHeight(),null);
	    }
	}

	/**
	 * Determines whether the shimmyPane is visible or not
	 */
	public boolean isShimmyVisible() {
		return visible;
	}

	/****************
	 * main
	 *
	 * test main method
	 ****************/
	public static void main(String[] args) {
		new SafetyNet();
	}

	/**
	 * Gets whether shimmy is enabled or not.
	 *
	 * @return himmyEnabled the boolean to return.
	 */
	public boolean isShimmyEnabled() {
		return shimmyEnabled;
	}

	/**
	 * Sets shimmyEnabled variable.
	 *
	 * @param shimmyEnabled the boolean to set it to.
	 */
	public void setShimmyEnabled(boolean shimmyEnabled) {
		this.shimmyEnabled = shimmyEnabled;
	}
}
