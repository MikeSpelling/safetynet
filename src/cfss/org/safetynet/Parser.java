/**
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		Parser
 *
 *  Author: 		Mark Mellar
 *
 *  Contributers:	.
 *  Testers:		.
 *
 *  Description:	Validates a passed xml file against g1_schema and
 *  if valid parses the schema to produce objects containing defaults,
 *  then parsed the passed xml file to find any default values which must
 *  be overwritten in these objects, then parses the xml file to produce
 *  a slideshow, all slideshow objects are constructed as copies of the
 *  relevant default object, attributes are then overwritten meaning attributes
 *  that aren't set remain at the default value.
 *
 */

package cfss.org.safetynet;

import cfss.org.safetynet.retrieve.*;
import cfss.org.xmlstructure.*;
import cfss.org.flags.Debug;

import java.awt.Color;
import java.io.File;
import java.net.MalformedURLException;
import java.util.*;

import org.w3c.dom.Document;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

/**
 * 	Validates a passed xml file against g1_schema and
 *  if valid parses the schema to produce objects containing defaults,
 *  then parsed the passed xml file to find any default values which must
 *  be overwritten in these objects, then parses the xml file to produce
 *  a slideshow, all slideshow objects are constructed as copies of the
 *  relevant default object, attributes are then overwritten meaning attributes
 *  that aren't set remain at the default value.
 *
 * 	@author Mark Mellar
 *
 */
public class Parser extends DefaultHandler {

	/** the path to the schema the xml is to be validated against */
	private String schemaPath;
	private String xmlPath;
	/**
	 * default objects contain element defaults working objects start life as
	 * clones of these then attributes overwritten with attributes in the xml if
	 * an attribute isn't set in the xml it is'nt overwritten and the default
	 * value remains
	 */
	private Slide slideDef;
	private Text textDef;
	private XMLImage imageDef;
	private Midi midiDef;
	private Video videoDef;
	private Audio audioDef;
	private Circle circleDef;
	private XMLPolygon shapeDef;
	private AnswerArea answerAreaDef;
	private ScrollPane scrollpaneDef;
	private QuizSlide quizSlideDef;

	/** working objects, temporary holders for objects being created from xml */
	private SlideShow slideShow;
	private DocumentInfo documentInfo;
	private Slide slide;
	private XMLImage image;
	private XMLPolygon polygon;
	private Circle circle;
	private Text text;
	private Midi midi;
	private Video video;
	private Audio audio;
	private TimerElement timer;
	private QuizSlide quizSlide;
	private AnswerArea answerArea;
	private ScrollPane scrollPane;

	/** stacks, used to determine structure of xml file being read */
	private Stack<String> parseStack;
	private Stack<String> defStack;

	/**
	 * Used to determine what the parser is looking for "getschema" retrive the
	 * xml's schema location from the xml "schemadefs" looking for schema
	 * defaults "xmldefs" looking for xml file defaults "parse" looking for xml
	 * elements with which to make a slideshow from
	 */
	private String mode;

	/**
	 * Constructor, initialises stacks
	 */
	public Parser() {
		parseStack = new Stack<String>();
		defStack = new Stack<String>();
	}

	/**
	 * Returns the slideshow defined in a passed xml document
	 *
	 * @param aPathName String the path to the XML file to be parsed
	 * @return Slideshow the slideshow made form the passed file
	 */
	public SlideShow parse(String aPathName) {
		if (Debug.parser)
			System.out.println("xmlPath"+aPathName);
		xmlPath = aPathName;
		xmlPath = xmlPath.replace('\\', '/');

		// get schema and xml files
		File inputFile = new File(aPathName);
		if (!inputFile.exists()) {
			System.out.println("Parser error: passed file does not exist");
			return null;
		}
		// create a parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser;

		// parse files
		try {
			saxParser = factory.newSAXParser();

			mode = "getschema";
			saxParser.parse(inputFile, this);

			File schema = new File(schemaPath);
			if (Debug.parser)
				System.out.println("schemaPath"+schemaPath);
			// validate passed xml against schema
			if (!isValid(inputFile, schema)) {
				System.out.println("Validation error!");
				return null;
			}

			// get schema defaults
			mode = "schemadefs";
			saxParser.parse(schema, this);
			// overwrite schema defaults when redefined in xml
			mode = "xmldefs";
			saxParser.parse(inputFile, this);
			// get data
			mode = "parse";
			saxParser.parse(inputFile, this);
		} catch (Exception e) {
			// something went wrong
			e.printStackTrace();
			System.out.println("SAX Error! Unable to parse slideshow"
					+ e.getMessage());
			return null;
		}
		// sort slides into ascending order of id, name any slide with no name
		sortSlides();

		return (slideShow);
	}

	/**
	 * sorts slides into ascending order of id, names any slide with no name
	 */
	private void sortSlides() {
		// quick and dirty bubble sort, never going to be many slides so speed
		// not a problem
		for (int range = slideShow.getTree().size() - 1; range >= 0; range--) {
			int pos = 0;
			while (pos < range) {
				TreeEntity slide = slideShow.getTree().get(pos);
				TreeEntity nextSlide = slideShow.getTree().get(pos + 1);
				if (slide.getId() > nextSlide.getId()) {
					slideShow.getTree().set(pos, nextSlide);
					slideShow.getTree().set(pos + 1, slide);
				}
				pos++;
			}
		}
		// name all unnamed slides using their IDs
		for (int i = 0; i < slideShow.getTree().size(); i++) {
			if (null == slideShow.getTree().get(i).getHeading()) {
				slideShow.getTree().get(i).setHeading("Slide "
								+ String.valueOf(slideShow.getTree().get(i)
										.getId()));
			}
		}
	}

	/**
	 * validates passed xml file against passed schema file
	 *
	 * @param File
	 *            xml the xml file to be validated
	 * @param File
	 *            schemaFile the schema file the xml is to be validated against
	 * @return boolean true if valid, false if invalid (or other validation
	 *         error)
	 */
	private boolean isValid(File xml, File schemaFile) {
		try {
			// parse an XML document into a DOM tree
			DocumentBuilderFactory parserFactory = DocumentBuilderFactory
					.newInstance();
			parserFactory.setNamespaceAware(true);
			DocumentBuilder parser = parserFactory.newDocumentBuilder();
			Document document = parser.parse(xml);

			// create a SchemaFactory capable of understanding WXS schemas
			SchemaFactory factory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			// load a WXS schema, represented by a Schema instance
			Source schemaSource = new StreamSource(schemaFile);
			Schema schema = factory.newSchema(schemaSource);

			// create a Validator instance, which can be used to validate an
			// instance document
			Validator validator = schema.newValidator();

			// validate the DOM tree (throws exception if not valid
			validator.validate(new DOMSource(document));
		} catch (Exception e) {
			// invalid xml (probably, other things in the try block could throw
			// exceptions too)
			System.out.println(e.getMessage());
			return false;
		}
		return true;

	}

