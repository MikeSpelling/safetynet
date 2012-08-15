/**
 * 		Crab Factory Software Solutions
 *
 * 		Software: SafetyNet
 * 		Module: Slide
 *
 * 		Author: Mark Mellar
 *
 * 		Contributers: Callum Goddard
 * 		Testers:
 *
 * 		Description:
 * 		Stores information pertaining to a slide and everything to be displayed
 * 		on it (quiz slides inherits most of its functionality from this)
 *
 */

package cfss.org.xmlstructure;

import java.awt.*;
import java.util.*;
/**
 * Stores information pertaining to a slide and everything to be displayed
 * on it (quiz slides inherits most of its functionality from this)
 * @author Mark Mellar
 *
 */
public class Slide extends TreeEntity{

	// background colour of slide
	private Color bkCol;
	//duration the slide is to be shown for (milliseconds)
	private int duration;
	//a vector of entities which are displayed on this slide
	private Vector<SlideEntity> entities;


	/**
	 *  constructor
	 */
	public Slide(){
		super();
		entities = new Vector<SlideEntity>();
	}

	/**
	 * copy constructor
	 * @param def the slide to be copied
	 */
	public Slide(Slide def) {
		super(def);
		//primitives
		this.duration = def.getDuration();
		//immutables
		this.bkCol = def.getBkCol();
		entities = new Vector<SlideEntity>();
		SlideEntity current;
		if (null != def.getEntities()){
			for (int i = 0; i < def.getEntities().size(); i++){
				current = def.getEntities().get(i);
				if (current instanceof Audio)
					entities.add(new Audio((Audio)current));
				else if (current instanceof Circle)
					entities.add(new Circle((Circle)current));
				else if (current instanceof Midi)
					entities.add(new Midi((Midi)current));
				else if (current instanceof XMLPolygon)
					entities.add(new XMLPolygon((XMLPolygon)current));
				else if (current instanceof ScrollPane)
					entities.add(new ScrollPane((ScrollPane)current));
				if (current instanceof Text)
					entities.add(new Text((Text)current));
				if (current instanceof Video)
					entities.add(new Video((Video)current));
				if (current instanceof XMLImage)
					entities.add(new XMLImage((XMLImage)current));
			}
		}
	}

	/**
	 *  returns bkCol
	 * @return the background colour of the slide
	 */
	public Color getBkCol() {
		return bkCol;
	}

	/**
	 *  returns duration
	 * @return the amount of time the slide is displayed (milliseconds)
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * returns entities
	 * @return a vector of entities which are to be displayed on the slide
	 */
	public Vector<SlideEntity> getEntities() {
		return entities;
	}

	/**
	 *  sets bkCol to bkCol
	 * @param bkCol the background colour of the slide
	 */
	public void setBkCol(Color bkCol) {
		this.bkCol = bkCol;
	}

	/**
	 *  sets duration to duration
	 * @param duration the amount of time the slide is to be displayed (milisecconds)
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * adds an entity to entities
	 * @param entity an entity to be displayed on the slide
	 */
	public void addEntity(SlideEntity entity) {
		this.entities.add(entity);
	}
}