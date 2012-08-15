/************************************************
 *	Crab Factory Software Solutions
 *
 *  Software:		SafetyNet
 *  Module: 		MenuList
 *
 *  Author: 		Mark Wrightson
 *
 *  Contributers:	Callum Goddard, Michael Spelling
 *  Testers:		.
 *  Integrators:	.
 *
 *  Description:	This creates the menu list that is placed in
 *  				the MenuBar containting the menus: File, View and About and
 *  				all the options contained within the menu.
 *
 ***********************************************/

package cfss.org.safetynet.gui;

import java.awt.Event;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import cfss.org.flags.Debug;
import cfss.org.safetynet.*;
/**
 * 	This creates the menu list that is placed in
 *  the MenuBar containting the menus: File, View and About and
 *  all the options contained within the menu.
 *
 * @author Mark Wrightson - framework GUI elements
 * @author Callum Goddard - Action listeners/ test cases
 *
 */
public class MenuList {

	private boolean lock = false;
	private JMenuItem miFullscreen;
	private JMenuItem miLockMenu;
	public JMenuBar menuBar;
	private JMenu menuFile,menuView,menuHelp;
	private JMenuItem menuItem;

	/**
	 * Emtpy constructor.
	 */
	public MenuList(){
	}

	/**
	 * Create menuBar with File, View and Help menus, Each is populated
	 * with menu Items as Specified in GUI.
	 */
	protected JMenuBar create(){


		// set up the menu bar
		menuBar = new JMenuBar();
		menuBar.setOpaque(false);
		menuBar.setBorderPainted(false);

		createFile();		//create file menu
		createView();		//create view menu
		createHelp();		//create help menu
		return menuBar;
	}

	/**
	 * This cycles through all Items in the MenuList.
	 *
	 * It sets each MenuItems enable to state
	 *
	 * @param state
	 * 				is the state all the menuItems enable
	 * 				varible is needed to be set to.
	 */
	public void setEnabled(boolean state){
		MenuElement[] list = menuBar.getSubElements();

		for(int i=0; i<list.length;i++){
			JMenu j = (JMenu) list[i];
			j.setEnabled(state);
		}

	}

	/**
	 * This method checks through the menu items to find if any are open
	 * If one is open then the true is returned, else false is returned.
	 *
	 * This method will stop the menuBar closing if a menuItem is currently open
	 *
	 * @return boolean
	 */
	public boolean isMenuItemOpen(){
		//add a means of detecting whether a menu item is currently open
		//if this is true, the persist method in MenuBar will stop the menubar
		//from hiding.
		MenuElement[] list = menuBar.getSubElements();

		for(int i=0; i<list.length;i++){
			JMenu j = (JMenu) list[i];
			if(j.isSelected())return true;

		}

		return false;
	}

	/**
	 * This checks to see what value lock has.
	 * @return lock
	 *
	 */
	public boolean isLock() {
		return lock;
	}

	/**
	 * This sets the boolean lock to the passed in parameter lock
	 *
	 * @param lock is the boolean you want Lock to be set to
	 */
	public void setLock(boolean lock) {
		this.lock = lock;
	}

	/**
	 * This sets the fullscreen boolean
	 *
	 * @param fullscreen
	 */
	public void setFullscreen(boolean fullscreen){
		miFullscreen.setSelected(fullscreen);

	}

	// This makes the File menu and populates it with all the various menu Items that
	// where specified in the cfss Gui documentation
	// It also adds the required functionality to each item.
	private void createFile(){
		menuFile = new JMenu("File");
		menuFile.setMnemonic('I');
		menuBar.add(menuFile);

		//this is is really important.  it fixes a bug.
		//when in full screen mode on 2nd monitor the JMenu appears on the wrong
		//monitor if the x location isn't specified
		menuFile.setMenuLocation(0, 50);

		// Open File menu item - will allow user to open presentation
		menuItem = new JMenuItem("Open File");
		menuItem.setMnemonic('O');
		menuItem.setAccelerator(KeyStroke.getKeyStroke('O', Event.CTRL_MASK,
				false));

		Action openFile = new AbstractAction(){
			/**
			 *
			 */
			private static final long serialVersionUID = -773596484807490150L;

			public void actionPerformed(ActionEvent e) {

				//Test Case to confirm Open File Click
				if(Debug.menu)System.out.println("Open File");

				SafetyNet.findPresentation(false);
			}
		};
		menuItem.addActionListener(openFile);
		menuFile.add(menuItem);

		// Opens URL menu Item - allows user to open a Remote Presentation
		menuItem = new JMenuItem("Open Location");
		menuItem.setMnemonic('U');
		menuItem.setAccelerator(KeyStroke.getKeyStroke('U', Event.CTRL_MASK,
				false));

		Action openLocation = new AbstractAction(){
;
			/**
			 *
			 */
			private static final long serialVersionUID = -4415857728921158044L;

			public void actionPerformed(ActionEvent e) {

				//Test Case to confirm Open Location Click
				if(Debug.menu)System.out.println("Open Location");

				SafetyNet.findPresentation(enabled);
			}
		};
		menuItem.addActionListener(openLocation);
		menuFile.add(menuItem);

		// Open Previous slide Show - allows user to resume previous presentation
		menuItem = new JMenuItem("Open Previous");
		menuItem.setMnemonic('P');
		menuItem.setAccelerator(KeyStroke.getKeyStroke('P', Event.CTRL_MASK,
				false));

		Action openPrevious = new AbstractAction(){
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				//Test Case to confirm Open Previous Click
				if(Debug.menu)System.out.println("Open Previous");

				SafetyNet.resumePrevious();
			}
		};
		menuItem.addActionListener(openPrevious);
		menuFile.add(menuItem);

