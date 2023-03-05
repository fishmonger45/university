package kalah.model.counter;

abstract class Pit implements IPit {
    protected Integer seeds;

    protected Pit(Integer seeds) {
        this.seeds = seeds;
    }

    public Integer getSeeds() {
        return this.seeds;
    }

    public void setSeeds(Integer seeds) {
        this.seeds = seeds;
    }

    @Override
    public void incrementSeeds() {
        ++this.seeds;
    }

    @Override
    public void addSeeds(Integer seeds) {
        this.seeds += seeds;
    }
}
