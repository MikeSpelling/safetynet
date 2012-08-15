package cfss.org.flags;

/**
 * Flags to control which error messages are sent to the console.
 *
 * @author Michael Tock
 */

public final class Debug{

	public static final boolean gui = false;
	public static final boolean localMouse = false;
	public static final boolean audio = false;
	public static final boolean circle = false;
	public static final boolean imagePlayer = false;
	public static final boolean midi = false;
	public static final boolean poly = false;
	public static final boolean scrollPane = false;
	public static final boolean text = false;
	public static final boolean video = false;
	public static final boolean engine = false;
	public static final boolean safetyNet = false;
	public static final boolean renderer = false;
	public static final boolean coverflow = false;
	public static final boolean menu = false;
	public static final boolean sectionNav = false;
	public static final boolean shimmy = false;
	public static final boolean sound = false;
	public static final boolean transport = false;
	public static final boolean localXML = false;
	public static final boolean remoteXML = false;
	public static final boolean config = false;
	public static final boolean parser = false;

/*	*** UNCOMMENT this block for 'ALL ON' testing

	public static final boolean gui = true;
	public static final boolean localMouse = true;
	public static final boolean audio = true;
	public static final boolean circle = true;
	public static final boolean imagePlayer = true;
	public static final boolean midi = true;
	public static final boolean poly = true;
	public static final boolean scrollPane = true;
	public static final boolean text = true;
	public static final boolean video = true;
	public static final boolean engine = true;
	public static final boolean safetyNet = true;
	public static final boolean renderer = true;
	public static final boolean coverflow = true;
	public static final boolean menu = true;
	public static final boolean sectionNav = true;
	public static final boolean shimmy = true;
	public static final boolean sound = true;
	public static final boolean transport = true;
	public static final boolean localXML = true;
	public static final boolean remoteXML = true;
*/

}



/*

Add: import Flags.Debug;
to each class you want to have a flag in...
Do NOT use the suggested java.com.XXX class as this is not our debug one!

if (Debug.XXX)
	System.out.println(...message...);

*/