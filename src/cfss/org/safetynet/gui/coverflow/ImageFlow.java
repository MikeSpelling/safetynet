package cfss.org.safetynet.gui.coverflow;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.*;

import cfss.org.flags.Debug;
import cfss.org.safetynet.Engine;
import cfss.org.xmlstructure.TreeEntity;

/*************************************************
 *	Crab Factory Software Solutions
 *
 *	Software: 			SafetyNet
 *	Module: 			ImageFlow
 *
 *  Author:				http://sourceforge.net/projects/jtunes4/
 *
 *  Contributers: 		Michael Spelling, David Walker, Mark Wrightson
 *
 *  Description: loadFromDirectory(File directory) - slide name is set in here
 *
 * displayheight, width must be set for a 4:3 aspect ratio image
 *
 * CDSIZE specifies the container that the image is displayed in.
 * it doesn't account for aspect ratio of the image.
 *
 * The image generated with the reflection is done inside CrystalCaseFactory
 * The size in there specifies the height & width of the actual image.
 * This keeps the aspect ratio (i think).  By increasing the height variable
 * to be greater than the height of the image a gap can be created between the
 * image and the reflected image
 *
 * Based on Source:
 * http://sourceforge.net/projects/jtunes4/
 *
 * @author http://sourceforge.net/projects/jtunes4/
 *************************************************/
