/*******************************************************************************
 * 		Crab Factory Software Solutions
 *
 * 		Software: 		SafetyNet
 * 		Module: 		Text
 *
 * 		Author: 		Mark Mellar
 *
 * 		Contributers: 	Mike Spelling, Callum Goddard
 * 		Testers:
 *
 * 		Description: 	Stores information pertaining to some text which is
 *						to be displayed in a slide.
 ******************************************************************************/

package cfss.org.xmlstructure;

import java.awt.*;
/**
 * Stores information pertaining to some text which is
 * to be displayed in a slide.
 * @author Mark Mellar
 */
public final class Text extends ClickableEntity {

	/**
	 * Transparency of background. Default is 1.
	 */
	private float backgroundAlpha = 1f;
	/**
	 * Colour of background. Default is white.
	 */
	private Color backgroundColor = Color.white;
	/**
	 * The text string to be displayed.
	 */
	private String data = "";
	/**
	 * The transparency of the text. Default is 1.
	 */
	private float fontAlpha = 1f;
	/**
	 * The colour of the text to be displayed. Default is black.
	 */
	private Color fontColor = Color.black;
	/**
	 * The name of the font to be used.
	 */
	private String fontName;
	/**
	 * The size of the font to be used. Default is 12.
	 */
	private float fontSize = 12;


	/**
	 *  Constructor calls super.
	 */
	public Text(){
		super();
	}

	/**
	 * Copy constructor.
	 *
	 * @param def The text to be copied.
	 */
	public Text(Text def){
		super(def);
		// Primitives
		this.fontSize = def.getFontSize();
		this.fontAlpha = def.getFontAlpha();
		this.backgroundAlpha = def.getBackgroundAlpha();

		// Immutable objects don't need copy's
		this.backgroundColor = def.getBackgroundColor();
		this.fontColor = def.getFontColor();
		this.data = def.getData();
		this.fontName = def.getFontName();
	}


	/**
	 * Returns backgroundAlpha.
	 *
	 * @return the transparency of the background of the text.
	 */
	public float getBackgroundAlpha() {
		return backgroundAlpha;
	}


	/**
	 * Returns backgroundColor.
	 *
	 * @return the colour of the background of the text.
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}


	/**
	 * Returns data.
	 *
	 * @return The text string to be displayed.
	 */
	public String getData() {
		return data;
	}


	/**
	 * Returns fontAlpha.
	 *
	 * @return The transparency of the text to be displayed.
	 */
	public float getFontAlpha() {
		return fontAlpha;
	}


	/**
	 * Returns font color.
	 *
	 * @return the colour of the text to be displayed.
	 */
	public Color getFontColor() {
		return fontColor;
	}


	/**
	 * Returns fontName
	 *
	 * @return The font of the text to be displayed.
	 */
	public String getFontName() {
		return fontName;
	}


	/**
	 * Returns fontSize.
	 *
	 * @return the size of the text to be displayed.
	 */
	public float getFontSize() {
		return fontSize;
	}


	/**
	 * Sets backgroundAlpha to backgroundAlpha.
	 *
	 * @param inputBackgroundAlpha The transparency of the background of
	 * the text.
	 */
	public void setBackgroundAlpha(float inputBackgroundAlpha) {
		if(inputBackgroundAlpha >= 1.0f)
			backgroundAlpha = 1.0f;
		else if(inputBackgroundAlpha <= 0.0f)
			backgroundAlpha = 0.0f;
		else
			backgroundAlpha = inputBackgroundAlpha;
	}


	/**
	 * Sets backgroundColor to backgroundColor.
	 *
	 * @param inputBackgroundColor The colour of the text to be displayed.
	 */
	public void setBackgroundColor(Color inputBackgroundColor) {
		this.backgroundColor = inputBackgroundColor;
	}

	/**
	 * Sets data to data.
	 *
	 * @param inputData The text string to be displayed.
	 */
	public void setData(String inputData) {
		this.data = inputData;
	}

	/**
	 * Sets fontAlpha to fontAlpha.
	 *
	 * @param inputFontAlpha the transparency of the text to be displayed.
	 */
	public void setFontAlpha(float inputFontAlpha) {
		if(inputFontAlpha >= 1.0f)
			fontAlpha = 1.0f;
		else if(inputFontAlpha <= 0.0f)
			fontAlpha = 0.0f;
		else
			fontAlpha = inputFontAlpha;
	}

	/**
	 * Sets font color to fontColor.
	 *
	 * @param inputFontColor The colour of the text to be displayed.
	 */
	public void setFontColor(Color inputFontColor) {
		this.fontColor = inputFontColor;
	}


	/**
	 * Sets fontName to fontName.
	 *
	 * @param inputFontName The name of the font of the text to be displayed.
	 */
	public void setFontName(String inputFontName) {
		this.fontName = inputFontName;
	}


	/**
	 * Sets fontSize to fontSize.
	 * @param inputFontSize the size of the text to be displayed.
	 */
	public void setFontSize(Float inputFontSize) {
		if(inputFontSize < 8)
			fontSize = 8;
		else if(fontSize > 72)
			fontSize = 72;
		else
			fontSize = inputFontSize;
	}
}