package kalah.view;

import com.qualitascorpus.testsupport.IO;
import kalah.model.counter.House;
import kalah.model.counter.Store;
import kalah.dto.OwnedResource;

import java.util.AbstractMap;

public class Render {

    /**
     * Render the board to the IO stream
     *
     * @param io         I/O stream to push text to
     * @param p1Resource Resources owned by player 1
     * @param p2Resource Resources owned by player 2
     */
    public static void renderBoard(IO io, OwnedResource p1Resource, OwnedResource p2Resource) {
        House[] p1Houses = p1Resource.getHouses();
        House[] p2Houses = p2Resource.getHouses();
        Store p1Store = p1Resource.getStore();
        Store p2Store = p2Resource.getStore();
        String p2BoardLine = String.format("| P2 | 6[%2d] | 5[%2d] | 4[%2d] " +
                                                   "|" + " 3[%2d] | 2[%2d] | 1[%2d] | %2d |",
                                           p2Houses[5].getSeeds(), p2Houses[4].getSeeds(),
                                           p2Houses[3].getSeeds(), p2Houses[2].getSeeds(),
                                           p2Houses[1].getSeeds(), p2Houses[0].getSeeds(),
                                           p1Store.getSeeds());
        String p1BoardLine = String.format("| %2d | 1[%2d] | 2[%2d] | 3[%2d] "
                                                   + "| 4[%2d] | 5[%2d] | 6[%2d] | P1 |",
                                           p2Store.getSeeds(), p1Houses[0].getSeeds(),
                                           p1Houses[1].getSeeds(), p1Houses[2].getSeeds(),
                                           p1Houses[3].getSeeds(), p1Houses[4].getSeeds(),
                                           p1Houses[5].getSeeds());
        io.println("+----+-------+-------+-------+-------+-------+-------+----+");
        io.println(p2BoardLine);
        io.println("|    |-------+-------+-------+-------+-------+-------|    |");
        io.println(p1BoardLine);
        io.println("+----+-------+-------+-------+-------+-------+-------+----+");
    }

    /**
     * Render a score card for the players of the game
     *
     * @param io     I/O stream to print score to
     * @param scores Scores of the players, player 1 score as the key (first element, player 2 score as the value
     *               (second element)
     */
    public static void renderScore(IO io, AbstractMap.SimpleEntry<Integer, Integer> scores) {
        int p1Score = scores.getKey();
        int p2Score = scores.getValue();
        io.println(String.format("\tplayer 1:%d", p1Score));
        io.println(String.format("\tplayer 2:%d", p2Score));
        if (p1Score > p2Score) {
            io.println("Player 1 wins!");
        } else if (p1Score < p2Score) {
            io.println("Player 2 wins!");
        } else {
            io.println("A tie!");
        }
    }

    /**
     * Render game over flavor text
     *
     * @param io I/O stream to print gamer over to
     */
    public static void renderGameOver(IO io) {
        io.println("Game over");
    }
}
