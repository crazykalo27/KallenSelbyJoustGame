package mainApp;
/**
 * Class: GameAdvanceListener
 * @author Team 303
 * <br>Purpose: advances the game one tick and updates object and redraws 
 * <br>Restrictions: needs to pass in a gameComponent which it can update
 * <br>For example: 
 * <pre>
 *    GameAdvanceListener g = new GameAdvanceListener(gameComponent);
 * </pre>
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import mainApp.GameComponent;

/**
 * Class: GameAdvanceListener
 * @author Team 303
 * <br>Purpose: Tells GameComponent to update and draw objects.
 * <br>Usage: 
 * <pre>
 *    GameAdvanceListener advanceListener = new GameAdvanceListener(gameComponent);
	  Timer timer = new Timer(DELAY, advanceListener);
 * </pre>
 */
public class GameAdvanceListener implements ActionListener {

	private GameComponent gameComponent;

	public GameAdvanceListener(GameComponent gameComponent) {
		this.gameComponent = gameComponent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		advanceOneTick();
	}

	public void advanceOneTick() {
		this.gameComponent.updateObjects();
		this.gameComponent.drawScreen();
	}
}
