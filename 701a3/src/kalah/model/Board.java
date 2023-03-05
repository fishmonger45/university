package kalah.model;

import com.qualitascorpus.testsupport.IO;
import kalah.model.counter.House;
import kalah.model.counter.Store;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class Board implements IBoard {

    public static final int NUMBER_HOUSES = 6;
    public static final int STARTING_SEEDS = 4;

    private Map<Player, House[]> houses = new HashMap<>();
    private Map<Player, Store> stores = new HashMap<>();

    public Board() {
        // Initialize player houses
        House[] p1Houses = new House[NUMBER_HOUSES];
        House[] p2Houses = new House[NUMBER_HOUSES];
        for (int i = 0; i < NUMBER_HOUSES; ++i) {
            p1Houses[i] = new House(STARTING_SEEDS);
            p2Houses[i] = new House(STARTING_SEEDS);
        }

        this.houses.put(Player.P1, p1Houses);
        this.houses.put(Player.P2, p2Houses);

        // Initialize player stores
        this.stores.put(Player.P1, new Store(0));
        this.stores.put(Player.P2, new Store(0));

    }

    public Map<Player, House[]> getHouses() {
        return houses;
    }


    public Map<Player, Store> getStores() {
        return stores;
    }

    @Override
    public boolean isGameFinished(Player player) {
        House[] houses = this.houses.get(player);
        int houseSeeds = 0;
        for (int i = 0; i < NUMBER_HOUSES; ++i) {
            houseSeeds += houses[i].getSeeds();
        }

        return houseSeeds == 0;
    }

    @Override
    public Player moveSeeds(int houseIndex, Player player, IO io) {
        // Validate input
        if (houseIndex < 0 || houseIndex >= NUMBER_HOUSES) {
            return player;
        }
        // Check for empty house
        if (this.houses.get(player)[houseIndex].getSeeds() == 0) {
            io.println("House is empty. Move again.");
            return player;
        }

        // Side of the board
        Player side = player;
        int spreadSeeds;

        // Remove seeds from players house to spread and iterate to next house
        spreadSeeds = this.houses.get(side)[houseIndex].getSeeds();
        this.houses.get(side)[houseIndex].setSeeds(0);
        houseIndex++;

        while (spreadSeeds != 0) {
            // Store
            if (houseIndex == NUMBER_HOUSES) {
                if (side.equals(player)) {
                    this.stores.get(player).incrementSeeds();
                    --spreadSeeds;
                }
                if (spreadSeeds == 0) {
                    return player;
                }
                side = Player.otherPlayer(side);
                houseIndex = 0;
            }
            // House
            else {
                // On players side
                if (side.equals(player)) {
                    // Rule for taking seeds in opposite players house, one seed remaining, current house has zero seeds
                    // Must also be on players own side on their own turn
                    if (spreadSeeds == 1 && this.houses.get(player)[houseIndex].getSeeds() == 0) {
                        Player oppositePlayer = Player.otherPlayer(player);
                        int oppositeIndex = getOppositeHouse(houseIndex);
                        int oppositeSeeds = this.houses.get(oppositePlayer)[oppositeIndex].getSeeds();
                        // Cannot capture opponents house if it is empty
                        if (oppositeSeeds != 0) {
                            this.houses.get(oppositePlayer)[oppositeIndex].setSeeds(0);
                            this.stores.get(player).addSeeds(1 + oppositeSeeds);
                        } else {
                            this.houses.get(player)[houseIndex].incrementSeeds();
                        }
                    }
                    // If not using last seed rule then simply place the seed
                    else {
                        this.houses.get(player)[houseIndex].incrementSeeds();
                    }
                }
                // On opponents side, place a seed, cannot capture house
                else {
                    this.houses.get(side)[houseIndex].incrementSeeds();
                }
                houseIndex++;
                --spreadSeeds;
            }
        }

        // Opposite players turn
        return Player.otherPlayer(player);
    }

    @Override
    public int getOppositeHouse(int houseIndex) {
        return (NUMBER_HOUSES - 1) - houseIndex;
    }

    @Override
    public AbstractMap.SimpleEntry<Integer, Integer> calculateScore() {
        int p1Score, p2Score;
        p1Score = this.stores.get(Player.P1).getSeeds();
        p2Score = this.stores.get(Player.P2).getSeeds();
        for (int i = 0; i < Board.NUMBER_HOUSES; ++i) {
            p1Score += this.houses.get(Player.P1)[i].getSeeds();
            p2Score += this.houses.get(Player.P2)[i].getSeeds();
        }
        return new AbstractMap.SimpleEntry<>(p1Score, p2Score);
    }
}
