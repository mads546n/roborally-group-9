/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
        // TODO Task1: method should be implemented by the students:
        //   - the current player should be moved to the given space
        //     (if it is free())
        //   - and the current player should be set to the player
        //     following the current player
        //   - the counter of moves in the game should be increased by one
        //     if the player is moved

        Player currentPlayer = board.getCurrentPlayer();

        if (space.getPlayer() == null) { // Check if the given space is empty
            currentPlayer.setSpace(space); // Move the current player to the given space

            // Find the index of current player
            int currentPlayerIndex = -1;
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                if (board.getPlayer(i) == currentPlayer) {
                    currentPlayerIndex = i;
                    break;
                }
            }

            // Designate the current player to be the player following the current player
            int nextPlayerIndex = (currentPlayerIndex + 1) % board.getPlayersNumber();
            Player nextPlayer = board.getPlayer(nextPlayerIndex);
            board.setCurrentPlayer(nextPlayer);

            // Increment the move counter
            int currentMove = board.getMoveCounter();
            board.setMoveCounter(currentMove + 1);
        }

    }

    // XXX: implemented in the current version
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        activateExecutionButtons();

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX: implemented in the current version
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX: implemented in the current version
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setStep(0);
        activateExecutionButtons();
    }

    // XXX: implemented in the current version
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: implemented in the current version
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX: implemented in the current version
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();

        // Execute programming for all the robots in the current game
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            executePlayerProgram(player);
        }

        // Increment move counter
        int currentMove = board.getMoveCounter();
        board.setMoveCounter(currentMove + 1);

        // Switch to next player
        board.switchToNextPlayer();

        // Activate execution buttons
        activateExecutionButtons();
    }

    // Method to assist in executing program for a single player
    public void executePlayerProgram(Player player) {
        for (int i = 0; i < Player.NO_REGISTERS; i++) {
            CommandCard card = player.getProgramField(i).getCard();
            if (card != null) {
                executeCommand(player, card.command);
            }
        }
    }

    // Method to execute the current register of the current robot playing
    public void executeCurrentRegister() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player currentPlayer = board.getPlayer(i);
            CommandCard card = currentPlayer.getProgramField(board.getStep()).getCard();
            if (card != null) {
                executeCommand(currentPlayer, card.command);
            }
        }
    }

    // XXX: implemented in the current version
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
        activateExecutionButtons();
    }

    // XXX: implemented in the current version
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    // XXX: implemented in the current version
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    // XXX: implemented in the current version
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    // TODO Task2
    public void moveForward(@NotNull Player player) {
        // First get the current space the player is standing on
        Space currentSpace = player.getSpace();

        // Find the "neighbour"-space located in the direction which the current player is moving
        if (currentSpace != null) {
            Heading heading = player.getHeading();
            Space nextSpace = board.getNeighbour(currentSpace, heading);

            if (nextSpace != null && nextSpace.getPlayer() == null) {
                //Check if the space is empty and move the player accordingly
                player.setSpace(nextSpace);
            }
        }
    }

    // TODO Task2
    public void fastForward(@NotNull Player player) {
        // For the sake of simplicity fastForward is implemented similarly to moveForward
        moveForward(player);
    }

    // TODO Task2
    public void turnRight(@NotNull Player player) {
        // Get current heading of player
        Heading currentHeading = player.getHeading();
        // Calculate the new heading after former move
        Heading newHeading = currentHeading.turnClockwise();
        // Now update the player's new heading
        player.setHeading(newHeading);
    }

    // TODO Task2
    public void turnLeft(@NotNull Player player) {
        Heading currentHeading = player.getHeading();
        Heading newHeading = currentHeading.turnCounterClockwise();
        player.setHeading(newHeading);
    }

    public void activateExecutionButtons() {
        Phase phase = board.getPhase();

        // Switch to enable or disable buttons depending on the current phase
        switch (phase) {
            case PROGRAMMING:
                // Enable "Finish Programming"
                board.setFinishProgramButtonEnabled(true);
                //Disable "Execute Program" and "Execute Current Register"
                board.setExecuteProgramButtonEnabled(false);
                board.setExecuteCurrentRegisterButtonEnabled(false);
                break;

            case ACTIVATION:
                // Disable "Finish Programming"
                board.setFinishProgramButtonEnabled(false);

                // Enable "Execute Program"
                board.setExecuteProgramButtonEnabled(true);

                // Enable "Execute Current Register"
                board.setExecuteCurrentRegisterButtonEnabled(true);
                break;

            default:
                // If the phase is not recognized as an "actual" phase, disable all buttons
                board.setFinishProgramButtonEnabled(false);
                board.setExecuteProgramButtonEnabled(false);
                board.setExecuteCurrentRegisterButtonEnabled(false);
                break;
        }
    }

    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

}
