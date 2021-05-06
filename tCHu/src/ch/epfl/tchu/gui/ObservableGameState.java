package ch.epfl.tchu.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Station;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static ch.epfl.tchu.game.Constants.FACE_UP_CARD_SLOTS;

/**
 * Class that represents an observable game state.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public class ObservableGameState {
	
    private PublicGameState ogs;
    private PlayerState ops;
    private PlayerId id; 
    // Group 1
    private final IntegerProperty ticketPercentage; 
    private final IntegerProperty cardPercentage;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final Map<Route, ObjectProperty<PlayerId>> routeOwners;
    
    // Group 2
    private final List<IntegerProperty> playerTicketsNum;
    private final List<IntegerProperty> playerTotalCardsNum;
    private final List<IntegerProperty> playerWagonsNum;
    private final List<IntegerProperty> playerClaimPoints;
    
    // Group 3
    private final ObservableList<Ticket> playerTickets;
    private final List<IntegerProperty> playerCards;
    private final Map<Route, BooleanProperty> claimableRoutes;
    
    /**
     * Public ObservableGameState constructor. Initializes an ObservableGameState using its id parameter.
     * @param id (PlayerId): the id of the player whose player state is shown in this instance of
     * ObservableGameState.
     */
    public ObservableGameState(PlayerId id){
        this.id = id;
        faceUpCards = createFaceUpCards();
        routeOwners = createRoutes();
        ticketPercentage = new SimpleIntegerProperty();
        cardPercentage = new SimpleIntegerProperty();
        playerTicketsNum = createIntList(2);
        playerTotalCardsNum = createIntList(2);
        playerWagonsNum = createIntList(2);
        playerClaimPoints = createIntList(2);
        playerTickets = FXCollections.observableArrayList();
        playerCards = createIntList(9);
        claimableRoutes = createOwnedRoutes();
    }
    
    /**
     * Creates and returns a list of IntegerProperty instances.
     * @param n (int): the number of IntegerProperties to be created and added to the new list.
     * @return (List<IntegerProperty>): returns a newly created list of IntegerProperty instances.
     */
    private List<IntegerProperty> createIntList(int n){
        List<IntegerProperty> newList = new ArrayList<>();
        for(int i = 0; i < n; i++) {
            newList.add(new SimpleIntegerProperty());
        }
        return newList;
    } 
    
    /**
     * Creates and returns a list of 5 ObjectProperty instances of the generic type Card.
     * @return (List<ObjectProperty<Card>>): a list of 5 newly created ObjectProperty instances of
     * the generic type Card.
     */
    private List<ObjectProperty<Card>> createFaceUpCards(){
        List<ObjectProperty<Card>> newList = new ArrayList<>();
        for (@SuppressWarnings("unused") int slot : FACE_UP_CARD_SLOTS) {
            newList.add(new SimpleObjectProperty<Card>());
        }
        return newList;
    }
    
    /**
     * Creates and returns a map of ObjectProperty instances of the generic type PlayerId and with
     * keys of type Route.
     * @return (Map<Route, ObjectProperty<PlayerId>>): returns a newly created map of ObjectProperty
     * instances of the generic type PlayerId and with keys of type Route.
     */
    private Map<Route, ObjectProperty<PlayerId>> createRoutes(){
        Map<Route, ObjectProperty<PlayerId>> newRoutes = new HashMap<>();
        for(Route r : ChMap.routes()) {
            newRoutes.put(r, new SimpleObjectProperty<>());
        }
        return newRoutes;
    }
    
    /**
     * Creates and returns a map of BooleanProperty instances and with keys of type Route.
     * @return (Map<Route, ObjectProperty<PlayerId>>): returns a newly created map of BooleanProperty
     * instances and with keys of type Route.
     */
    private Map<Route, BooleanProperty> createOwnedRoutes(){
        Map<Route, BooleanProperty> newRoutes = new HashMap<>();
        for(Route r : ChMap.routes()) {
            newRoutes.put(r, new SimpleBooleanProperty());
        }
        return newRoutes;
    }
    
    /**
     * Updates all the attributes within ObservableGameState using the values provided
     * in the parameters of the method.
     * @param newGameState (PublicGameState): the new public game state.
     * @param newPlayerState (PlayerState): the new player state.
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
       ogs = newGameState;
       ops = newPlayerState;
       Set<List<Station>> doubles = new HashSet<>();
       cardPercentage.set(newGameState.cardState().deckSize() / Constants.TOTAL_CARDS_COUNT);
       ticketPercentage.set(newGameState.ticketsCount() / 46);
       playerTickets.setAll(newPlayerState.tickets().toList());
       for(Route r: newGameState.claimedRoutes()) {
           doubles.add(r.stations());
       }
       
       for (int slot : FACE_UP_CARD_SLOTS) {
           Card newCard = newGameState.cardState().faceUpCard(slot);
           faceUpCards.get(slot).set(newCard);
       }
       
       for(PlayerId id : PlayerId.ALL) {
           int ord = id.ordinal();
           playerTicketsNum.get(ord).set(newGameState.playerState(id).ticketCount());
           playerTotalCardsNum.get(ord).set(newGameState.playerState(id).cardCount());
           playerWagonsNum.get(ord).set(newGameState.playerState(id).carCount());
           playerClaimPoints.get(ord).set(newGameState.playerState(id).claimPoints());
       }
       
       for(Route r : ChMap.routes()) {
           if(!(newGameState.claimedRoutes().contains(r))) {
               routeOwners.get(r).set(null);
           } else if(newPlayerState.routes().contains(r)) {
               routeOwners.get(r).set(id);
           } else {
               routeOwners.get(r).set(id.next());
           }
           
           // ================== Check This Part ==================
           
           
           if(newGameState.currentPlayerId() == id && newPlayerState.canClaimRoute(r) && !doubles.contains(r.stations())) {
               claimableRoutes.get(r).set(true);
           } else {
               claimableRoutes.get(r).set(false);
           }
       }
       
       for(Card c : Card.ALL) {
           playerCards.get(c.ordinal()).set(newPlayerState.cards().countOf(c));
       }
       
    }
    
    /**
     * Returns the field ticketPercentage.
     * @return (ReadOnlyIntegerProperty): the field ticketPercentage.
     */
    public ReadOnlyIntegerProperty ticketPercentage() {
        return ticketPercentage;
    }
    
    /**
     * Returns the field cardPercentage.
     * @return (ReadOnlyIntegerProperty): the field cardPercentage.
     */
    public ReadOnlyIntegerProperty cardPercentage() {
        return cardPercentage;
    }
    
    /**
     * Returns the ReadOnlyObjectProperty of the face-up card in the given slot.
     * @param slot (int): the face-up card to be chosen.
     * @return (ReadOnlyObjectProperty<Card>): the ReadOnlyObjectProperty of the
     * face-up card in the given slot.
     */
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }
    
    /**
     * Returns the ReadOnlyObjectProperty of the PlayerId who owns the route
     * specified in the parameter r.
     * @param r (Route): the route in question.
     * @return (ReadOnlyObjectProperty<PlayerId>): the ReadOnlyObjectProperty of
     * the PlayerId who owns the route specified in the parameter r.
     */
    public ReadOnlyObjectProperty<PlayerId> routeId(Route r){
        return routeOwners.get(r);
    }
    
    /**
     * Returns the ReadOnlyIntegerProperty containing the amount of tickets
     * owned by the player whose PlayerId is specified in the parameter.
     * @return (ReadOnlyIntegerProperty): the ReadOnlyIntegerProperty containing
     * the amount of tickets owned by the player whose PlayerId is specified in
     * the parameter.
     */
    public ReadOnlyIntegerProperty playerTickets(PlayerId id) {
        return playerTicketsNum.get(id.ordinal());
    }
    
    /**
     * Returns the ReadOnlyIntegerProperty containing the amount of cards
     * owned by the player whose PlayerId is specified in the parameter.
     * @return (ReadOnlyIntegerProperty): the ReadOnlyIntegerProperty containing
     * the amount of cards owned by the player whose PlayerId is specified in
     * the parameter.
     */
    public ReadOnlyIntegerProperty playerCards(PlayerId id) {
        return playerTotalCardsNum.get(id.ordinal());
    }
    
    /**
     * Returns the ReadOnlyIntegerProperty containing the amount of wagons
     * remaining for the player whose PlayerId is specified in the parameter.
     * @return (ReadOnlyIntegerProperty): the ReadOnlyIntegerProperty containing
     * the amount of wagons remaining for the player whose PlayerId is specified in
     * the parameter.
     */
    public ReadOnlyIntegerProperty playerWagons(PlayerId id) {
        return playerWagonsNum.get(id.ordinal());
    }
    
    /**
     * Returns the ReadOnlyIntegerProperty containing the amount of claim points
     * owned by the player whose PlayerId is specified in the parameter.
     * @return (ReadOnlyIntegerProperty): the ReadOnlyIntegerProperty containing
     * the amount of claim points owned by the player whose PlayerId is specified in
     * the parameter.
     */
    public ReadOnlyIntegerProperty playerClaimPoints(PlayerId id) {
        return playerClaimPoints.get(id.ordinal());
    }
    
    /**
     * Returns the field playerTickets
     * @return (ObservableList<Ticket>): the field playerTickets.
     */
    public ObservableList<Ticket> playerTickets(){
        return playerTickets;
    }
    
    /**
     * Returns the ReadOnlyIntegerProperty containing the amount of cards of a given type
     * owned by the player linked to this instance of ObservableGameState.
     * @return (ReadOnlyIntegerProperty): Returns the ReadOnlyIntegerProperty containing
     * the amount of cards of a given type owned by the player linked to this instance of
     * ObservableGameState.
     */
    public ReadOnlyIntegerProperty playerCards(Card c) {
        return playerCards.get(c.ordinal());
    }
    
    /**
     * Returns the ReadOnlyBooleanProperty containing the boolean whether it is 
     * possible for the player linked to this instance of ObservableGameState to
     * claim the given route.
     * @return (ReadOnlyBooleanProperty): the ReadOnlyBooleanProperty containing
     * the boolean whether it is possible for the player linked to this instance
     * of ObservableGameState to claim the given route.
     */
    public ReadOnlyBooleanProperty claimable(Route r) {
        return claimableRoutes.get(r);
    }
    
    /**
	 * Determines whether it is possible to draw tickets.
	 * @return (boolean): true if it is possible to draw tickets, false otherwise.
	 */
    public boolean canDrawTickets() {
        return ogs.canDrawTickets();
    }
    
    /**
	 * Determines whether it is possible to draw cards.
	 * @return (boolean): true if it is possible to draw cards, false otherwise.
	 */
    public boolean canDrawCards() {
        return ogs.canDrawCards();
    }
    
    /**
     * Returns a List of a SortedBag (itself a SortedBag of Cards) containing all the possible combinations of cards which the
     * player can use to claim the route.
     * @return (List<SortedBag<Card>>): a List of all the possible combinations of card combinations which the player can use to
     * claim the route.
     * @throws IllegalArgumentException if the player doesn't have enough wagons to claim the route.
     */
    public List<SortedBag<Card>> possibleClaimCards(Route r) {
        return ops.possibleClaimCards(r);
    }
    
    
}
