package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameControllerTest {

    @Test
    public void testMoveCurrentPlayerToSpace() {
        // Create a board
        Board board = new Board(5, 5);

        // Create two players and add them to the board
        Player player1 = new Player(board, "Red", "Player 1");
        Player player2 = new Player(board, "Blue", "Player 2");
        board.addPlayer(player1);
        board.addPlayer(player2);

        // Set Player 1 as the current player
        board.setCurrentPlayer(player1);

        // Call getter-method for move counter
        int initialMoveCounter = board.getMoveCounter();

        // Perform move
        Space targetSpace = board.getSpace(1, 1); // Here we assume that (1, 1) is a valid and empty space
        GameController gameController = new GameController(board);
        gameController.moveCurrentPlayerToSpace(targetSpace);

        // Check if the player has moved to the given target space
        assertEquals(targetSpace, player1.getSpace());

        // Check that the current player changes to player 2 after move
        assertEquals(player2, board.getCurrentPlayer());

        // Make sure the move counter has been incremented by 1
        assertEquals(initialMoveCounter + 1, board.getMoveCounter());
    }

}
