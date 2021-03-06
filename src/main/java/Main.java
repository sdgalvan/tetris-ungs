import controllers.SwingKeyboardController;
import controllers.SwingMainController;
import core.*;
import factory.ClassicTetrominoFactory;
import models.Board;
import models.Game;
import service.DefaultCollisionService;
import service.DefaultScoreService;
import service.KeyboardService;
import service.TimeService;
import views.SwingGameView;

public class Main {
	public static void main(String[] args) {
		GameStateHandler gameHandler = new GameStateHandler();
		CleanerHandler cleanerHandler = new CleanerHandler(gameHandler, new DefaultScoreService());
		GravityHandler gravityHandler = new GravityHandler(cleanerHandler, new DefaultCollisionService(), new TimeService());
		MovementHandler movementHandler = new MovementHandler(gravityHandler, new DefaultCollisionService());
		TetrominoHandler tetrominoHandler = new TetrominoHandler(movementHandler, new ClassicTetrominoFactory());

		KeyboardService keyboard = new KeyboardService(movementHandler);
		SwingKeyboardController keyboardController = new SwingKeyboardController(keyboard);
		SwingGameView gameView  = new SwingGameView();
		SwingMainController viewController = new SwingMainController(keyboardController, gameView);

		gameHandler.addObserver(viewController);
		viewController.start();

		Game game = new Game(new Board());
		while (true) {
			tetrominoHandler.handle(game);
		}
	}
}
