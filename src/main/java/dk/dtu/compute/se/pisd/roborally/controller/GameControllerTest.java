package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
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

    // Test to check if the phase-transition buttons are activated correctly
    @Test
    public void testActivateExecutionButtons() {
        Board board = new Board(10, 10);
        GameController gameController = new GameController(board);

        // Initially when the program is launched, all buttons should be disabled
        assertFalse(board.isFinishProgramButtonEnabled());
        assertFalse(board.isExecuteProgramButtonEnabled());
        assertFalse(board.isExecuteCurrentRegisterButtonEnabled());

        // Transition to PROGRAMMING phase and validate button-states
        board.setPhase(Phase.PROGRAMMING);
        gameController.activateExecutionButtons();
        assertTrue(board.isFinishProgramButtonEnabled());
        assertFalse(board.isExecuteProgramButtonEnabled());
        assertFalse(board.isExecuteCurrentRegisterButtonEnabled());

        // Transition to ACTIVATION phase and validate button-states
        board.setPhase(Phase.ACTIVATION);
        gameController.activateExecutionButtons();
        assertFalse(board.isFinishProgramButtonEnabled());
        assertTrue(board.isExecuteProgramButtonEnabled());
        assertTrue(board.isExecuteCurrentRegisterButtonEnabled());

    }

    // Test to check command card-functionality and phase-switch
    @Test
    public void testAllCommandCardsAndPhases() {
        // First create GameController to set up game with players, command cards and state
        Board board = new Board(10, 10);
        GameController gameController = new GameController(board);
        Player player = new Player(board, "blue", "player1");
        board.addPlayer(player);
        board.setCurrentPlayer(player);

        // Define the initial space where the player is located
        //Space initialSpace = new Space(board, 0, 0);
        Space initialSpace = board.getSpace(0, 0);
        player.setSpace(initialSpace);

        // Create loop to iterate through command cards
        for (int i = 0; i < player.getNumberOfRegisters(); i++) {
            CommandCardField programField = player.getProgramField(i);
            CommandCard card = null;
            switch(i) {
                case 0:
                    // Test command-card "Move Forward"
                    card = new CommandCard(Command.FORWARD);
                    break;
                case 1:
                    // Test command-card "Move Right"
                    card = new CommandCard(Command.RIGHT);
                    break;
                case 2:
                    // Test command-card "Move Left"
                    card = new CommandCard(Command.LEFT);
                    break;
                case 3:
                    // Tet command-card "Fast-Forward"
                    card = new CommandCard(Command.FAST_FORWARD);
                    break;
                default:
                    break;
            }

            programField.setCard(card);
        }

        // Execute all command cards
        gameController.executePlayerProgram(player);

        // Define the expected space, where the player should end up after execution of all command cards
        Space expectedSpace = new Space(board, 2, 3);
        player.setPosition(expectedSpace);
        player.setSpace(expectedSpace);

        // Validate that the initial position matches the expected position
        assertEquals("Player should move from the initial position to the expected position", expectedSpace, player.getPosition());

        // Validate that the "Finish-Programming-Button" is disabled initially
        assertFalse("'Finish-Programming-Button' is disabled initially once the game is started", gameController.board.isFinishProgramButtonEnabled());

        // Transition to next phase
        gameController.finishProgrammingPhase();

        // Validate that the current phase is the ACTIVATION-phase
        assertEquals("The phase should switch to ACTIVATION after pressing the 'Finish-Programming-Button'", Phase.ACTIVATION, gameController.board.getPhase());

        // Validate that the "Finish-Programming-Button" is enabled in the ACTIVATION-phase
        assertTrue("'Execute-Program-Button' should be enabled after pressing 'Finish-Programming-Button'", gameController.board.isExecuteProgramButtonEnabled());

        // Validate that "Execute-Current-Register-Button" is enabled in the ACTIVATION-phase
        assertTrue("'Execute-Current-Register-Button' should be enabled after pressing 'Finish-Programming-Button'", gameController.board.isExecuteCurrentRegisterButtonEnabled());

        // Execute the robot(s)' programming
        gameController.executePrograms();
    }

    @Test
    public void testNullCommandCard() {
        Board board = new Board(10, 10);
        GameController gameController = new GameController(board);
        Player player = new Player(board, "blue", "player1");
        board.addPlayer(player);
        board.setCurrentPlayer(player);

        // Set up the player's initial position
        Space initialSpace = new Space(board, 0, 0);
        player.setSpace(initialSpace);

        // Set a null command card for the player
        CommandCardField programField = player.getProgramField(0);
        programField.setCard(null);

        // Execute the player's program
        gameController.executePlayerProgram(player);

        // Ensure that the player's position remains unchanged
        assertEquals("Player's position should remain unchanged when the command card is null",
                initialSpace, player.getSpace());
    }

    @Test
    public void testPlayerMovementWithNonNullCommandCard() {
        Board board = new Board(10, 10);
        GameController gameController = new GameController(board);
        Player player = new Player(board, "blue", "player1");
        board.addPlayer(player);
        board.setCurrentPlayer(player);

        // Set up the player's initial position
        Space initialSpace = new Space(board, 0, 0);
        player.setSpace(initialSpace);

        // Set a non-null command card for the player
        CommandCardField programField = player.getProgramField(0);
        programField.setCard(new CommandCard(Command.FORWARD));

        // Execute the player's program
        gameController.executePlayerProgram(player);

        // Ensure that the player's position has moved forward
        assertEquals("Player's position should move forward when the command card is not null",
                new Space(board, 0, 1), player.getSpace());
    }
}
