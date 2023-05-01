package mainApp;


/**
 * Class: MainApp
 * @author Put your team name here
 * <br>Purpose: Top level class for CSSE220 Project containing main method 
 * <br>Restrictions: None
 */
public class MainApp {
	
	
	private void runApp(GameViewer gameViewer) {
		gameViewer.viewerMain();	
	} // runApp

	/**
	 * ensures: runs the application
	 * @param args unused
	 */
	public static void main(String[] args) {
		MainApp mainApp = new MainApp();
		GameViewer gameViewer = new GameViewer();
		mainApp.runApp(gameViewer);		
	} // main

}