package kalah.model;

/**
 * Players of a game representing a disjoint union (sum type)
 */
public enum Player {
    P1,
    P2,
    ;

    /**
     * FInd the player that the input player is playing against
     *
     * @param player The current player
     * @return Player The input player is playing against
     */
    public static Player otherPlayer(Player player) {
        if (player.equals(Player.P1)) {
            return Player.P2;
        } else {
            return Player.P1;
        }
    }
}



