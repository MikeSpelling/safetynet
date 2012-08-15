/**
 * 		Crab Factory Software Solutions
 *
 * 		Software: S		afetyNet
 * 		Module: 		ClickableEntity
 *
 * 		Author: 		Mark Mellar
 *
 * 		Contributers: 	Michael Spelling, Callum Goddard
 * 		Testers:
 *
 * 		Description:
 * 		Stores information pertaining to an entity displayed to a slide and may
 * 		have onclick functionality, (image, circle, polygon and text take
 * 		functionality from this)
 *
 */

package cfss.org.xmlstructure;

/**
 * Stores information pertaining to an entity displayed to a slide and may
 * have onclick functionality, (image, circle, polygon and text take
 * functionality from this)
 * @author Mark Mellar
 *
 */
public abstract class ClickableEntity extends SlideEntity{

	/**
	 * Determines action to be taken when the entity is clicked on
	 * -1	no on click function.
	 * 0	jumps to a specified URL.
	 * >0	specifies the slide id to jump to.
	 * Default is -1.
	 */
	private int onClick = -1;

	/**
	 * URL to go to if onClick = 0.
	 */
	private String onClickUrl = null;

	/**
	 * Constructor.
	 */
	public ClickableEntity(){
		super();
	}

	/**
	 * Copy constructor.
	 *
	 * @param def Entity to copy
	 */
	public ClickableEntity(ClickableEntity def){
		super(def);
		// Primitive
		this.onClick = def.getOnClick();
		// Immutable
		this.onClickUrl = def.getOnClickUrl();
	}

	/**
	 * Returns  onClick.
	 *
	 * @return  -1	no on click function. 0	jumps to a specified URL. >0 specifies the slide id to jump to.
	 */
	public int getOnClick() {
		return onClick;
	}

	/**
	 * Returns onClickURL.
	 *
	 * @return the url to open if onclick url is 0
	 */
	public String getOnClickUrl() {
		return onClickUrl;
	}


	/**
	 * Set onClick to new variable.
	 *
	 * @param newOnClick -1 no on click function. 0 jumps to a specified URL.
	 * >0 specifies the slide id to jump to.
	 */
	public void setOnClick(int newOnClick) {
		this.onClick = newOnClick;
	}

	/**
	 * Set onClickURL to onClickURL.
	 *
	 * @param onClickURL the url to open if onclick url is 0
	 */
	public void setOnClickUrl(String onClickURL) {
		this.onClickUrl = onClickURL;
	}
}