		menuFile.add(new JSeparator());

		// Menu Item - resets the quiz data in current presentation
		// QuizHandler Class doesn't exist in this package yet
		// When it does the action performed can be uncommented out.
		menuItem = new JMenuItem("Reset Quiz");
		menuItem.setMnemonic('R');
		menuItem.setAccelerator(KeyStroke.getKeyStroke('R', Event.CTRL_MASK,
				false));

		Action resetQuiz = new AbstractAction(){

		private static final long serialVersionUID = 1L;

		/**
		 * Method called when action is performed
		 *
		 * @param e is the ActionEvent.
		 */
		public void actionPerformed(ActionEvent e) {
			//Test Case to confirm Reset Quiz Click
			if(Debug.menu)	System.out.println("Reset Quiz");

			// reset quiz data
			QuizHandler.resetQuizData();

			// clear slide screen now
			SlideRenderer.hideAll();
			SlideRenderer.clearEntities();

			// Update the Gui components
			Gui.getSectionNav().updateTree();

			// Re render the slide
			Engine.playSlideshow();
		}};
		menuItem.addActionListener(resetQuiz);
		menuFile.add(menuItem);

		// Product Store Menu Item - will open the product store
		// calling browerloaded

		menuItem = new JMenuItem("Product Store");
		menuItem.setMnemonic('S');

		menuItem.setAccelerator(KeyStroke.getKeyStroke('S', Event.CTRL_MASK,
		false));

		Action openProductStore = new AbstractAction(){

		private static final long serialVersionUID = 1L;

		/**
		 * Method called when action performed.
		 *
		 * @param e is the ActionEvent.
		 */
		public void actionPerformed(ActionEvent e) {

			//Test Case to confirm product Store Click
			if(Debug.menu)System.out.println("Product Store");
			// TODO update the Product store url
			BrowserLoader.openURL("http://www.voltnet.co.uk/tour/store");

		}};
		menuItem.addActionListener(openProductStore);
		menuFile.add(menuItem);

		menuFile.add(new JSeparator());

