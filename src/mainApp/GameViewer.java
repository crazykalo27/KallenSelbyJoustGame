package mainApp;
/**
 * Class: GameViewer
 * @author Team 303
 * <br>Purpose: Creates the actual frame and its properties to display the game
 * <br>Restrictions: needs to be given proper levels to load
 * <br>For example: 
 * <pre>
 *    GameViewer v = new GameViewer();
 * </pre>
 */

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameViewer {
	
	public static final int DELAY=40;

	public void viewerMain() {
		final String frameTitle = "CSSE220 Final Project - Joust";
		final int frameWidth = 800;
		final int frameHeight = 850;
		final int frameXLoc = 100;
		final int frameYLoc = 100;
		
		JFrame frame = new JFrame();
		frame.setTitle(frameTitle);
		frame.setSize(frameWidth, frameHeight);
		frame.setLocation(frameXLoc, frameYLoc);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GameComponent gameComponent = new GameComponent();
		gameComponent.loadLevel(1);
		frame.addKeyListener(gameComponent);
		
		GameAdvanceListener advanceListener = new GameAdvanceListener(gameComponent);
		Timer timer = new Timer(DELAY, advanceListener);
		timer.start();
	
		frame.add(gameComponent);
		frame.setVisible(true);
	}
	
}
