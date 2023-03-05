package kalah.model.counter;

/**
 * Kalah house, serving the purpose of a buffer to store seeds, seeds can be placed in a store (usually one at a time
 * by Kalah rules), seeds are taken all at once from a store for distribution
 */
public class House extends Pit {

    public House(Integer seeds) {
        super(seeds);
    }

    @Override
    public void addSeeds(Integer seeds) {
        this.seeds += seeds;
    }
}