public class ImageFlow extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final double ANIM_SCROLL_DELAY = 400;

	/*
    * The specified height must account for:
    * The original image height + the reflected image
    */
	private static final int CD_SIZE = 200;	//width of an avatar
	private int displayHeight = (int) (CD_SIZE * 2 / 1.3); //height of an avatar

    private int displayWidth = CD_SIZE;

    private List<ImageFlowItem> avatars = null;
    private String avatarText = null;

    private boolean loadingDone = false;

    private Thread picturesFinder = null;
    private Timer scrollerTimer = null;
    private Timer faderTimer = null;

    private float veilAlphaLevel = 0.0f;
    private float alphaLevel = 0.5f;
    private float textAlphaLevel = 0.5f;

    private int avatarIndex = -1; // The currently selected avatar
    private double avatarPosition = 0;

    /**
     * sets the spacing of the avatars
     * Range 0 -> 1.0
     * Default (0.3) can be set here.
     * setAvatarSpacing() can be used to dynamically change the spacing
     */
    private double avatarSpacing = 0.3;

    /**
     * A pointer to the list of avatars.
     */
    private int avatarAmount = 1;

    private double sigma;
    private double rho;

    private double exp_multiplier;
    private double exp_member;

    private boolean damaged = true;

    private DrawableAvatar[] drawableAvatars;

    private FocusGrabber focusGrabber;
    private AvatarScroller avatarScroller;
    private MouseAvatarSelector mouseAvatarSelector;
    private CursorChanger cursorChanger;
    private MouseWheelScroller wheelScroller;
    private KeyScroller keyScroller;
    private KeyAvatarSelector keyAvatarSelector;

    private Font avatarFont;
    private CrystalCaseFactory fx;

    private List<ListSelectionListener> listSelectionListeners;


    /**
     * Constructor which takes a vector of TreeEntity's and loads the
     * a list of ImageFlowItems for them. It then calls the overloaded
     * constructor containing the list of ImageFlowItems.
     *
     * @param treeEntityVector - containing TreeEntities with the data of the
     * paths for all images to be loaded from.
     */
    public ImageFlow(Vector <TreeEntity> treeEntityVector) {
    	this(ImageFlowItem.loadFromTreeVector(treeEntityVector));
    }

    /**
     * Overloaded constructor creates the coverflow from the list.
     *
     * @param items is the list of ImageFlowItems to display.
     */
    public ImageFlow(List<ImageFlowItem> items) {
    	avatars = items;
        avatarFont = new Font("Dialog", Font.PLAIN, 24);
        fx = CrystalCaseFactory.getInstance();

        loadAvatars();

        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        setSigma(0.5);	// Sets the perceived depth

        addComponentListener(new DamageManager());

        initInputListeners();
        addInputListeners();

        listSelectionListeners = new ArrayList<ListSelectionListener>();
    }

    /**
     * Sets the amount of avatars.
     *
     * @param amount the amount of avatars.
     */
    public void setAmount(int amount) {
        if (amount > avatars.size())
            throw new IllegalArgumentException("Too many avatars");
        this.avatarAmount = amount;
        repaint();
    }


    /**
     * Sets the position in avatars.
     *
     * @param position is the avatar position.
     */
    private void setPosition(double position) {
        this.avatarPosition = position;
        this.damaged = true;
        repaint();
    }


    /**
     * Sets the sigma value and performs calculations.
     *
     * @param sigma
     */
    public void setSigma(double sigma) {
        this.sigma = sigma;
        this.rho = 1.0;
        computeEquationParts();
        this.rho = computeModifierUnprotected(0.0);
        computeEquationParts();
        this.damaged = true;
        repaint();
    }


    /**
     * Sets the spacing for the avatars.
     * @param avatarSpacing is the distance the avatars will be spaced by.
     */
    public void setSpacing(double avatarSpacing) {
        if (avatarSpacing < 0.0 || avatarSpacing > 1.0)
            throw new IllegalArgumentException("Spacing must be < 1.0 and " +
            		"> 0.0");
        this.avatarSpacing = avatarSpacing;
        this.damaged = true;
        repaint();
    }


    /**
     * Gets the preferred size.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(displayWidth * 5, (int) (displayHeight * 3));
    }


    /**
     * Gets the minimum size.
     */
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }


    /**
     * Returns true if opaque.
     */
    @Override
    public boolean isOpaque() {
        return false;
    }


    /**
     * Returns true if component is focusable.
     */
    @Override
    public boolean isFocusable() {
        return true;
    }


    /**
     * Gets focus.
     */
    public void FocusGrab() {
    	requestFocus();
    }


    /**
     * Paints all components.
     */
    @Override
    protected void paintChildren(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        Composite oldComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, veilAlphaLevel));
        super.paintChildren(g);
        g2.setComposite(oldComposite);
    }


    /**
     * Overidden method paints the component.
     *
     * @param g is the graphics to paint.
     */
    @Override
    protected void paintComponent(Graphics g) {
        if(!isShowing()) return;

        super.paintComponent(g);

        if(!loadingDone && faderTimer == null) return;

        Insets insets = getInsets();

        int x = insets.left;
        int y = insets.top;

        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom - 100;	//offset from centre of screen

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        Composite oldComposite = g2.getComposite();

        if(damaged) {
            drawableAvatars = sortAvatarsByDepth(x, y, width, height);
            damaged = false;
        }

        drawAvatars(g2, drawableAvatars);

        if(drawableAvatars.length > 0) {
            drawAvatarName(g2);
        }

        g2.setComposite(oldComposite);
    }


    /**
     * Draws the Images onscreen.
     * @param g2 is the graphics.
     * @param drawableAvatars is the array of avatars to draw.
     */
    private void drawAvatars(Graphics2D g2, DrawableAvatar[] drawableAvatars) {
        for (DrawableAvatar avatar: drawableAvatars) {
        	AlphaComposite composite =
        		AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
        		(float)avatar.getAlpha());
            g2.setComposite(composite);
            g2.drawImage(avatars.get(avatar.getIndex()).getImage(),
                         (int) avatar.getX(), (int) avatar.getY(),
                         avatar.getWidth(), avatar.getHeight(), null);
        }
    }


    /**
     * Sorts the avatars by depth.
     *
     * @param x is the x position.
     * @param y is the y position.
     * @param width is the width.
     * @param height is the height.
     * @return the array of avatars to draw.
     */
    private DrawableAvatar[] sortAvatarsByDepth(int x, int y,
    	int width, int height) {
        List<DrawableAvatar> drawables = new LinkedList<DrawableAvatar>();

        for (int i = 0; i < avatars.size(); i++)
            promoteAvatarToDrawable(drawables, x, y, width, height,
            		i - avatarIndex);

        DrawableAvatar[] drawableAvatars = new DrawableAvatar[drawables.size()];
        drawableAvatars = drawables.toArray(drawableAvatars);
        Arrays.sort(drawableAvatars);
        return drawableAvatars;
    }


    /**
     * Draws the name of the slide.
     * @param g2
     */
    private void drawAvatarName(Graphics2D g2) {
        Composite composite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                   textAlphaLevel));

        FontRenderContext context = g2.getFontRenderContext();
        TextLayout layout = new TextLayout(avatarText, avatarFont, context);
        Rectangle2D bounds = layout.getBounds();

        double bulletWidth = bounds.getWidth() + 12;
        double bulletHeight = bounds.getHeight() + layout.getDescent() + 4;

        // Sets the position of the reflected text
        double x = (getWidth() - bulletWidth) / 2.0;

        double y = (CD_SIZE)/1.5;

        BufferedImage textImage = new BufferedImage((int)bulletWidth,
        		(int)bulletHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2text = textImage.createGraphics();

        // Sets the colour of the text to be displayed
        g2text.setColor(new Color(0, 0, 0, 170));
        layout.draw(g2text, 6, layout.getAscent() + 1);
        g2text.dispose();

        // This creates the reflection of the text
        g2.drawImage(fx.createReflectedPicture(textImage,
        		fx.createGradientMask((int) bulletWidth,
                (int) bulletHeight)),(int) x, (int) y, null);
        g2.setComposite(composite);
    }


    /**
     * Makes an avatar drawable.
     *
     * @param drawables is the List of Avatars.
     * @param x is the integer x position.
     * @param y is the integer y position.
     * @param width is the integer width.
     * @param height is the integer height.
     * @param offset if the integer offset.
     */
    private void promoteAvatarToDrawable(List<DrawableAvatar> drawables,
    	int x, int y, int width, int height, int offset) {

        double spacing = offset * avatarSpacing;
        double avatarPosition = this.avatarPosition + spacing;

        if (avatarIndex + offset < 0 ||
            avatarIndex + offset >= avatars.size()) {
            return;
        }

        int avatarWidth = displayWidth;
        int avatarHeight = displayHeight;

        double result = computeModifier(avatarPosition);
        int newWidth = (int)(avatarWidth * result);
        if(newWidth == 0)
            return;
        int newHeight = (int)(avatarHeight * result);
        if(newHeight == 0)
            return;

        double avatar_x = x + (width - newWidth) / 2.0;
        double avatar_y = y + (height - newHeight / 2.0) / 2.0;

        double semiWidth = width / 2.0;
        avatar_x += avatarPosition * semiWidth;

        if (avatar_x >= width || avatar_x < -newWidth) {
            return;
        }

        drawables.add(new DrawableAvatar(avatarIndex + offset,
        	avatar_x, avatar_y,  newWidth, newHeight, avatarPosition, result));
    }


    /**
     * Calculates multiplier and member.
     */
    private void computeEquationParts() {
        exp_multiplier = Math.sqrt(2.0 * Math.PI) / sigma / rho;
        exp_member = 4.0 * sigma * sigma;
    }


    /**
     * Calculates the modifier.
     *
     * @param x is the double to mulitply.
     * @return the modifier
     */
    private double computeModifier(double x) {
        double result = computeModifierUnprotected(x);
        if (result > 1.0) result = 1.0;
        else if (result < -1.0) result = -1.0;
        return result;
    }


    /**
     * Calculates the modifier without error handling.
     * @param x is the double to multiply.
     * @return the modifier
     */
    private double computeModifierUnprotected(double x) {
        return exp_multiplier * Math.exp((-x * x) / exp_member);
    }


    /**
     * Creates the listeners for mouse and keyboard.
     */
    private void addInputListeners() {
        addMouseListener(focusGrabber);
        addMouseListener(avatarScroller);
        addMouseListener(mouseAvatarSelector);
        addMouseMotionListener(cursorChanger);
        addMouseWheelListener(wheelScroller);
        addKeyListener(keyScroller);
        addKeyListener(keyAvatarSelector);
    }


    /**
     * Initialises the listeners.
     */
    private void initInputListeners() {
        // Input listeners are all stateless hence they can only be
    	// instantiated once.
        focusGrabber = new FocusGrabber();
        avatarScroller = new AvatarScroller();
        mouseAvatarSelector = new MouseAvatarSelector();
        cursorChanger = new CursorChanger();
        wheelScroller = new MouseWheelScroller();
        keyScroller = new KeyScroller();
        keyAvatarSelector = new KeyAvatarSelector();
    }


    /**
     * Loads the avatars.
     */
    private void loadAvatars() {
        picturesFinder = new Thread(new PicturesFinderThread());
        picturesFinder.setPriority(Thread.MIN_PRIORITY);
        picturesFinder.start();
    }


    /**
     * Sets the avatars to be at the specified index.
     * @param index to set the avatars to.
     */
    public void setAvatarIndex(int index) {
        avatarIndex = index;
        if(!avatars.isEmpty()){
        	avatarText = avatars.get(index).getLabel();
        	notifyListSelectionListener();
        }
    }


    /**
     * Sets the amount the mouse wheel will scroll by.
     * @param increment is the number of images to scroll by each turn.
     */
    private void scrollBy(int increment) {
        if (loadingDone) {
            setAvatarIndex(avatarIndex + increment);
            if (avatarIndex < 0)
                setAvatarIndex(0);
            else if (avatarIndex >= avatars.size())
                setAvatarIndex(avatars.size() - 1);
            damaged = true;
            repaint();
        }
    }


    /**
     * MW - additional scrollTo method
     * @param index
     */
    public void scrollTo(int index) {
        if (loadingDone) {
            setAvatarIndex(index);
            if (index < 0)
                setAvatarIndex(0);
            else if (index >= avatars.size())
                setAvatarIndex(avatars.size() - 1);
            damaged = true;
            repaint();
        }
        else
        	if(Debug.coverflow) System.out.println("scrollTo() - " +
        			"loading not done");
    }


    /**
     * Scrolls and animates the coverflow by the amount specified.
     * @param increment the number of images to scroll by.
     */
    private void scrollAndAnimateBy(int increment) {
        if(loadingDone && (scrollerTimer == null || !scrollerTimer.isRunning())) {
            int index = avatarIndex + increment;
            if(index < 0)
                index = 0;
            else if(index >= avatars.size())
                index = avatars.size() - 1;

            DrawableAvatar drawable = null;

            if (drawableAvatars != null) {
                for(DrawableAvatar avatar: drawableAvatars) {
                    if(avatar.index == index) {
                        drawable = avatar;
                        break;
                    }
                }
            }
            if (drawable != null) {
                scrollAndAnimate(drawable);
            }
        }
    }


    /**
     * MW - additional scrollAndAnimateTo method
     * @param index
     */
    public void scrollAndAnimateTo(int index) {
        if(loadingDone && (scrollerTimer == null ||
        		!scrollerTimer.isRunning())) {
            if (index < 0)
                index = 0;
            else if (index >= avatars.size())
                index = avatars.size() - 1;

            DrawableAvatar drawable = null;

            if(drawableAvatars != null) {
                for (DrawableAvatar avatar: drawableAvatars) {
                    if (avatar.index == index) {
                        drawable = avatar;
                        break;
                    }
                }
            }
            if(drawable != null)
                scrollAndAnimate(drawable);
            else
            	setSelectedIndex(index);
        }
    }


    /**
     * Scrolls and animates the avatar.
     * @param avatar to animate.
     */
    private void scrollAndAnimate(DrawableAvatar avatar) {
        if (loadingDone) {
            scrollerTimer = new Timer(33, new AutoScroller(avatar));
            scrollerTimer.start();
        }
    }


    private DrawableAvatar getHitAvatar(int x, int y) {
        for (DrawableAvatar avatar: drawableAvatars) {
            Rectangle hit = new Rectangle((int) avatar.getX(), (int) avatar.getY(),
            	avatar.getWidth(), avatar.getHeight() / 2);
            if (hit.contains(x, y)) {
                return avatar;
            }
        }
        return null;
    }


    private void startFader() {
        faderTimer = new Timer(35, new FaderAction());
        faderTimer.start();
    }


    ///////////////////
     // List Methods //
     //////////////////

    /**
     * Returns the largest selected cell index
     *
     * @return int the largest selected cell index
     */
    public int getMaxSelectionIndex() {
        if (this.avatars == null) return 0;
        else return avatars.size();
    }


    /**
     * Returns the smallest selected cell index
     *
     * @return int the largest selected cell index
     */
    public int getMinSelectionIndex() {
        return 0;
    }


    /**
     * Returns the first selected index; returns -1 if there is no selected item
     * @return int
     */
    public int getSelectedIndex()  {
        return avatarIndex;
    }


    /**
     * Returns the first selected value, or null if the selection is empty.
     * @return Object
     */
    public Object getSelectedValue() {
        if (this.avatars == null)
            return null;

        return avatars.get(avatarIndex);
    }


    /**
     * Returns true if the specified index is selected.
     * @param index int
     * @return boolean
     */
    public boolean isSelectedIndex(int index) {
        return (avatarIndex == index);
    }

    /**
     * Selects a single cell
     * @param index int
     */
    public void setSelectedIndex(int index)  {
        this.scrollBy(index - avatarIndex);
    }

    /**
     * Adds a listener to the list that's notified each time a change to the selection occur
     * @param listener ListSelectionListener
     */
    public void addListSelectionListener(ListSelectionListener listener)  {
        listSelectionListeners.add(listener);
    }

    /**
     * Removes a listener from the list that's notified each time a change to the selection occurs
     * @param listener ListSelectionListener
     */
    public void removeListSelectionListener(ListSelectionListener listener) {
        listSelectionListeners.remove(listener);
    }

    /**
     * Notify the listeners when a selection event has occured
     */
    private void notifyListSelectionListener()  {
        ListSelectionEvent event = new ListSelectionEvent(this, avatarIndex, avatarIndex, false);

        for (ListSelectionListener listener : listSelectionListeners)
            listener.valueChanged(event);
    }



    private class PicturesFinderThread implements Runnable {
    	// Empty constructor prints error if needed
        public PicturesFinderThread() {
            try{}
            catch (Exception e){e.printStackTrace();}
        }

        // Called automatically when running thread
        public void run() {
            for (ImageFlowItem item: avatars) {
                try {item.getImage();} //force image to load
                catch (Exception e) {e.printStackTrace();}
            }

            setAvatarIndex(avatarAmount / 2);
            startFader();

            loadingDone = true;

            repaint();
        }
    }

    private class FaderAction implements ActionListener {
        private long start = 0;

        private FaderAction() {
            alphaLevel = 0.0f;
            textAlphaLevel = 0.0f;
        }

        public void actionPerformed(ActionEvent e) {
            if (start == 0) {
                start = System.currentTimeMillis();
            }

            alphaLevel = (System.currentTimeMillis() - start) / 500.0f;
            textAlphaLevel = alphaLevel;
            if (alphaLevel > 1.0f) {
                alphaLevel = 1.0f;
                textAlphaLevel = 1.0f;
                faderTimer.stop();
            }
            repaint();
        }
    }

    @SuppressWarnings("unchecked")
	private class DrawableAvatar implements Comparable {
        private int index;
        private double x;
        private double y;
        private int width;
        private int height;
        private double zOrder;
        private double position;

        private DrawableAvatar(int index,
                               double x, double y, int width, int height,
                               double position, double zOrder) {
            this.index = index;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.position = position;
            this.zOrder = zOrder;
        }

        public int compareTo(Object o) {
            double zOrder2 = ((DrawableAvatar) o).zOrder;
            if (zOrder < zOrder2) {
                return -1;
            } else if (zOrder > zOrder2) {
                return 1;
            }
            return 0;
        }

        public double getPosition() {
            return position;
        }

        public double getAlpha() {
            return zOrder * alphaLevel;
        }

        public int getHeight() {
            return height;
        }

        public int getIndex() {
            return index;
        }

        public int getWidth() {
            return width;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }

    private class MouseWheelScroller implements MouseWheelListener {
        public void mouseWheelMoved(MouseWheelEvent e) {
            int increment = e.getWheelRotation();
            scrollAndAnimateBy(increment);
        }
    }

    /**
     * Handle key presses to animate and display previews or go to slide.
     */
    private class KeyScroller extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    scrollAndAnimateBy(-1);
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    scrollAndAnimateBy(1);
                    break;
                case KeyEvent.VK_END:
                    scrollBy(avatars.size() - avatarIndex - 1);
                    break;
                case KeyEvent.VK_HOME:
                    scrollBy((0 - avatarIndex));
                    break;
                case KeyEvent.VK_PAGE_UP:
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    scrollAndAnimateBy(-3);
                    break;
                case KeyEvent.VK_PAGE_DOWN:
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    scrollAndAnimateBy(3);
                    break;
                // Mike S: Added go to slide functionality.
                case KeyEvent.VK_ENTER:
                	Engine.gotoSlide(avatarIndex + 1);
            }
        }
    }


    private class FocusGrabber extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            requestFocus();
        }
    }


    /**
     * Handle mouse clicks to animate previews and go to slide.
     *
     * Mike S: Added functionality to move slides.
     */
    private class AvatarScroller extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if((scrollerTimer != null && scrollerTimer.isRunning()) ||
                drawableAvatars == null)
                return;

            if(e.getButton() == MouseEvent.BUTTON1) {
                DrawableAvatar avatar = getHitAvatar(e.getX(), e.getY());
                try {
	                if(avatar.getIndex() == avatarIndex)
	                	Engine.gotoSlide(avatarIndex + 1);
	                else if (avatar != null)
	                    scrollAndAnimate(avatar);
                }
                catch(NullPointerException npe) {}
            }
        }
    }

    private class DamageManager extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            damaged = true;
        }
    }

    private class AutoScroller implements ActionListener {
        private double position;
        private int index;
        private long start;

        private AutoScroller(DrawableAvatar avatar) {
            this.index = avatar.getIndex();
            this.position = avatar.getPosition();
            this.start = System.currentTimeMillis();
        }

        public void actionPerformed(ActionEvent e) {
            long elapsed = System.currentTimeMillis() - start;
            if (elapsed < ANIM_SCROLL_DELAY / 2.0)
                textAlphaLevel = (float) (1.0 - 2.0 * (elapsed / ANIM_SCROLL_DELAY));
            else {
                avatarText = avatars.get(index).getLabel();
                textAlphaLevel = (float) (((elapsed / ANIM_SCROLL_DELAY) - 0.5) * 2.0);
                if (textAlphaLevel > 1.0f)
                    textAlphaLevel = 1.0f;
            }
            if (textAlphaLevel < 0.1f)
                textAlphaLevel = 0.1f;
            double newPosition = (elapsed / ANIM_SCROLL_DELAY) * -position;

            if(elapsed >= ANIM_SCROLL_DELAY) {
                ((Timer) e.getSource()).stop();
                setAvatarIndex(index);
                setPosition(0.0);
                return;
            }
            setPosition(newPosition);
        }
    }


    /**
     * Makes cursor a hand if over a slide preview
     */
    private class CursorChanger extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            if ((scrollerTimer != null && scrollerTimer.isRunning()) ||
                drawableAvatars == null) {
                return;
            }

            DrawableAvatar avatar = getHitAvatar(e.getX(), e.getY());
            if (avatar != null) {
                getParent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                getParent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }

    private class KeyAvatarSelector extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if ((scrollerTimer == null || !scrollerTimer.isRunning()) &&
                drawableAvatars != null) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                }
            }
        }
    }

    private class MouseAvatarSelector extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if ((scrollerTimer == null || !scrollerTimer.isRunning()) &&
                drawableAvatars != null) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    DrawableAvatar avatar = getHitAvatar(e.getX(), e.getY());
                    if (avatar != null && avatar.getIndex() == avatarIndex) {
                    }
                }
            }
        }
    }
}