	/**
	 * called by sax parser when an element has started decides on appropriate
	 * action depending on the current mode of the parser then calls the
	 * appropriate function based on this decision
	 *
	 * @param uri String not used
	 * @param localName String not used
	 * @param qName String the name of the element which started
	 * @param attrs Attrubutes the attribues associated with the element which started
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attrs) throws SAXException {
		// determine mode and call appropriate method
		if (mode == "getschema")
			// find the schema location
			startGetSchemaElement(qName, attrs);
		if (mode == "schemadefs")
			// get schema defaults
			startSchemaElement(qName, attrs);
		else if (mode == "xmldefs")
			// get xml defaults
			startXmlDefault(qName, attrs);
		else if (mode == "parse")
			// get the slideshow
			startSlideShowElement(qName, attrs);
	}

	/**
	 * retreives the location of the schema file from the xml file
	 *
	 * @param String
	 *            name the name of the element which has started
	 * @param Attributes
	 *            attrs the attributes relating to the element which has started
	 */
	private void startGetSchemaElement(String name, Attributes attrs) {
		if (name.equals("slideshow")) {
			for (int i = 0; i < attrs.getLength(); i++) {
				if (attrs.getQName(i).equals("xsi:noNamespaceSchemaLocation")) {
					// get the schema path
					String path = attrs.getValue(i);
					// ensure unix type file path
					path = path.replace('\\', '/');
					if (path.substring(0, 5).equals("http:")) {
						try {
							schemaPath = RemoteFileRetriever.downloadFile(path);
						} catch (MalformedURLException e) {
							System.out
									.println("Could not download remote schema"
											+ e.getMessage());
						}
					} else if (path.charAt(1) == ':')
						// absolute path
						schemaPath = path;
					else if (path.charAt(0) == '/')
						// relative to SafetyNet directory
						schemaPath = path;
					else if (!path.contains("/")) {
						// get path of xml and remove xml file name, append
						// schema path
						int directoryPathEnd = xmlPath.lastIndexOf('/');
						String directoryPath = xmlPath.substring(0,
								directoryPathEnd + 1);
						schemaPath = directoryPath + path;
					}
				}
			}
		}
	}

	/**
	 * called by sax parser when an element has ended
	 *
	 * @param uri String not used
	 * @param localName String not used
	 * @param qName String not used
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// determine mode and call appropriate method
		if (mode == "schemadefs")
			endSchemaElement(qName); // get schema defaults
		else if (mode == "parse")
			endSlideShowElement(); // get the slideshow
	}

	/**
	 * called by sax when char data is encountered passes data to other methods
	 * depending on the parser mode
	 *
	 * @param ch Char an array of characters
	 * @param start int the index in ch[] where the characters of interest begin
	 * @param length int the number of chars in the encounter chardata
	 */
	public void characters(char ch[], int start, int length)
			throws SAXException {
		String chars = new String(ch, start, length);
		// determine mode and call appropriate method
		if (mode == "parse") {
			addSlideShowCharData(chars);
		}
	}

	/**
	 * takes chardata and adds it to the appropriate object depending on what is
	 * on top of the parse stack
	 *
	 * @param String
	 *            chars the characters to add
	 */
	@SuppressWarnings("deprecation")
	private void addSlideShowCharData(String chars) {
		// element the chardata belongs to must have its name on top of the
		// stack
		// decide which object the data belongs to and add it
		if (parseStack.isEmpty() != true) {
			if (parseStack.peek().equals("text"))
				text.setData(chars);
			else if (parseStack.peek().equals("heading"))
				documentInfo.setHeading(chars);
			else if (parseStack.peek().equals("author"))
				documentInfo.setName(chars);
			else if (parseStack.peek().equals("version"))
				documentInfo.setVersion(chars);
			else if (parseStack.peek().equals("comment"))
				documentInfo.setComment(chars);
			else if (parseStack.peek().equals("date")) {
				StringTokenizer tokenedDate = new StringTokenizer(chars, "-");
				int year = Integer.valueOf(tokenedDate.nextToken());
				int month = Integer.valueOf(tokenedDate.nextToken());
				int day = Integer.valueOf(tokenedDate.nextToken());
				Date date = new Date(year, month, day);
				documentInfo.setDate(date);
			}
		}
	}

	/**
	 * when an element ends it should be taken off the stack and added to its
	 * owner an objects owner is always beneath it on the stack
	 */

	private void endSlideShowElement() {
		if (!parseStack.isEmpty()) {
			// find out which object has ended
			String adding = parseStack.pop();
			// depending on the object which object has ended pass the
			// appropriate method
			// the owner object it is to be added to
			if (!parseStack.isEmpty()) {
				// get owner object name
				String to = parseStack.peek();
				// add ended object to owner object
				if (adding.equals("text")) {
					endText(to);
				} else if (adding.equals("image")) {
					endImage(to);
				} else if (adding.equals("circle")) {
					endCircle(to);
				} else if (adding.equals("shape")) {
					endShape(to);
				} else if (adding.equals("midi")) {
					endMidi(to);
				} else if (adding.equals("video")) {
					endVideo(to);
				} else if (adding.equals("audio")) {
					endAudio(to);
				} else if (adding.equals("timer")) {
					endTimer(to);
				} else if (adding.equals("slide")) {
					endSlide(to);
				} else if (adding.equals("documentinfo")) {
					endDocumentInfo(to);
				} else if (adding.equals("quizslide")) {
					endQuizSlide(to);
				} else if (adding.equals("answerarea")) {
					endAnswerArea(to);
				} else if (adding.equals("scrollpane")) {
					endScrollPane(to);
				}
			}
		}
	}

	/**
	 * adds the current scroll pane to the object who's name is passed
	 *
	 * @param String
	 *            to the name of the element which the scrollpane should be
	 *            added to
	 */
	private void endScrollPane(String to) {
		if (to.equals("slide"))
			slide.addEntity(scrollPane);
		else if (to.equals("quizslide"))
			quizSlide.addEntity(scrollPane);
		else if (to.equals("feedback"))
			quizSlide.addFeedback(scrollPane);
	}

	/**
	 * adds the current answer area to the object who's name is passed
	 *
	 * @param String
	 *            to the name of the element which the answerArea should be
	 *            added to
	 */
	private void endAnswerArea(String to) {
		if (to.equals("quizslide"))
			quizSlide.addAnswerArea(answerArea);
	}

	/**
	 * adds the current quiz slide to the object who's name is passed
	 *
	 * @param String
	 *            to the name of the element which the quizSlide should be added
	 *            to
	 */
	private void endQuizSlide(String to) {
		if (to.equals("slideshow"))
			slideShow.addTreeEntity(quizSlide);
	}

	/**
	 * adds the current document info to the object who's name is passed
	 *
	 * @param String
	 *            to the name of the element which the slideShow should be added
	 *            to
	 */
	private void endDocumentInfo(String to) {
		if (to.equals("slideshow"))
			slideShow.setDocInf(documentInfo);
	}

