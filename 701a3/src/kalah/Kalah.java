package kalah;

import com.qualitascorpus.testsupport.IO;
import com.qualitascorpus.testsupport.MockIO;
import kalah.controller.Game;
import kalah.controller.IGame;

/**
 * Main entrypoint to Kalah game loop
 *
 * @See Game
 */
public class Kalah {

    private static IGame game;

    public Kalah() {
        game = new Game();
    }

    public static void main(String[] args) {
        new Kalah().play(new MockIO());
    }

    public void play(IO io) {
        game.play(io);
    }
}
