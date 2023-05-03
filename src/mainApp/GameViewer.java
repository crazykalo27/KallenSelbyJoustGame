package mainApp;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

import mainApp.GameAdvanceListener;

public class GameViewer {
	
	public static final int DELAY=50;

	public void viewerMain() {
		final String frameTitle = "CSSE220 Final Project - Joust";
		final int frameWidth = 1000;
		final int frameHeight = 600;
		final int frameXLoc = 100;
		final int frameYLoc = 200;
		
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
