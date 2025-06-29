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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameViewer {
	
	public static final int DELAY=16; // ~60 FPS for smooth gameplay
	
	public void viewerMain() {
		final String frameTitle = "CSSE220 Final Project - Joust";
		final int frameWidth = 800;
		final int frameHeight = 850;
		final int frameXLoc = 100;
		final int frameYLoc = 0;
		
		JFrame frame = new JFrame();
		frame.setTitle(frameTitle);
		frame.setSize(frameWidth, frameHeight);
		frame.setLocation(frameXLoc, frameYLoc);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		GameComponent gameComponent = new GameComponent();
		gameComponent.loadLevel(0);
		frame.addKeyListener(gameComponent);
		
		GameAdvanceListener advanceListener = new GameAdvanceListener(gameComponent);
		Timer timer = new Timer(DELAY, advanceListener);
		timer.start();
	
		frame.add(gameComponent);
		frame.setVisible(true);
	}
	
}
