package core;

import exceptions.SquareNotFoundException;
import models.*;
import service.CollisionService;
import utils.Cleaner;

import java.util.*;

public class MovementHandler implements Handler<Game> {

    private Handler<Game> next;
    private Queue<Movement> queue;
    private final CollisionService collision;

    public MovementHandler(CollisionService collision) {
        this.collision = collision;
        this.queue = new LinkedList<>();
    }

    public MovementHandler(Handler<Game> next, CollisionService collision) {
        this.next = next;
        this.collision = collision;
        this.queue = new LinkedList<>();
    }

    @Override
    public void handle(Game request) {
        try {
            if(!this.queue.isEmpty()) {
                Movement nextMove = this.queue.poll();
                Cleaner.appearInGameTetromino(request, false);
                resolve(request, nextMove);
                Cleaner.appearInGameTetromino(request, true);
            }
        } catch (SquareNotFoundException exception) {
            System.out.println("An error occurs in movement handler! " + exception.getMessage());
        }
        if (!Objects.isNull(next)) {
            next.handle(request);
        }
    }

    private void resolve(Game game, Movement movimiento) throws SquareNotFoundException {
        if(movimiento.equals(Movement.ROTATE) && collision.canRotate(game)) {
            Cleaner.appearInGameTetromino(game, false);
            game.getInGameTetromino().setNextRotateState();
        }

        if(!game.inGameTetrominoIsFloating()) {
            return;
        }

        if(collision.canMove(game, movimiento)) {
            InGameTetromino tetromino = game.getInGameTetromino();
            List<Square> squares = tetromino.getSquareListForm();

            for(Square square : squares) {
                game.getBoard().getSquare(square.getX() + tetromino.getX(), square.getY() + tetromino.getY()).setOccupied(false);
            }

            for(Square square : squares) {
                Integer newPositionX = square.getX() + tetromino.getX() + movimiento.getMovX();
                Integer newPositionY = square.getY() + tetromino.getY() + movimiento.getMovY();
                game.getBoard().getSquare(newPositionX, newPositionY).setOccupied(true);
            }
            tetromino.setPosition(new Position(tetromino.getX() + movimiento.getMovX(), tetromino.getY() + movimiento.getMovY()));
        }
    }

    /**
     * Agregar un movimiento a la cola de movimientos pendientes
     * @param mov - movimiento realizado por el usuario
     */
    public void addToQueue(Movement mov) {
        if(queue == null) {
            Queue<Movement> tmp = new LinkedList<>();
            queue = new LinkedList<>();
            queue.add(mov);
        }
        else {
            if(queue.size() < 2) {
                queue.add(mov);
            }
            if(queue.size() == 2) {
                queue.clear();
                this.queue.add(mov);
            }
        }
    }
}