		/** program exit */
		Action programExitAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				if(null != SafetyNet.getFileLocation())
					Config.saveData();
				System.exit(0);
			}
		};
		menuItem = new JMenuItem("Exit");
		menuItem.setMnemonic('x');
		menuItem.addActionListener(programExitAction);
		menuItem.setAccelerator(KeyStroke.getKeyStroke('X', Event.CTRL_MASK,
				false));
		menuFile.add(menuItem);
	}

	// This creates the view menu and adds fullscreen check box, and lock
	// checkbox menu item
	// It also adds functionality to each menu Item.
	private void createView(){

		Action menuViewAction = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				System.out.println("view clicked");
			}
		};
		// ---- set up the choices on the View menu ----
		menuView = new JMenu("View");
		menuView.setMnemonic('V');
		menuView.addActionListener(menuViewAction);
		menuBar.add(menuView);

		//this is is really important.  it fixes a bug.
		//when in full screen mode on 2nd monitor the JMenu appears on the wrong
		//monitor if the x location isn't specified
		menuView.setMenuLocation(0, 50);

		miFullscreen = new JCheckBoxMenuItem("Fullscreen");
		miFullscreen.setMnemonic('F');

		miFullscreen.setAccelerator(KeyStroke.getKeyStroke('F', Event.CTRL_MASK,
		 false));

		// -------- program fullscreen
		Action fullScreen = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				//Test Case to confirm Fullscreen Click
				if(Debug.menu)System.out.println("Fullscreen");

				if(Gui.isFullScreenOn()){
					Gui.setWindowedMode();
				}
				else if(!Gui.isFullScreenOn()){
					Gui.setFullScreenMode();
				}
				else{
					System.out.println("MenuList->FullScreenAction ->" +
							"something has gone very wrong!");
				}
			}
		};
		miFullscreen.addActionListener(fullScreen);
		menuView.add(miFullscreen);

		miLockMenu = new JCheckBoxMenuItem("Lock Menu");
		miLockMenu.setMnemonic('L');

		miLockMenu.setAccelerator(KeyStroke.getKeyStroke('L', Event.CTRL_MASK,
		false));

		// This sets a tick box to stop the menu bar from vanishing
		Action lockMenuBar = new AbstractAction(){
			private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					//Test Case to confirm Lock Menu Click
					if(Debug.menu)System.out.println("Lock Menu");

					if(isLock() == false)
						setLock(true);
					else if(isLock() == true)
						setLock(false);
			}
		};
		miLockMenu.addActionListener(lockMenuBar);
		menuView.add(miLockMenu);
	}

	// This creates the help menu and adds all the menu items that are required
	// but the Design GUI documentation.  It also adds functionality to each menu item.
	private void createHelp(){
		// ---- set up the choices on the help menu ----
		menuHelp = new JMenu("Help");
		menuHelp.setMnemonic('H');
		menuBar.add(menuHelp);

		//this is is really important.  it fixes a bug.
		//when in full screen mode on 2nd monitor the JMenu appears on the wrong
		//monitor if the x location isn't specified
		menuHelp.setMenuLocation(0, 50);

		// Will open the Browser and goto the online help page
		menuItem = new JMenuItem("Help");
		menuItem.setMnemonic('H');

		 menuItem.setAccelerator(KeyStroke.getKeyStroke('H', Event.CTRL_MASK,
		 false));

		Action help = new AbstractAction(){
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				//Test Case to confirm Help Click
				if(Debug.menu)System.out.println("Help");

				// TODO complete online help URL
				BrowserLoader.openURL("http://www.voltnet.co.uk/tour/help");

			}
		};
		menuItem.addActionListener(help);
		menuHelp.add(menuItem);

		// Opens the online demo url when selected - in the user browser
		menuItem = new JMenuItem("Online Demo");
		menuItem.setMnemonic('O');

		menuItem.setAccelerator(KeyStroke.getKeyStroke('D', Event.CTRL_MASK,
		 false));

		Action onlineDemo = new AbstractAction(){
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				//Test Case to confirm Online Demo Click
				if(Debug.menu)System.out.println("Online Demo");

				// TODO update the url for the demo URL
				BrowserLoader.openURL("http://www.voltnet.co.uk/tour/productdemo");
			}

		};
		menuItem.addActionListener(onlineDemo);
		menuHelp.add(menuItem);

		menuItem = new JMenuItem("Online Documentation");
		menuItem.setMnemonic('M');

		menuItem.setAccelerator(KeyStroke.getKeyStroke('M', Event.CTRL_MASK,
		 false));

		Action onlineDocumentation= new AbstractAction(){

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				//Test Case to confirm	Online Documentation Click
				if(Debug.menu)System.out.println("Online Documentation");

				// TODO update the url for the demo URL
				BrowserLoader.openURL("http://www.voltnet.co.uk/tour/documentation");
			}

		};
		menuItem.addActionListener(onlineDocumentation);
		menuHelp.add(menuItem);

		// Update content menuItem - currently displays a diagloge box
		// which states this feature is not yet implemented.
		menuItem = new JMenuItem("Update Content");
		menuItem.setMnemonic('U');

		menuItem.setAccelerator(KeyStroke.getKeyStroke('U', Event.CTRL_MASK,
		  false));

		Action updateContent = new AbstractAction(){

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				//Test Case to confirm Open Previous Click
				if(Debug.menu)System.out.println("Update Content");

				JOptionPane.showMessageDialog(Gui.getContentPane(), "Not Yet Implemented", "Message", 0);
			}

		};
		menuItem.addActionListener(updateContent);
		menuHelp.add(menuItem);

		// Adds About menuItem - this displays the about dialogue box
		menuItem = new JMenuItem("About");//TODO add programName
		menuItem.setMnemonic('A');

		menuItem.setAccelerator(KeyStroke.getKeyStroke('A', Event.CTRL_MASK,
				  false));
		Action about = new AbstractAction(){

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				//Test Case to confirm Open Previous Click
				if(Debug.menu)System.out.println("About");

				JOptionPane.showMessageDialog(Gui.getContentPane(),
						"SafetyNet v1.0\n\nTeam Members:\n" +
						"Callum Goddard\nDavid Walker\nHarry Taylor\nMark Mellar\n" +
						"Mark Wrightson\nMike Angus\nMike Spelling\nMike Tock\nPhil Day" +
						"\n\nCopyright University of York",
						"Message", JOptionPane.INFORMATION_MESSAGE);
			}

		};
		menuItem.addActionListener(about);
		menuHelp.add(menuItem);
	}
}