	/**
	 * adds the current slide to the object who's name is passed
	 *
	 * @param String
	 *            to the name of the element which the slide should be added to
	 */
	private void endSlide(String to) {
		if (to.equals("slideshow"))
			slideShow.addTreeEntity(slide);
	}

	/**
	 * adds the current timer to the object who's name is passed
	 *
	 * @param String
	 *            to the name of the element which the timer should be added to
	 */
	private void endTimer(String to) {
		if (to.equals("audio"))
			audio.addTimer(timer);
		else if (to.equals("video"))
			video.addTimer(timer);
	}

	/**
	 * adds an audio to the object who's name is passed
	 *
	 * @param String
	 *            to the name of the element which the audio should be added to
	 */
	private void endAudio(String to) {
		if (to.equals("slide"))
			slide.addEntity(audio);
		else if (to.equals("quizslide"))
			quizSlide.addEntity(audio);
		else if (to.equals("scrollpane"))
			scrollPane.addEntity(audio);
		else if (to.equals("feedback"))
			quizSlide.addFeedback(audio);
	}

	/**
	 * adds the current video to the object who's name is passed
	 *
	 * @param String
	 *            to the name of the element which the video should be added to
	 */
	private void endVideo(String to) {
		if (to.equals("slide"))
			slide.addEntity(video);
		else if (to.equals("quizslide"))
			quizSlide.addEntity(video);
		else if (to.equals("scrollpane"))
			scrollPane.addEntity(video);
		else if (to.equals("feedback"))
			quizSlide.addFeedback(video);
	}

	/**
	 * adds the current midi to the object who's name is passed
	 *
	 * @param String
	 *            to the name of the element which the midi should be added to
	 */
	private void endMidi(String to) {
		if (to.equals("slide"))
			slide.addEntity(midi);
		else if (to.equals("quizslide"))
			quizSlide.addEntity(midi);
		else if (to.equals("scrollpane"))
			scrollPane.addEntity(midi);
		else if (to.equals("feedback"))
			quizSlide.addFeedback(midi);
	}

	/**
	 * adds the current polygon to the object who's name is passed
	 *
	 * @param String
	 *            to the name of the element which the polygon should be added
	 *            to
	 */
	private void endShape(String to) {
		if (to.equals("slide"))
			slide.addEntity(polygon);
		else if (to.equals("quizslide"))
			quizSlide.addEntity(polygon);
		else if (to.equals("scrollpane"))
			scrollPane.addEntity(polygon);
		else if (to.equals("feedback"))
			quizSlide.addFeedback(polygon);
	}

	/**
	 * adds the current circle to the object who's name is passed
	 *
	 * @param String
	 *            to the name of the element which the circle should be added to
	 */
	private void endCircle(String to) {
		if (to.equals("slide"))
			slide.addEntity(circle);
		else if (to.equals("quizslide"))
			quizSlide.addEntity(image);
		else if (to.equals("scrollpane"))
			scrollPane.addEntity(image);
		else if (to.equals("feedback"))
			quizSlide.addFeedback(image);
	}

	/**
	 * adds the current image to the object who's name is passed
	 *
	 * @param String
	 *            to the name of the element which the image should be added to
	 */
	private void endImage(String to) {
		if (to.equals("slide"))
			slide.addEntity(image);
		else if (to.equals("quizslide"))
			quizSlide.addEntity(image);
		else if (to.equals("scrollpane"))
			scrollPane.addEntity(image);
		else if (to.equals("feedback"))
			quizSlide.addFeedback(image);
	}

	/**
	 * adds the current text to the object who's name is passed
	 *
	 * @param String
	 *            to the name of the element which the text should be added to
	 */
	private void endText(String to) {
		if (to.equals("slide"))
			slide.addEntity(text);
		else if (to.equals("quizslide"))
			quizSlide.addEntity(text);
		else if (to.equals("scrollpane"))
			scrollPane.addEntity(text);
		else if (to.equals("feedback"))
			quizSlide.addFeedback(text);
	}

	/**
	 * pops the defaults stack when a schema element has ended
	 *
	 * @param String
	 *            aElementName the name of the element which ended
	 */
	private void endSchemaElement(String aElementName) {
		if (!defStack.isEmpty()) {
			if (aElementName.equals("xs:element"))
				defStack.pop();
		}
	}

	/**
	 * decides what which element is defined in the xml and passes the
	 * appropriate method the attributes with which to create one
	 *
	 * @param String
	 *            name the name of the element which started
	 * @param Attributes
	 *            attrs the attributes relating to the element which started
	 */
	private void startSlideShowElement(String name, Attributes attrs) {
		// put the elements name on the stack
		parseStack.push(name);
		// decide what to create, the create one
		if (name.equals("text")) {
			createText(attrs);
		} else if (name.equals("image")) {
			createImage(attrs);
		} else if (name.equals("midi")) {
			createMidi(attrs);
		} else if (name.equals("video")) {
			createVideo(attrs);
		} else if (name.equals("audio")) {
			createAudio(attrs);
		} else if (name.equals("circle")) {
			createCircle(attrs);
		} else if (name.equals("shape")) {
			createShape(attrs);
		} else if (name.equals("slideshow")) {
			createSlideShow();
		} else if (name.equals("documentinfo")) {
			createDocumentInfo();
		} else if (name.equals("slide")) {
			createSlide(attrs);
		} else if (name.equals("timer")) {
			createTimerElement(attrs);
		} else if (name.equals("quizslide")) {
			createQuizSlide(attrs);
		} else if (name.equals("answerarea")) {
			createAnswerArea(attrs);
		} else if (name.equals("correctanswer")) {
			setCorrectAnswerInfo(attrs);
		} else if (name.equals("scorelocation")) {
			setScoreLocationInfo(attrs);
		} else if (name.equals("scrollpane")) {
			createScrollPane(attrs);
		} else if (name.equals("correctlocation")) {
			setCorrectLocationInfo(attrs);
		} else if (name.equals("section")) {
			setSectionInfo(attrs);
		} else if (name.equals("section_slide")) {
			setSlideInfo(attrs);
		} else if (name.equals("shapepath")) {
			addPolygonVirtex(attrs);
		}
	}

