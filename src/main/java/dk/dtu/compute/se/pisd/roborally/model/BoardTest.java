package dk.dtu.compute.se.pisd.roborally.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class BoardTest {

    @Test
    public void testGetStatusMessage() {
        // Create a board
        Board board = new Board(5, 5);

        // Create players and add to board
        Player player1 = new Player(board, "Red", "Player 1");
        Player player2 = new Player(board, "Blue", "Player 2");
        board.addPlayer(player1);
        board.addPlayer(player2);

        // Set player1 as current player
        board.setCurrentPlayer(player1);

        // Move the current player to a space. We assume that (1, 1) is a valid and empty space
        Space targetSpace = board.getSpace(1, 1);
        player1.setSpace(targetSpace);

        // Insert value into the move counter
        board.setMoveCounter(3);

        // Create string to store expected status message output
        String expectedMessage = "Phase: INITIALISATION, Player: Player 1, Move: 3";

        // Create string to store the actual status message output
        String actualMessage = board.getStatusMessage(player1);

        // Check if expected- and actual status message output matches
        assertEquals(expectedMessage, actualMessage);
    }
}
