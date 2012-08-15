/**
 * 		Crab Factory Software Solutions
 *
 * 		Software: 		SafetyNet
 * 		Module: 		ScrollPane
 *
 * 		Author: 		Mark Mellar
 *
 * 		Contributers: 	Michael Spelling, Callum Goddard
 * 		Testers:
 *
 * 		Description: Stores information pertaining to a scroll pane and its
 * 					 contents.
 */

package cfss.org.xmlstructure;

import java.awt.Color;
import java.util.Vector;
/**
 *  Stores information pertaining to a scroll pane and its
 * 	contents.
 * @author Mark Mellar
 *
 */
public final class ScrollPane extends SlideEntity{

	/**
	 * Transparency of the pane.
	 */
	private float bkAlpha;
	/**
	 * Background colour of the pane.
	 */
	private Color bkCol;
	/**
	 * Vector of entities to be put inside the JScrollPane.
	 */
	private Vector<SlideEntity> entities;
	/**
	 * Height of the pane.
	 */
	private int height;
	/**
	 * Width of the pane.
	 */
	private int width;


	/**
	 * Constructor.
	 */
	public ScrollPane(){
		super();
	}


	/**
	 * Copy constructor.
	 *
	 * @param def ScrollPane to copy.
	 */
	public ScrollPane(ScrollPane def){
		super(def);
		bkCol = def.getBkCol();
		bkAlpha = def.getBkAlpha();
		width = def.getWidth();
		height = def.getHeight();
		// Copy each individual object using the appropriate copy constructor
		entities = new Vector<SlideEntity>();
		if (null != def.getEntities()){
			for (int i = 0; i < def.getEntities().size(); i++){
				SlideEntity cur = def.getEntities().get(i);
				if (cur instanceof XMLImage)
					entities.add(new XMLImage((XMLImage)cur));
				if (cur instanceof Circle)
					entities.add(new Circle((Circle)cur));
				if (cur instanceof XMLPolygon)
					entities.add(new XMLPolygon((XMLPolygon)cur));
				if (cur instanceof Text)
					entities.add(new Text((Text)cur));
				if (cur instanceof Midi)
					entities.add(new Midi((Midi)cur));
				if (cur instanceof Video)
					entities.add(new Video((Video)cur));
				if (cur instanceof Audio)
					entities.add(new Audio((Audio)cur));
			}
		}
	}


	/**
	 * Adds an entity to the vector entities.
	 *
	 * @param entity entity to add to pane.
	 */
	public void addEntity(SlideEntity entity){
		// No scrollpanes allowed
		if (!(entity instanceof ScrollPane))
			entities.add(entity);
	}


	/**
	 * Returns bkalpha.
	 *
	 * @return the background transparency of the pane.
	 */
	public Float getBkAlpha() {
		return bkAlpha;
	}


	/**
	 * Returns bkcol.
	 *
	 * @return the background colour of the pane.
	 */
	public Color getBkCol() {
		return bkCol;
	}


	/**
	 * Returns entities.
	 *
	 * @return a vector of entities in the pane.
	 */
	public  Vector<SlideEntity> getEntities(){
		return entities;
	}


	/**
	 * Returns height.
	 *
	 * @return the height of the pane in pixels.
	 */
	public int getHeight() {
		return height;
	}


	/**
	 * Returns width.
	 *
	 * @return the width of the pane in pixels.
	 */
	public int getWidth() {
		return width;
	}


	/**
	 * Sets bkalpha.
	 *
	 * @param anAlpha the background transparency of the pane.
	 */
	public void setBkAlpha(Float anAlpha) {
		if(anAlpha >= 1.0f)
			this.bkAlpha = 1.0f;
		else if(anAlpha <= 0.0f)
			this.bkAlpha = 0.0f;
		else
			this.bkAlpha = anAlpha;
	}


	/**
	 * Sets bkcol.
	 *
	 * @param aColour the colour of the background of the slide.
	 */
	public void setBkCol(Color aColour) {
		this.bkCol = aColour;
	}

	/**
	 * Sets height.
	 *
	 * @param height the height of the slide in pixels.
	 */
	public void setHeight(int height) {
		this.height = height;
	}


	/**
	 * Sets width.
	 *
	 * @param width the width of the slide in pixels.
	 */
	public void setWidth(int width) {
		this.width = width;
	}
}