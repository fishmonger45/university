package kalah.model;

import com.qualitascorpus.testsupport.IO;

import java.util.AbstractMap;

/**
 * Representation of the board that the players play a game on, provides interaction mechanism for moving seeds
 * around a board
 */
public interface IBoard {
    /**
     * Test if the game is finished for a particular player. A game can be finished for one player but continue for
     * the opposite/other player. A game is finished for a player when all houses belonging to the player contain
     * zero seeds
     *
     * @param player The player to check if the game is finished for
     * @return boolean If the game is finished for the player
     */
    boolean isGameFinished(Player player);

    /**
     * Move the seeds on a board.
     *
     * @param houseIndex Index of the house to move seeds from
     * @param player     Player moving the seeds
     * @param io         Input/output to print against
     * @return Player The next player turn
     */
    Player moveSeeds(int houseIndex, Player player, IO io);

    /**
     * Fetch the house index of the house opposite the players house
     *
     * @param houseIndex The input house to find the opposite index of
     * @return int Index of the
     */
    int getOppositeHouse(int houseIndex);

    /**
     * Calculate the score of the players
     *
     * @return Pair<Integer, Integer> A pair of scores, with the first element being the player 1 score and the
     * second being player 2 score
     */
    AbstractMap.SimpleEntry<Integer, Integer> calculateScore();
}
