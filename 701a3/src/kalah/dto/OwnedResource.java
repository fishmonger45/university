package kalah.dto;

import kalah.model.counter.House;
import kalah.model.counter.Store;

/**
 * Player owned resources, used as a common object between different class domains
 */
public class OwnedResource {
    House[] houses;
    Store store;

    public OwnedResource(House[] houses, Store store) {
        this.houses = houses;
        this.store = store;
    }

    public House[] getHouses() {
        return houses;
    }

    public Store getStore() {
        return store;
    }

}
