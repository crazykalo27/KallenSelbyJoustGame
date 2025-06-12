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
	
	public static final int DELAY=10;  // ~100 FPS - MAXIMUM aggressive performance for web
	
	public void viewerMain() {
		final String frameTitle = "CSSE220 Final Project - Joust";
		final int frameWidth = 800;
		final int frameHeight = 850;
		final int frameXLoc = 100;
		final int frameYLoc = 0;
		
		JFrame frame = new JFrame();
		frame.setTitle(frameTitle);
		
		// Create game component first to get preferred size
		GameComponent gameComponent = new GameComponent();
		gameComponent.loadLevel(0);
		
		// Set game component to exact desired size
		gameComponent.setPreferredSize(new java.awt.Dimension(frameWidth, frameHeight));
		gameComponent.setSize(frameWidth, frameHeight);
		
		// Configure frame for web performance
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setUndecorated(true); // Remove decorations to fix sizing
		
		frame.add(gameComponent);
		frame.pack(); // This will size frame to content + decorations
		frame.setLocationRelativeTo(null); // Center on screen
		
		frame.addKeyListener(gameComponent);
		
		// Optimized timer with higher priority
		GameAdvanceListener advanceListener = new GameAdvanceListener(gameComponent);
		Timer timer = new Timer(DELAY, advanceListener);
		timer.setCoalesce(true); // Combine multiple events
		timer.start();
	
		frame.setVisible(true);
		
		// Request focus for better input responsiveness
		gameComponent.requestFocusInWindow();
	}
	
}