	/**
	 * adds a point to a polygons path
	 *
	 * @param String
	 *            attrs the attributes of the point to add
	 */
	private void addPolygonVirtex(Attributes attrs) {
		XMLPoint tempPoint = new XMLPoint();
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("x")) {
				tempPoint.setX(Integer.valueOf(attrs.getValue(i)));
			} else if (attrs.getQName(i).equals("y")) {
				tempPoint.setY(Integer.valueOf(attrs.getValue(i)));
			}
		}
		polygon.addPoint(tempPoint);
	}

	/**
	 * sets the heading of the slide with the id set in the attributes
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the slide info
	 */
	private void setSlideInfo(Attributes attrs) {
		Vector<TreeEntity> slides = slideShow.getTree();
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < slides.size(); i++) {
			if (slides.get(i).getId() == Integer.valueOf(attrs.getValue(0))) {
				slides.get(i).setHeading(attrs.getValue(1));
			}
		}
	}

	/**
	 * slide with id passed in attributes gets heading set and becomes a section
	 * delimiter
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the section info
	 */
	private void setSectionInfo(Attributes attrs) {
		// slide must already exist as sections lie at end of xml
		Vector<TreeEntity> slides = slideShow.getTree();
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < slides.size(); i++) {
			if (slides.get(i).getId() == Integer.valueOf(attrs.getValue(0))) {
				slides.get(i).setHeading(attrs.getValue(1));
				slides.get(i).setSection(true);
			}
		}
	}

	/**
	 * sets correct location info in current quiz slide
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the correctLocation
	 */
	private void setCorrectLocationInfo(Attributes attrs) {
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("x"))
				quizSlide.getCorrectStart().setX(
						Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("y"))
				quizSlide.getCorrectStart().setY(
						Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("fontname"))
				quizSlide.setCorrectFont(attrs.getValue(i));
			else if (attrs.getQName(i).equals("fontsize"))
				quizSlide.setCorrectSize(Float.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("fontcolor"))
				quizSlide.setCorrectColour(new Color(Integer.valueOf(attrs
						.getValue(i).substring(1), 16)));
			else if (attrs.getQName(i).equals("alpha"))
				quizSlide.setCorrectAlpha(Float.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("backgroundcolor"))
				quizSlide.setCorrectBkColour(new Color(Integer.valueOf(attrs
						.getValue(i).substring(1), 16)));
			else if (attrs.getQName(i).equals("backgroundalpha"))
				quizSlide.setCorrectBkAlpha(Float.valueOf(attrs.getValue(i)));
		}
	}

	/**
	 * creates a new scroll pane with the passed attributes
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the scrollpane
	 */
	private void createScrollPane(Attributes attrs) {
		scrollPane = new ScrollPane(scrollpaneDef);
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("x"))
				scrollPane.getStartPoint().setX(
						Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("y"))
				scrollPane.getStartPoint().setY(
						Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("width"))
				scrollPane.setWidth(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("height"))
				scrollPane.setHeight(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("startTime"))
				scrollPane.setStartTime(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("endTime"))
				scrollPane.setEndTime(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("backgroundcolor"))
				scrollPane.setBkCol(new Color(Integer.valueOf(attrs.getValue(i)
						.substring(1), 16)));
			else if (attrs.getQName(i).equals("backgroundalpha"))
				scrollPane.setBkAlpha(Float.valueOf(attrs.getValue(i)));
		}
	}

	/**
	 * sets the score location info in the current quiz slide
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the scoreLocation
	 */
	private void setScoreLocationInfo(Attributes attrs) {
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("x"))
				quizSlide.getScoreStart().setX(
						Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("y"))
				quizSlide.getScoreStart().setY(
						Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("fontname"))
				quizSlide.setScoreFont(attrs.getValue(i));
			else if (attrs.getQName(i).equals("fontsize"))
				quizSlide.setScoreSize(Float.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("fontcolor"))
				quizSlide.setScoreColour(new Color(Integer.valueOf(attrs
						.getValue(i).substring(1), 16)));
			else if (attrs.getQName(i).equals("alpha"))
				quizSlide.setScoreAlpha(Float.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("backgroundcolor"))
				quizSlide.setScoreBkCol(new Color(Integer.valueOf(attrs
						.getValue(i).substring(1), 16)));
			else if (attrs.getQName(i).equals("backgroundalpha"))
				quizSlide.setScoreBkAlpha(Float.valueOf(attrs.getValue(i)));
		}
	}

	/**
	 * sets the correct answer info in the current quiz slide
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the correctAnswer
	 */
	private void setCorrectAnswerInfo(Attributes attrs) {
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("id"))
				quizSlide.setCorrectAnswer(Integer.valueOf(attrs.getValue(i)));
		}
	}

	/**
	 * creates a new anserArea with the passed attributes
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the answerArea
	 */
	private void createAnswerArea(Attributes attrs) {
		answerArea = new AnswerArea(answerAreaDef);
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("id"))
				answerArea.setId(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("x"))
				answerArea.getStartPoint().setX(
						Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("y"))
				answerArea.getStartPoint().setY(
						Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("width"))
				answerArea.setWidth(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("height"))
				answerArea.setHeight(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("starttime"))
				answerArea.setStartTime(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("endtime"))
				answerArea.setEndTime(Integer.valueOf(attrs.getValue(i)));
		}
	}

	/**
	 * creates a new quiz slide with the passed attributes
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the quizSlide
	 */
	private void createQuizSlide(Attributes attrs) {
		quizSlide = new QuizSlide(quizSlideDef);
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("id"))
				quizSlide.setId(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("backgroundcolor"))
				quizSlide.setBkCol(new Color(Integer.valueOf(attrs.getValue(i)
						.substring(1), 16)));
			else if (attrs.getQName(i).equals("duration"))
				quizSlide.setDuration(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("nextslideid"))
				quizSlide.setNext(Integer.valueOf(attrs.getValue(i)));
		}
	}

	/**
	 * creates a new timer element with the passed attributes
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the timer
	 */
	private void createTimerElement(Attributes attrs) {
		timer = new TimerElement();
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("playtime"))
				timer.setPlayTime(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("pausetime"))
				timer.setPauseTime(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("pauseduration"))
				timer.setPauseDuration(Integer.valueOf(attrs.getValue(i)));
		}
	}

	/**
	 * creates a new slide with the passed attributes
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the slide
	 */
	private void createSlide(Attributes attrs) {
		slide = new Slide(slideDef);
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("id"))
				slide.setId(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("backgroundcolor"))
				slide.setBkCol(new Color(Integer.valueOf(attrs.getValue(i)
						.substring(1), 16)));
			else if (attrs.getQName(i).equals("duration"))
				slide.setDuration(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("nextslideid"))
				slide.setNext(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("previewpath")){
				String path = attrs.getValue(i).replace('\\', '/');
				if (isLocal(path)){
					int directoryPathEnd = xmlPath.lastIndexOf('/');
					String directoryPath = xmlPath.substring(0,
						directoryPathEnd + 1);
					slide.setPrevPath(directoryPath + path);
				}else {
					// if remote download file and insert local path
					try {
						slide.setPrevPath(RemoteFileRetriever.downloadFile(path));
					} catch (MalformedURLException e) {
						System.out.println("Could not download remote preview image"
								+ e.getMessage());
					}
				}
			}
				slide.setPrevPath(attrs.getValue(i));
		}
	}

	/**
	 * creates a new document info
	 */
	private void createDocumentInfo() {
		documentInfo = new DocumentInfo();
	}

	/**
	 * creates a new slideshow
	 */
	private void createSlideShow() {
		slideShow = new SlideShow();
	}

	/**
	 * creates a new shape with the passed attributes
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the shape
	 */
	private void createShape(Attributes attrs) {
		polygon = new XMLPolygon(shapeDef);
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("x"))
				polygon.getStartPoint()
						.setX(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("y"))
				polygon.getStartPoint()
						.setY(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("starttime"))
				polygon.setStartTime(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("endtime"))
				polygon.setEndTime(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("color"))
				polygon.setColor(new Color(Integer.valueOf(attrs.getValue(i).substring(1),
						16)));
			else if (attrs.getQName(i).equals("thickness"))
				polygon.setThickness(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("fill"))
				polygon.setFill(Boolean.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("onclick"))
				polygon.setOnClick(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("onclickurl"))
				polygon.setOnClickUrl(attrs.getValue(i));
			else if (attrs.getQName(i).equals("alpha"))
				polygon.setAlpha(Float.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("color1"))
				polygon.setColor1(new Color(Integer.valueOf(attrs.getValue(i).substring(1), 16)));
			else if (attrs.getQName(i).equals("color2"))
				polygon.setColor2(new Color(Integer.valueOf(attrs.getValue(i).substring(1), 16)));
			else if (attrs.getQName(i).equals("xatcolor1"))
				polygon.setxAtColour1(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("xatcolor2"))
				polygon.setxAtColour2(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("yatcolor1"))
				polygon.setyAtColour1(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("yatcolor2"))
				polygon.setyAtColour2(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("iscyclic"))
				polygon.setCyclic(Boolean.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("filltype"))
				polygon.setFillType(Integer.valueOf(attrs.getValue(i)));
		}
	}

	/**
	 * creates a new circle with the passed attributes
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the circle
	 */
	private void createCircle(Attributes attrs) {
		circle = new Circle(circleDef);
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("x"))
				circle.getStartPoint().setX(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("y"))
				circle.getStartPoint().setY(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("starttime"))
				circle.setStartTime(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("endtime"))
				circle.setEndTime(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("color"))
				circle.setColor(new Color(Integer.valueOf(attrs.getValue(i)
						.substring(1), 16)));
			else if (attrs.getQName(i).equals("thickness"))
				circle.setThickness(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("radius"))
				circle.setRadius(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("fill"))
				circle.setFill(Boolean.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("onclick"))
				circle.setOnClick(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("onclickurl"))
				circle.setOnClickUrl(attrs.getValue(i));
			else if (attrs.getQName(i).equals("alpha"))
				circle.setAlpha(Float.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("color1"))
				circle.setColor1(new Color(Integer.valueOf(attrs.getValue(i).substring(1), 16)));
			else if (attrs.getQName(i).equals("color2"))
				circle.setColor2(new Color(Integer.valueOf(attrs.getValue(i).substring(1), 16)));
			else if (attrs.getQName(i).equals("xatcolor1"))
				circle.setxAtColour1(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("xatcolor2"))
				circle.setxAtColour2(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("yatcolor1"))
				circle.setyAtColour1(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("yatcolor2"))
				circle.setyAtColour2(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("iscyclic"))
				circle.setCyclic(Boolean.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("filltype"))
				circle.setFillType(Integer.valueOf(attrs.getValue(i)));
		}
	}

	/**
	 * creates a new audio with the passed attributes
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the audio
	 */
	private void createAudio(Attributes attrs) {
		audio = new Audio(audioDef);
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("x"))
				audio.getStartPoint().setX(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("y"))
				audio.getStartPoint().setY(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("starttime"))
				audio.setStartTime(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("endtime"))
				audio.setEndTime(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("loop"))
				audio.setLoop(Boolean.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("path")) {
				// ensure unix type file path
				String path = attrs.getValue(i).replace('\\', '/');
				if (isLocal(path)){
					int directoryPathEnd = xmlPath.lastIndexOf('/');
					String directoryPath = xmlPath.substring(0,
						directoryPathEnd + 1);
					audio.setPath(directoryPath + path);
				}else {
					// if remote download file and insert local path
					try {
						audio.setPath(RemoteFileRetriever.downloadFile(path));
					} catch (MalformedURLException e) {
						System.out.println("Could not download remote audio"
								+ e.getMessage());
					}
				}

			} else if (attrs.getQName(i).equals("autoplay"))
				audio.setAutoplay(Boolean.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("controls"))
				audio.setControls(Boolean.valueOf(attrs.getValue(i)));
		}
	}

	/**
	 * creates a new video with the passed attributes
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the video
	 */
	private void createVideo(Attributes attrs) {
		video = new Video(videoDef);
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("x"))
				video.getStartPoint().setX(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("y"))
				video.getStartPoint().setY(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("starttime"))
				video.setStartTime(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("endtime"))
				video.setEndTime(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("loop"))
				video.setLoop(Boolean.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("path")) {
				// ensure unix type file path
				String path = attrs.getValue(i).replace('\\', '/');
				if (isLocal(path)){
					int directoryPathEnd = xmlPath.lastIndexOf('/');
					String directoryPath = xmlPath.substring(0,
						directoryPathEnd + 1);
					video.setPath(directoryPath + path);
				}else {
					// if remote download file and insert local path
					try {
						video.setPath(RemoteFileRetriever.downloadFile(path));
					} catch (MalformedURLException e) {
						System.out.println("Could not download remote video"
								+ e.getMessage());
					}
				}
			} else if (attrs.getQName(i).equals("width"))
				video.setWidth(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("height"))
				video.setHeight(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("autoplay"))
				video.setAutoplay(Boolean.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("controls"))
				video.setControls(Boolean.valueOf(attrs.getValue(i)));
		}
	}

	/**
	 * creates a new midi with the passed attributes
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the midi
	 */
	private void createMidi(Attributes attrs) {
		midi = new Midi(midiDef);
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("x"))
				midi.getStartPoint().setX(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("y"))
				midi.getStartPoint().setY(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("starttime"))
				midi.setStartTime(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("endtime"))
				midi.setEndTime(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("loop"))
				midi.setLoop(Boolean.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("path")) {
				// ensure unix type file path
				String path = attrs.getValue(i).replace('\\', '/');
				if (isLocal(path)){
					int directoryPathEnd = xmlPath.lastIndexOf('/');
					String directoryPath = xmlPath.substring(0,
					directoryPathEnd + 1);
					midi.setPath(directoryPath + path);
				}else {
					// if remote download file and insert local path
					try {
						midi.setPath(RemoteFileRetriever.downloadFile(path));
					} catch (MalformedURLException e) {
						System.out.println("Could not download remote midi"
								+ e.getMessage());
					}
				}
			}
		}
	}

	/**
	 * creates a new image with the passed attributes
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the image
	 */
	private void createImage(Attributes attrs) {
		image = new XMLImage(imageDef);
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("x"))
				image.getStartPoint().setX(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("y"))
				image.getStartPoint().setY(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("starttime"))
				image.setStartTime(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("endtime"))
				image.setEndTime(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("onclick"))
				image.setOnClick(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("onclickurl"))
				image.setOnClickUrl(attrs.getValue(i));
			else if (attrs.getQName(i).equals("path")) {
				// ensure unix type file path
				String path = attrs.getValue(i).replace('\\', '/');
				if (isLocal(path)){
					int directoryPathEnd = xmlPath.lastIndexOf('/');
					String directoryPath = xmlPath.substring(0,
					directoryPathEnd + 1);
					image.setPath(directoryPath + path);
				}else {
					// if remote download file and insert local path
					try {
						image.setPath(RemoteFileRetriever.downloadFile(path));
					} catch (MalformedURLException e) {
						System.out.println("Could not download remote image"
								+ e.getMessage());
					}
				}
			} else if (attrs.getQName(i).equals("width"))
				image.setWidth(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("height"))
				image.setHeight(Integer.valueOf(attrs.getValue(i)));
		}
	}

	/**
	 * creates a new text with the passed attributes
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the text
	 */
	private void createText(Attributes attrs) {
		text = new Text(textDef);
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("fontname"))
				text.setFontName(attrs.getValue(i));// overwrite defaults
			else if (attrs.getQName(i).equals("fontsize"))
				text.setFontSize(Float.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("fontcolor"))
				text.setFontColor(new Color(Integer.valueOf(attrs.getValue(i)
						.substring(1), 16)));
			else if (attrs.getQName(i).equals("alpha"))
				text.setFontAlpha(Float.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("backgroundcolor"))
				text.setBackgroundColor(new Color(Integer.valueOf(attrs
						.getValue(i), 16)));
			else if (attrs.getQName(i).equals("backgroundalpha"))
				text.setBackgroundAlpha(Float.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("x"))
				text.getStartPoint().setX(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("y"))
				text.getStartPoint().setY(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("starttime"))
				text.setStartTime(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("endtime"))
				text.setEndTime(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("onclick"))
				text.setOnClick(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("onclickurl"))
				text.setOnClickUrl(attrs.getValue(i));
		}
	}

	/**
	 * called when an xml default has been parsed decides on the element the
	 * default relates to and passes attributes to the correct function
	 *
	 * @param String
	 *            name the name of the element the default relates to
	 * @param Attributes
	 *            attrs the defaults to be set
	 */
	private void startXmlDefault(String name, Attributes attrs) {
		// decide on which default object is to be modified
		// then call the method which modifies it
		if (name.equals("slidedefault")) {
			overwriteSlideDefault(attrs);
		} else if (name.equals("textdefault")) {
			overwriteTextDefault(attrs);
		} else if (name.equals("circledefault")) {
			overwriteCircleDefault(attrs);
		} else if (name.equals("shapedefault")) {
			overwriteShapeDefault(attrs);
		}
	}

	/**
	 * overwrites shape defaults with passed attributes
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the shapeDefault
	 */
	private void overwriteShapeDefault(Attributes attrs) {
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("color"))
				shapeDef.setColor(new Color(Integer.valueOf(attrs.getValue(i),
						16)));
			else if (attrs.getQName(i).equals("thickness"))
				shapeDef.setThickness(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("fill"))
				shapeDef.setFill(Boolean.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("alpha"))
				shapeDef.setAlpha(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("filltype"))
				shapeDef.setFillType(Integer.valueOf(attrs.getValue(i)));
		}
	}

	/**
	 * overwrites circle defaults with passed attributes
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the circleDefault
	 */
	private void overwriteCircleDefault(Attributes attrs) {
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("color"))
				circleDef.setColor(new Color(Integer.valueOf(attrs.getValue(i),
						16)));
			else if (attrs.getQName(i).equals("thickness"))
				circleDef.setThickness(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("fill"))
				circleDef.setFill(Boolean.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("alpha"))
				circleDef.setAlpha(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("filltype"))
				circleDef.setFillType(Integer.valueOf(attrs.getValue(i)));
		}
	}

	/**
	 * overwrites text defaults with passed attributes
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the textDefault
	 */
	private void overwriteTextDefault(Attributes attrs) {
		// loop through all attributes, setting appropriate values
		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("fontname"))
				textDef.setFontName(attrs.getValue(i));
			else if (attrs.getQName(i).equals("fontsize"))
				textDef.setFontSize(Float.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("fontcolor"))
				textDef.setFontColor((new Color(Integer.valueOf(attrs.getValue(
						i).substring(1), 16))));
			else if (attrs.getQName(i).equals("alpha"))
				textDef.setFontAlpha(Integer.valueOf(attrs.getValue(i)));
			else if (attrs.getQName(i).equals("backgroundcolor"))
				textDef.setBackgroundColor(new Color(Integer.valueOf(attrs
						.getValue(i).substring(1), 16)));
			else if (attrs.getQName(i).equals("backgroundalpha"))
				textDef.setBackgroundAlpha(Integer.valueOf(attrs.getValue(i)));
		}
	}

	/**
	 * overwrites slide defaults with passed attributes
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the slideDefault
	 */
	private void overwriteSlideDefault(Attributes attrs) {
		// loop through all attributes, setting appropriate values

		for (int i = 0; i < attrs.getLength(); i++) {
			if (attrs.getQName(i).equals("backgroundcolor"))
				slideDef.setBkCol(new Color(Integer.valueOf(attrs.getValue(i)
						.substring(1), 16)));
			else if (attrs.getQName(i).equals("previewpath"))
				slideDef.setPrevPath(attrs.getValue(i));
		}
	}

	/**
	 * called when a start tag is found while parsing the schema for defaults
	 *
	 * @param String
	 *            name the name of the element for which default values are
	 *            being set
	 * @param Attributes
	 *            attrs the defaults to be set
	 */
	private void startSchemaElement(String name, Attributes attrs) {
		// if isNewSchemaElement is true tag is new element definition
		boolean isNewSchemaDefault = false;
		// find out if tag is new element definition
		if (name == "xs:element") {
			defStack.push(attrs.getValue(0));
			if (1 == attrs.getLength()) {
				isNewSchemaDefault = true;
			} else if (attrs.getValue(0).equals("slide")
					|| attrs.getValue(0).equals("quizslide")) {
				isNewSchemaDefault = true;
			}
		}
		// TODO:very specific to this schema is there a way to generalise?
		if (isNewSchemaDefault) {
			// if its new create a new default element
			createNewDefault(attrs);
		} else if (name.equals("xs:attribute") && attrs.getLength() == 2) {
			if (attrs.getQName(1).equals("default")) {
				// must assign a default to an element
				setSchemaDefault(attrs);
			}
		}
	}

	/**
	 * finds which element default is to be set by looking at the defaults stack
	 * and calls method to set the appropriate default
	 *
	 * @param Attributes
	 *            attrs the defaults to be passed to the appropriate method
	 */

	private void setSchemaDefault(Attributes attrs) {
		// use stack to determine which object the default belongs to
		if (!defStack.isEmpty()) {
			if (defStack.peek().equals("text")) {
				setTextDefault(attrs);
			} else if (defStack.peek().equals("slide")) {
				setSlideDefault(attrs);
			} else if (defStack.peek().equals("image")) {
				setImageDefault(attrs);
			} else if (defStack.peek().equals("midi")) {
				setMidiDefault(attrs);
			} else if (defStack.peek().equals("video")) {
				setVideoDefault(attrs);
			} else if (defStack.peek().equals("audio")) {
				setAudioDefault(attrs);
			} else if (defStack.peek().equals("shape")) {
				setShapeDefault(attrs);
			} else if (defStack.peek().equals("circle")) {
				setCircleDefault(attrs);
			} else if (defStack.peek().equals("quizslide")) {
				setQuizSlideDefault(attrs);
			} else if (defStack.peek().equals("answerarea")) {
				setAnswerAreaDefault(attrs);
			} else if (defStack.peek().equals("correctlocation")) {
				setCorrectLocationDefault(attrs);
			} else if (defStack.peek().equals("scorelocation")) {
				setScoreLocationDefault(attrs);
			} else if (defStack.peek().equals("scrollpane")) {
				setScrollPaneDefault(attrs);
			}
		}
	}

	/**
	 * sets scroll pane defaults to passed attribute values
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the scrollPaneDefault
	 */
	private void setScrollPaneDefault(Attributes attrs) {
		// find the value to be set, then set it
		if (attrs.getValue(0).equals("starttime")) {
			scrollpaneDef.setStartTime(Integer.valueOf(attrs.getValue(1)));
		} else if (attrs.getValue(0).equals("endtime")) {
			scrollpaneDef.setEndTime(Integer.valueOf(attrs.getValue(1)));
		} else if (attrs.getValue(0).equals("backgroundcolor")) {
			scrollpaneDef.setBkCol(new Color(Integer.valueOf(attrs.getValue(1)
					.substring(1), 16)));
		} else if (attrs.getValue(0).equals("backgroundalpha")) {
			scrollpaneDef.setBkAlpha(Float.valueOf(attrs.getValue(1)));
		}
	}

	/**
	 * sets correct location defaults to passed attribute values
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the
	 *            currentLocationDefault
	 */
	private void setCorrectLocationDefault(Attributes attrs) {
		// find the value to be set, then set it
		if (attrs.getValue(0).equals("fontname")) {
			quizSlideDef.setCorrectFont(attrs.getValue(1));
		} else if (attrs.getValue(0).equals("fontsize")) {
			quizSlideDef.setCorrectSize(Float.valueOf(attrs.getValue(1)));
		} else if (attrs.getValue(0).equals("fontcolor")) {
			quizSlideDef.setCorrectColour(new Color(Integer.valueOf(attrs
					.getValue(1).substring(1), 16)));
		} else if (attrs.getValue(0).equals("alpha")) {
			quizSlideDef.setCorrectAlpha(Float.valueOf(attrs.getValue(1)));
		} else if (attrs.getValue(0).equals("backgroundcolor")) {
			quizSlideDef.setCorrectBkColour(new Color(Integer.valueOf(attrs
					.getValue(1).substring(1), 16)));
		} else if (attrs.getValue(0).equals("backgroundalpha")) {
			quizSlideDef.setCorrectBkAlpha(Float.valueOf(attrs.getValue(1)));
		}
	}

	/**
	 * sets score location defaults to passed attribute values
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the scoreLocation
	 */
	private void setScoreLocationDefault(Attributes attrs) {
		// find the value to be set, then set it
		if (attrs.getValue(0).equals("fontname")) {
			quizSlideDef.setScoreFont(attrs.getValue(1));
		} else if (attrs.getValue(0).equals("fontsize")) {
			quizSlideDef.setScoreSize(Float.valueOf(attrs.getValue(1)));
		} else if (attrs.getValue(0).equals("fontcolor")) {
			quizSlideDef.setScoreColour(new Color(Integer.valueOf(attrs
					.getValue(1).substring(1), 16)));
		} else if (attrs.getValue(0).equals("alpha")) {
			quizSlideDef.setScoreAlpha(Float.valueOf(attrs.getValue(1)));
		} else if (attrs.getValue(0).equals("backgroundcolor")) {
			quizSlideDef.setScoreBkCol(new Color(Integer.valueOf(attrs
					.getValue(1).substring(1), 16)));
		} else if (attrs.getValue(0).equals("backgroundalpha")) {
			quizSlideDef.setScoreBkAlpha(Float.valueOf(attrs.getValue(1)));
		}
	}

	/**
	 * sets answer area defaults to passed attribute values
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the answerAreaDefault
	 */
	private void setAnswerAreaDefault(Attributes attrs) {
		// find the value to be set, then set it
		if (attrs.getValue(0).equals("starttime")) {
			answerAreaDef.setStartTime(Integer.valueOf(attrs.getValue(1)));
		} else if (attrs.getValue(0).equals("endtime")) {
			answerAreaDef.setEndTime(Integer.valueOf(attrs.getValue(1)));
		}
	}

	/**
	 * sets quiz slide defaults to passed attribute values
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the quizSlideDefault
	 */
	private void setQuizSlideDefault(Attributes attrs) {
		// find the value to be set, then set it
		if (attrs.getValue(0).equals("backgroundcolor"))
			quizSlideDef.setBkCol(new Color(Integer.valueOf(attrs.getValue(1)
					.substring(1), 16)));
		else if (attrs.getValue(0).equals("duration"))
			quizSlideDef.setDuration(Integer.valueOf(attrs.getValue(1)));
	}

	/**
	 * sets circle defaults to passed attribute values
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the circleDefault
	 */
	private void setCircleDefault(Attributes attrs) {
		// find the value to be set, then set it
		if (attrs.getValue(0).equals("fill"))
			circleDef.setFill(Boolean.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("color"))
			circleDef.setColor(new Color((Integer.valueOf(attrs.getValue(1)
					.substring(1), 16))));
		else if (attrs.getValue(0).equals("thickness"))
			circleDef.setThickness(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("alpha"))
			circleDef.setAlpha(Float.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("starttime"))
			circleDef.setStartTime(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("endtime"))
			circleDef.setEndTime(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("onclick"))
			circleDef.setOnClick(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("onclickurl"))
			circleDef.setOnClickUrl(attrs.getValue(1));
		else if (attrs.getQName(0).equals("filltype"))
			circleDef.setFillType(Integer.valueOf(attrs.getValue(1)));
	}

	/**
	 * sets shape defaults to passed attribute values
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the shapeDefault
	 */
	private void setShapeDefault(Attributes attrs) {
		// find the value to be set, then set it
		if (attrs.getValue(0).equals("fill"))
			shapeDef.setFill(Boolean.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("color"))
			shapeDef.setColor(new Color((Integer.valueOf(attrs.getValue(1)
					.substring(1), 16))));
		else if (attrs.getValue(0).equals("thickness"))
			shapeDef.setThickness(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("alpha"))
			shapeDef.setAlpha(Float.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("starttime"))
			shapeDef.setStartTime(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("endtime"))
			shapeDef.setEndTime(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("onclick"))
			shapeDef.setOnClick(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("onclickurl"))
			shapeDef.setOnClickUrl(attrs.getValue(1));
		else if (attrs.getQName(0).equals("filltype"))
			shapeDef.setFillType(Integer.valueOf(attrs.getValue(1)));
	}

	/**
	 * sets audio defaults to passed attribute values
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the audioDefault
	 */
	private void setAudioDefault(Attributes attrs) {
		// find the value to be set, then set it
		if (attrs.getValue(0).equals("starttime"))
			audioDef.setStartTime(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("endtime"))
			audioDef.setEndTime(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("loop"))
			audioDef.setLoop(Boolean.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("controls"))
			audioDef.setControls(Boolean.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("autoplay"))
			audioDef.setAutoplay(Boolean.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("x"))
			audioDef.getStartPoint().setX(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("y"))
			audioDef.getStartPoint().setY(Integer.valueOf(attrs.getValue(1)));
	}

	/**
	 * sets video defaults to passed attribute values
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the videoDefault
	 */
	private void setVideoDefault(Attributes attrs) {
		// find the value to be set, then set it
		if (attrs.getValue(0).equals("starttime"))
			videoDef.setStartTime(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("endtime"))
			videoDef.setEndTime(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("loop"))
			videoDef.setLoop(Boolean.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("controls"))
			videoDef.setControls(Boolean.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("autoplay"))
			videoDef.setAutoplay(Boolean.valueOf(attrs.getValue(1)));
	}

	/**
	 * sets midi defaults to passed attribute values
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the midi
	 */
	private void setMidiDefault(Attributes attrs) {
		// find the value to be set, then set it
		if (attrs.getValue(0).equals("starttime"))
			midiDef.setStartTime(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("endtime"))
			midiDef.setEndTime(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("loop"))
			midiDef.setLoop(Boolean.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("controls")) {
			// midi dosent have controls in our implementation
		}
	}

	/**
	 * sets image defaults to passed attribute values
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the imageDefault
	 */
	private void setImageDefault(Attributes attrs) {
		// find the value to be set, then set it
		if (attrs.getValue(0).equals("starttime"))
			imageDef.setStartTime(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("endtime"))
			imageDef.setEndTime(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("onclick")) {
			imageDef.setOnClick(Integer.valueOf(attrs.getValue(1)));
		} else if (attrs.getValue(0).equals("onclickurl"))
			imageDef.setOnClickUrl(attrs.getValue(1));
	}

	/**
	 * sets slide defaults to passed attribute values
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the slideDefault
	 */
	private void setSlideDefault(Attributes attrs) {
		// find the value to be set, then set it
		if (attrs.getValue(0).equals("backgroundcolor")) {
			slideDef.setBkCol(new Color(Integer.valueOf(attrs.getValue(1)
					.substring(1), 16)));
		} else if (attrs.getValue(0).equals("duration")) {
			slideDef.setDuration(Integer.valueOf(attrs.getValue(1)));
		} else if (attrs.getValue(0).equals("previewpath"))
			slideDef.setPrevPath(attrs.getValue(1));
	}

	/**
	 * sets text defaults to passed attribute values
	 *
	 * @param Attributes
	 *            attrs the attributes associated with the textDefault
	 */
	private void setTextDefault(Attributes attrs) {
		// find the value to be set, then set it
		if (attrs.getValue(0).equals("fontname"))
			textDef.setFontName(attrs.getValue(1));
		else if (attrs.getValue(0).equals("fontsize"))
			textDef.setFontSize(Float.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("fontcolor")) {
			textDef.setFontColor(new Color(Integer.valueOf(attrs.getValue(1)
					.substring(1), 16)));
		} else if (attrs.getValue(0).equals("alpha"))
			textDef.setFontAlpha(Float.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("backgroundcolor")) {
			textDef.setBackgroundColor(new Color(Integer.valueOf(attrs
					.getValue(1).substring(1), 16)));
		} else if (attrs.getValue(0).equals("backgroundalpha"))
			textDef.setBackgroundAlpha(Float.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("starttime"))
			textDef.setStartTime(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("endtime"))
			textDef.setEndTime(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("onclick"))
			textDef.setOnClick(Integer.valueOf(attrs.getValue(1)));
		else if (attrs.getValue(0).equals("onclickurl"))
			textDef.setOnClickUrl(attrs.getValue(1));
	}

	/**
	 * creates a new default object depending on the passed attribute
	 *
	 * @param Attributes
	 *            attrs attributes, contains the name of the default object to
	 *            be made at index 0
	 */
	private void createNewDefault(Attributes attrs) {
		// find the default to be created, then create it
		if (attrs.getValue(0).equals("text")) {
			// create new default Text object
			textDef = new Text();
		} else if (attrs.getValue(0).equals("slide")) {
			// create new default slide object
			slideDef = new Slide();
		} else if (attrs.getValue(0).equals("image")) {
			// create new default image object
			imageDef = new XMLImage();
		} else if (attrs.getValue(0).equals("midi")) {
			// create new default midi object
			midiDef = new Midi();
		} else if (attrs.getValue(0).equals("video")) {
			// create new default video object
			videoDef = new Video();
		} else if (attrs.getValue(0).equals("audio")) {
			// create new default audio object
			audioDef = new Audio();
		} else if (attrs.getValue(0).equals("circle")) {
			// create new default circle object
			circleDef = new Circle();
		} else if (attrs.getValue(0).equals("shape")) {
			// create new default shape object
			shapeDef = new XMLPolygon();
		} else if (attrs.getValue(0).equals("quizslide")) {
			// create new default quiz slide object
			quizSlideDef = new QuizSlide();
		} else if (attrs.getValue(0).equals("answerarea")) {
			// create new default answer area object
			answerAreaDef = new AnswerArea();
		} else if (attrs.getValue(0).equals("scrollpane")) {
			// create new default scroll pane object
			scrollpaneDef = new ScrollPane();
		}
	}

	/**
	 * returns true if the file is local, false if remote
	 *
	 * @param String
	 *            path the path to be assessed for locality
	 * @return boolean true if local, false if remote
	 */
	private boolean isLocal(String path) {
		if (path.substring(0, 6).equals("http://"))
			return false;
		return true;
	}

	public static void main(String[] args) {
		Parser p = new Parser();
		p.parse("src/cfss/org/XML/test v1.4.xml");
		p.parse("src/cfss/org/XML/g1_test_v1.4.3.xml");

	}
}