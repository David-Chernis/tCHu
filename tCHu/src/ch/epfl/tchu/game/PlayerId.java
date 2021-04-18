package ch.epfl.tchu.game;

import java.util.List;

/**
 * The enumerable type PlayerID represents the 2 different players' IDs playing the game.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public enum PlayerId {
	PLAYER_1, PLAYER_2;
	
	/**
	 * (List<PlayerID>): list of all the different types of PlayerID.
	 */
    public static final List<PlayerId> ALL = List.of(PlayerId.values());
    
    /**
     * (int): number of types of PlayerID.
     */
    public static final int COUNT = ALL.size();
    
    /**
     * Returns the identity of the player who follows the one to whom this method is applied.
     * @return (PlayerId): the identity of the player who follows the one to whom this method is applied.
     */
    public PlayerId next() {
        return this.equals(PLAYER_1) ? PLAYER_2 : PLAYER_1;
    }
}
