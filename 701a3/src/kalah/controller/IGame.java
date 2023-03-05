package kalah.controller;

import com.qualitascorpus.testsupport.IO;
import kalah.model.Player;

/**
 * Controller interface for directing the rules of the game.
 */
public interface IGame {
    /**
     * Fetch an input string from the player
     *
     * @param io Input/Output stream to fetch input from
     * @return String Input from user
     */
    String getPlayerInput(IO io);

    /**
     * Fetch the winner of the game
     *
     * @return Player that has won the game, if the game has not been won then return null
     */
    Player getWinner();

    /**
     * Launch the main game loop that controls the flow of the game
     *
     * @param io Input/Output stream that the game loop must print and receive
     *           input from to direct the flow of the game
     */
    void play(IO io);
}
