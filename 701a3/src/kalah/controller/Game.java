package kalah.controller;

import com.qualitascorpus.testsupport.IO;
import kalah.model.Board;
import kalah.model.Player;
import kalah.dto.OwnedResource;
import kalah.view.Render;

public class Game implements IGame {
    private final Board board;
    private Player turn;

    public Game() {
        this.board = new Board();
        this.turn = Player.P1; // By default player 1 starts
    }

    @Override
    public String getPlayerInput(IO io) {
        String inputBuffer;

        // Prompt player 1
        if (this.turn.equals(Player.P1)) {
            inputBuffer = io.readFromKeyboard("Player P1's turn - Specify house number or 'q' to quit: ");
        }
        // Prompt player 2
        else {
            inputBuffer = io.readFromKeyboard("Player P2's turn - Specify house number or 'q' to quit: ");
        }
        return inputBuffer;
    }

    @Override
    public Player getWinner() {
        return board.isGameFinished(turn) ? turn : null;
    }

    @Override
    public void play(IO io) {
        // Print board
        Render.renderBoard(io,
                           new OwnedResource(this.board.getHouses().get(Player.P1),
                                             this.board.getStores().get(Player.P1)),
                           new OwnedResource(this.board.getHouses().get(Player.P2),
                                             this.board.getStores().get(Player.P2))
        );

        String inputBuffer = "";
        while (this.getWinner() == null) {
            inputBuffer = this.getPlayerInput(io);

            // Quit
            if (inputBuffer.equals("q")) {
                break;
            }
            // Select house
            else {
                try {
                    int selectedHouse = Integer.parseInt(inputBuffer);
                    turn = this.board.moveSeeds(selectedHouse - 1, this.turn, io); // Offset for index
                    Render.renderBoard(io,
                                       new OwnedResource(this.board.getHouses().get(Player.P1),
                                                         this.board.getStores().get(Player.P1)),
                                       new OwnedResource(this.board.getHouses().get(Player.P2),
                                                         this.board.getStores().get(Player.P2))
                    );
                }
                // Invalid input
                catch (Error e) {
                    String invalidInput = String.format("Please input either \"q\" or a number from 1-%1d",
                                                        Board.NUMBER_HOUSES);
                    io.println(invalidInput);
                }
            }
        }

        // Finalize game by printing game over flavor text and rendering scores
        Render.renderGameOver(io);
        Render.renderBoard(io,
                           new OwnedResource(this.board.getHouses().get(Player.P1),
                                             this.board.getStores().get(Player.P1)),
                           new OwnedResource(this.board.getHouses().get(Player.P2),
                                             this.board.getStores().get(Player.P2))
        );

        if (!inputBuffer.equals("q")) {
            Render.renderScore(io, this.board.calculateScore());
        }
    }

}
