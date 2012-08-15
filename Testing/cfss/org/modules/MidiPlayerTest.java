package cfss.org.modules;

/**
  *		Crab Factory Software Solutions
  *
  *  	Software: 		SafetyNet
  *    	Module: 		MidiPlayerTest
  *
  *   	Author:			Michael Spelling
  *
  *   	Contributers:
  *   	Testers:		Michael Spelling
  *
  *   	Description:	This module tests the MidiPlayer class.
  *
  */

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MidiPlayerTest extends TestCase {
	
	MidiPlayer midiDisplay;
	MidiPlayer midiUnDisplay;
	MidiPlayer midiFile;
	MidiPlayer midiVolume;
	MidiPlayer midiLoop;
	
	public MidiPlayerTest(String name) {
		super(name);
	}
	
	// Instantiate classes to use for testing
	protected void setUp() throws Exception{
		super.setUp();
		midiDisplay = new MidiPlayer();
		midiUnDisplay = new MidiPlayer();
		midiFile = new MidiPlayer();
		midiVolume = new MidiPlayer();
		midiLoop = new MidiPlayer();	
	}
	
	// Free up memory after the test
	protected void tearDown() throws Exception {
		super.setUp();
		midiDisplay = null;
		midiUnDisplay = null;
		midiFile = null;
		midiVolume = null;
		midiLoop = null;
	}

	// This test can be found in the SafetyNet class by setting the variable
	// TEST_MIDI to true
	public void testDisplay() {
		assertTrue("Do this test manually" == "Do this test manually");
	}

	// This test can be found in the SafetyNet class by setting the variable
	// TEST_MIDI to true
	public void testUnDisplay() {
		assertTrue("Do this test manually" == "Do this test manually");
	}

	// The following test cases check the setters and getters for the MidiPlayer
	// class. Further tests are done in SafetyNet by setting TEST_MIDI to true
	
	public void testFile_path() {
		// Test instantiation
		assertTrue(midiFile.getFile_path() == null);
		
		// Test that can set and get a valid file name
		midiFile.setFile_path("C:\\Random Folder\\The File.mid");
		assertTrue(midiFile.getFile_path() == "C:\\Random Folder\\The File.mid");
		
		// Test setting various invalid data
		midiFile.setFile_path("11.2");
		assertTrue(midiFile.getFile_path() == "11.2");		
		midiFile.setFile_path("");
		assertTrue(midiFile.getFile_path() == "");
		midiFile.setFile_path(
				"1234asdf1234asdf1234asdf1234asdf1234asdf1234asdf" +
				"1234asdf1234asdf1234asdf1234asdf1234asdf1234asdf1234asdf1234asdf" +
				"1234asdf1234asdf1234asdf1234asdf1234asdf1234asdf1234asdf1234asdf");
		assertTrue(midiFile.getFile_path() == 
				"1234asdf1234asdf1234asdf1234asdf1234asdf1234asdf" +
				"1234asdf1234asdf1234asdf1234asdf1234asdf1234asdf1234asdf1234asdf" +
				"1234asdf1234asdf1234asdf1234asdf1234asdf1234asdf1234asdf1234asdf");
	}

	public void testLoop() {
		// Test instantiation
		assertTrue(midiLoop.getLoop() == false);
		
		// Test all possible setters
		midiLoop.setLoop(true);
		assertTrue(midiLoop.getLoop() == true);
		midiLoop.setLoop(false);
		assertTrue(midiLoop.getLoop() == false);
	}

	public void testVolume() {
		// Test the instantiation
		assertTrue(midiVolume.getVolume() == 127);
		
		// Test that can set and get valid volume values
		midiVolume.setVolume(0);
		assertTrue(midiVolume.getVolume() == 0);
		midiVolume.setVolume(50);
		assertTrue(midiVolume.getVolume() == 50);
		midiVolume.setVolume(127);
		assertTrue(midiVolume.getVolume() == 127);
		
		// Test setting various invalid data
		midiVolume.setVolume(-1);
		assertTrue(midiVolume.getVolume() == 0);
		midiVolume.setVolume(9999999);
		assertTrue(midiVolume.getVolume() == 127);
		midiVolume.setVolume(-7777);
		assertTrue(midiVolume.getVolume() == 0);
		midiVolume.setVolume((int)11.11);
		assertTrue(midiVolume.getVolume() == 11);
		midiVolume.setVolume((int)100.98);
		assertTrue(midiVolume.getVolume() == 100);
	}
	
	// Create test suite
	public static Test MidiPlayerSuite(){
        TestSuite midiPlayerSuite = new TestSuite();
        midiPlayerSuite.addTest(new MidiPlayerTest("midiPlayerTest"));
        return midiPlayerSuite;
	}
}