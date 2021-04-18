package ch.epfl.tchu.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Player.TurnKind;
import ch.epfl.tchu.game.Route.Level;
import ch.epfl.tchu.gui.Info;

/**
 * Final and non-instanciable class that represents a part of the game.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class Game {
	
    private static GameState gameState;
	/**
	 * Private constructor existing for the sole purpose of not allowing this class to not
	 * be instanciating (at least outside of this class).
	 */
	private Game() {}
	
	/**
	 * Makes a game of tCHu be played with the characteristics passed as parameters.
	 * @param players (Map<PlayerId, Player>): A map linking the player IDs with the players they represent.
	 * @param playerNames (Map<PlayerId, String>): A map linking the player IDs with the player names they represent.
	 * @param tickets (SortedBag<Ticket>): The tickets available for this game.
	 * @param rng (Random): A random variable used to randomize certain aspects of the game.
	 */
	public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng){
	    Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);
	    //playerInfoMap to more efficiently handle Info objects.
        Map<PlayerId, Info> playerInfoMap = new HashMap<>();
        players.forEach((playerId, player) -> playerInfoMap.put(playerId, new Info(playerNames.get(playerId))));
	    
        GameState gameState = initializeGame(players,  playerNames, playerInfoMap, tickets, rng);
        
        // loop that plays the game.
        while(true) {
            gameState = playTurn(playerInfoMap, gameState, players, rng);
            updateState(players, gameState);
            if(gameState.lastTurnBegins()) {
                gameState = gameState.forNextTurn();
                break;
            }
            gameState = gameState.forNextTurn();
        }
        
        // Plays out two extra turns.
        for(int i = 0; i < players.size(); i++) {
            gameState = playTurn(playerInfoMap, gameState, players, rng);
            updateState(players, gameState);
            if(gameState.currentPlayerId() != gameState.lastPlayer()) {
                gameState = gameState.forNextTurn();        
            }
        }
        updateState(players, gameState);
        calculatePoints(gameState, players,  playerNames, playerInfoMap);
	}
	
    /**
     * Initializes a game performing all the tasks needed to be performed at the start of a game.
     * @param players (Map<PlayerId, Player>): A map linking the player IDs with the players they represent.
     * @param playerNames (Map<PlayerId, String>): A map linking the player IDs with the player names they represent.
     * @param playerInfoMap (Map<PlayerId, Info>): A map linking the Info objects of player to their Player ID.
     * @param tickets (SortedBag<Ticket>): The tickets available for this game.
     * @param rng (Random): A random variable used to randomize certain aspects of the game.
     * @return (GameState): a GameState initialized with all the steps needed to be performed at the start of a game.
     */
	private static GameState initializeGame(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, Map<PlayerId,Info> playerInfoMap, SortedBag<Ticket> tickets, Random rng) {
	    gameState = GameState.initial(tickets, rng);
	    players.forEach((playerId, player) -> player.initPlayers(playerId, playerNames));
	    receiveInfoForBoth(playerInfoMap.get(gameState.currentPlayerId()).willPlayFirst(), players);
	    
	    for(PlayerId id: PlayerId.ALL) {
	        players.get(id).setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
	        gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
	        players.get(id).updateState(gameState, gameState.playerState(id));
	    }
        updateState(players, gameState);
        
        players.forEach(((playerId, player) -> gameState = gameState.withInitiallyChosenTickets(playerId, player.chooseInitialTickets())));
        playerInfoMap.forEach((playerId, info) -> info.keptTickets(gameState.playerState(playerId).ticketCount()));
        return gameState;
	}
	
	
    /**
     * Private static method that is called at the end of the game to calculate the points of both players and declare the victor. 
     * @param gameState (GameState): the given game state at the end of the game.
     * @param players (Map<PlayerId, Player>): A map linking each player id to the player it refers to.
     * @param playerNames (Map<PlayerId, String>): A map linking the player IDs with the player names they represent.
     * @param playerInfoMap (Map<PlayerId, Info>): A map linking the Info objects of player to their Player ID.
     */
	private static void calculatePoints(GameState gameState, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, Map<PlayerId, Info> playerInfoMap) {
	    Map<PlayerId, Trail> longestMap = new HashMap<PlayerId, Trail>();
	    
	    players.forEach((playerId, player) -> longestMap.put(playerId, Trail.longest(gameState.playerState(playerId).routes())));
        
	    int bonus1 = 0, bonus2 = 0;
        int length1 = longestMap.get(PlayerId.PLAYER_1).length(); 
        int length2 = longestMap.get(PlayerId.PLAYER_2).length();
        
        if(length1 > length2) {
            receiveInfoForBoth(playerInfoMap.get(PlayerId.PLAYER_1).getsLongestTrailBonus(longestMap.get(PlayerId.PLAYER_1)), players);
            bonus1 = 10;   
            
        } else if(length1 < length2) {
            receiveInfoForBoth(playerInfoMap.get(PlayerId.PLAYER_2).getsLongestTrailBonus(longestMap.get(PlayerId.PLAYER_2)), players);
            bonus2 = 10;
            
        } else {
            receiveInfoForBoth(playerInfoMap.get(PlayerId.PLAYER_1).getsLongestTrailBonus(longestMap.get(PlayerId.PLAYER_1)) 
                    + playerInfoMap.get(PlayerId.PLAYER_2).getsLongestTrailBonus(longestMap.get(PlayerId.PLAYER_2)), players);
            bonus1 = 10; bonus2 = 10;
        }
        
        int finalPoints1 = gameState.playerState(PlayerId.PLAYER_1).finalPoints() + bonus1;
        int finalPoints2 = gameState.playerState(PlayerId.PLAYER_2).finalPoints() + bonus2;
        
        if(finalPoints1 > finalPoints2) {
            receiveInfoForBoth(playerInfoMap.get(PlayerId.PLAYER_1).won(finalPoints1, finalPoints2), players);
            
        } else if(finalPoints1 < finalPoints2) {
            receiveInfoForBoth(playerInfoMap.get(PlayerId.PLAYER_2).won(finalPoints2, finalPoints1), players);
        } else {
            List<String> playerNameList = List.of(playerNames.get(PlayerId.PLAYER_1), playerNames.get(PlayerId.PLAYER_2));
            receiveInfoForBoth(Info.draw(playerNameList, finalPoints1), players);
        }
	}
	
	/**
     * Method that Plays out a turn of a certain player with the given game state, which then returns a modified gameState at the end of the turn.
     * @param playerInfoMap (Map<PlayerId, Info>): Allows the access of the Info object of both players.
     * @param gameState (GameState): the given game state used to play out a turn.
     * @param players (Map<PlayerId, Player>): A map linking each player id to the player it refers to.
     * @param currentPlayerId (PlayerId): the ID of the current player.
     * @param rng (Random): A random variable used to randomize certain aspects of the game.
     * @return (GameState): an updated game state after the turn of the given player has been played.
     */
	private static GameState playTurn(Map<PlayerId, Info> playerInfoMap, GameState gameState, Map<PlayerId, Player> players, Random rng) {
	    //Start of turn and Choosing Type of Turn
        receiveInfoForBoth(playerInfoMap.get(gameState.currentPlayerId()).canPlay(), players);
        updateState(players, gameState);
        TurnKind chosenTurnKind = players.get(gameState.currentPlayerId()).nextTurn();
        
        // Turn type: Choose Tickets
        switch(chosenTurnKind) {
        case DRAW_TICKETS:  gameState = drawTicketTurn(gameState, players, playerInfoMap);
                            break;
        case DRAW_CARDS:    gameState = drawCardsTurn(gameState, players, playerInfoMap, rng);
                            break;
        case CLAIM_ROUTE:   gameState = claimRouteTurn(gameState, players, playerInfoMap, rng);
                            break;
        }
        
        //informs the player that the last turn has begun
        if(gameState.lastTurnBegins()) {
            receiveInfoForBoth(playerInfoMap.get(gameState.currentPlayerId()).lastTurnBegins(gameState.currentPlayerState().carCount()), players);  
        }
        
        return gameState;
	}
	
	/**
	 * Method that plays out a turn of the player where he chooses to draw tickets. 
	 * @param gameState (GameState) : the current gameState fed into the method to update it.
	 * @param players (Map<PlayerId, Player>) : Map of the players mapped to their respective playerIds
	 * @param playerInfoMap (Map<playerId, Info>) : Map of the player info objects mapped to their respective playerIds
	 * @return (GameState) : the new updated gameState
	 */
	private static GameState drawTicketTurn(GameState gameState , Map<PlayerId, Player> players , Map<PlayerId, Info> playerInfoMap) {
	    receiveInfoForBoth(playerInfoMap.get(gameState.currentPlayerId()).drewTickets(3), players);
        SortedBag<Ticket> chosenTickets = players.get(gameState.currentPlayerId()).chooseTickets(gameState.topTickets(3));
        gameState = gameState.withChosenAdditionalTickets(gameState.topTickets(3), chosenTickets);
        receiveInfoForBoth(playerInfoMap.get(gameState.currentPlayerId()).keptTickets(chosenTickets.size()), players);
        return gameState;
	}
	
	/**
	 * Method that plays out a turn of the player where he chooses to draw tickets. 
     * @param gameState (GameState) : the current gameState fed into the method to update it.
     * @param players (Map<PlayerId, Player>) : Map of the players mapped to their respective playerIds
     * @param playerInfoMap (Map<playerId, Info>) : Map of the player info objects mapped to their respective playerIds
	 * @param rng (Random) : Random variable for randomizing the recreation of the Deck
	 * @return (GameState) : the new updated gameState
	 */
	private static GameState drawCardsTurn(GameState gameState , Map<PlayerId, Player> players , Map<PlayerId, Info> playerInfoMap, Random rng) {
	    for(int i = 0; i < 2; i++) {
            if(i == 1) {
                updateState(players, gameState);
            }
            int drawSlot = players.get(gameState.currentPlayerId()).drawSlot();
            if(Constants.FACE_UP_CARD_SLOTS.contains(drawSlot)) {
                receiveInfoForBoth(playerInfoMap.get(gameState.currentPlayerId()).drewVisibleCard(gameState.cardState().faceUpCard(drawSlot)), players);
                gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                gameState = gameState.withDrawnFaceUpCard(drawSlot);
            } else if(drawSlot == Constants.DECK_SLOT) {
                receiveInfoForBoth(playerInfoMap.get(gameState.currentPlayerId()).drewBlindCard(), players);
                gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                gameState = gameState.withBlindlyDrawnCard();
            }
        }
	    return gameState;
	}
	
	/**
	 * 
	 * Method that plays out a turn of the player where he chooses to draw tickets. 
     * @param gameState (GameState) : the current gameState fed into the method to update it.
     * @param players (Map<PlayerId, Player>) : Map of the players mapped to their respective playerIds
     * @param playerInfoMap (Map<playerId, Info>) : Map of the player info objects mapped to their respective playerIds
     * @param rng (Random) : Random variable for randomizing the recreation of the Deck
	 * @return (GameState) : the new updated gameState
	 */
	private static GameState claimRouteTurn(GameState gameState , Map<PlayerId, Player> players , Map<PlayerId, Info> playerInfoMap, Random rng) {
	    Route currentRoute = players.get(gameState.currentPlayerId()).claimedRoute();
        SortedBag<Card> initialClaimCards = players.get(gameState.currentPlayerId()).initialClaimCards();
        
        if(currentRoute.level() == Level.OVERGROUND) {
            if(!initialClaimCards.isEmpty()  && gameState.currentPlayerState().canClaimRoute(currentRoute) ) {
                receiveInfoForBoth(playerInfoMap.get(gameState.currentPlayerId()).claimedRoute(currentRoute, initialClaimCards), players);
                gameState = gameState.withClaimedRoute(currentRoute, initialClaimCards);
                
            } else {
                receiveInfoForBoth(playerInfoMap.get(gameState.currentPlayerId()).didNotClaimRoute(currentRoute), players);
            }
        }
        
        else if(currentRoute.level() == Level.UNDERGROUND) {
            if(!initialClaimCards.isEmpty()  && gameState.currentPlayerState().canClaimRoute(currentRoute) ) {
                receiveInfoForBoth(playerInfoMap.get(gameState.currentPlayerId()).attemptsTunnelClaim(currentRoute, initialClaimCards), players);
                
                // Additional Cards Being Drawn
                SortedBag<Card> drawnCards;
                SortedBag<Card> additionalCards = SortedBag.of();
                SortedBag.Builder<Card> drawnCardsBuilder = new SortedBag.Builder<>();
                for (int additionalCardCount = 0; additionalCardCount < Constants.ADDITIONAL_TUNNEL_CARDS; additionalCardCount++) {
                    gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                    drawnCardsBuilder.add(gameState.topCard());
                    gameState = gameState.withoutTopCard();  
                    gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                }
                drawnCards = drawnCardsBuilder.build();
                gameState = gameState.withMoreDiscardedCards(drawnCards);
                
                //Building of Possible Additional Cards + Informing player of drawn Additional Cards
                int additionalCardCount = currentRoute.additionalClaimCardsCount(initialClaimCards, drawnCards);
                receiveInfoForBoth(playerInfoMap.get(gameState.currentPlayerId()).drewAdditionalCards(drawnCards, additionalCardCount), players);
                
                List<SortedBag<Card>> possibleAdditionalCards = additionalCardCount >= 1 ? 
                        gameState.currentPlayerState().possibleAdditionalCards(additionalCardCount, initialClaimCards, drawnCards) 
                        : List.of();
                
                // Management of the claiming of the route if the additionalCardCount implies an additional cost and can be fulfilled by the player.
                if( additionalCardCount >= 1 && !possibleAdditionalCards.isEmpty()){ 
                    additionalCards = players.get(gameState.currentPlayerId()).chooseAdditionalCards(possibleAdditionalCards);
                    if(!additionalCards.isEmpty()) {
                        receiveInfoForBoth(playerInfoMap.get(gameState.currentPlayerId()).claimedRoute(currentRoute, additionalCards.union(initialClaimCards)), players);
                        gameState = gameState.withClaimedRoute(currentRoute, additionalCards.union(initialClaimCards));
                    } else {
                        receiveInfoForBoth(playerInfoMap.get(gameState.currentPlayerId()).didNotClaimRoute(currentRoute), players);
                    }
                // Management of the claiming of the route if there is no additional Cost.
                } else if(additionalCardCount == 0) {
                    receiveInfoForBoth(playerInfoMap.get(gameState.currentPlayerId()).claimedRoute(currentRoute, initialClaimCards), players);
                    gameState = gameState.withClaimedRoute(currentRoute, initialClaimCards);
                } else {
                    receiveInfoForBoth(playerInfoMap.get(gameState.currentPlayerId()).didNotClaimRoute(currentRoute), players);
                }
            } else {
                receiveInfoForBoth(playerInfoMap.get(gameState.currentPlayerId()).didNotClaimRoute(currentRoute), players);
            }
        }
        return gameState;
	}
	
	/**
     * A private method that essentially calls the receiveInfo method for both players in the game specified
     * by the players Map.
     * @param info (String): the info to be sent to both players.
     * @param players (Map<PlayerId, Player>) the map specifying the players to whom the message must be sent.
     */
    private static void receiveInfoForBoth(String info, Map<PlayerId, Player> players) {
        players.forEach((playerId, player) -> player.receiveInfo(info));
    }
    
    /**
     * Updates the state of both players by their provided updateState methods. 
     * @param players (Map<PlayerId, Player>): A map that links each player to their respective playerId.
     * @param gameState (GameState): Current gameState of the game that must be provided in order to update the player and game.
     */
    private static void updateState(Map<PlayerId, Player> players, GameState gameState) {
        players.forEach((playerId, player) -> player.updateState(gameState, gameState.playerState(playerId)));
    }
}