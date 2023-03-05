package kalah.model.counter;

public interface IPit {
    /**
     * Increment the amount of seeds in a pit by one (1)
     */
    void incrementSeeds();

    /**
     * Add an amount of seeds in a pit
     *
     * @param seeds Number of seeds to add to the pit
     */
    void addSeeds(Integer seeds);
}
