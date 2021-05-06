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

public class ObservableGameState {
    private  PublicGameState ogs;
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
    
    private List<IntegerProperty> createIntList(int n){
        List<IntegerProperty> newList = new ArrayList<>();
        for(int i = 0; i < n; i++) {
            newList.add(new SimpleIntegerProperty());
        }
        return newList;
    } 
    
    private List<ObjectProperty<Card>> createFaceUpCards(){
        List<ObjectProperty<Card>> newList = new ArrayList<>();
        for (@SuppressWarnings("unused") int slot : FACE_UP_CARD_SLOTS) {
            newList.add(new SimpleObjectProperty<Card>());
        }
        return newList;
    }
    
    private Map<Route, ObjectProperty<PlayerId>> createRoutes(){
        Map<Route, ObjectProperty<PlayerId>> newRoutes = new HashMap<>();
        for(Route r : ChMap.routes()) {
            newRoutes.put(r, new SimpleObjectProperty<>());
        }
        return newRoutes;
    }
    
    private Map<Route, BooleanProperty> createOwnedRoutes(){
        Map<Route, BooleanProperty> newRoutes = new HashMap<>();
        for(Route r : ChMap.routes()) {
            newRoutes.put(r, new SimpleBooleanProperty());
        }
        return newRoutes;
    }
    
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
    // Property Getter Methods
    public ReadOnlyIntegerProperty ticketPercentage() {
        return ticketPercentage;
    }
    public ReadOnlyIntegerProperty cardPercentage() {
        return cardPercentage;
    }    
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }
    public ReadOnlyObjectProperty<PlayerId> routeId(Route r){
        return routeOwners.get(r);
    }
    public ReadOnlyIntegerProperty playerTickets(PlayerId id) {
        return playerTicketsNum.get(id.ordinal());
    }
    public ReadOnlyIntegerProperty playerCards(PlayerId id) {
        return playerTotalCardsNum.get(id.ordinal());
    }
    public ReadOnlyIntegerProperty playerWagons(PlayerId id) {
        return playerWagonsNum.get(id.ordinal());
    }
    public ReadOnlyIntegerProperty playerClaimPoints(PlayerId id) {
        return playerClaimPoints.get(id.ordinal());
    }
    public ObservableList<Ticket> playerTickets(){
        return playerTickets;
    }
    public ReadOnlyIntegerProperty playerCards(Card c) {
        return playerCards.get(c.ordinal());
    }
    public ReadOnlyBooleanProperty claimable(Route r) {
        return claimableRoutes.get(r);
    }
    
    // GameState and playerState access methods.
    public boolean canDrawTickets() {
        return ogs.canDrawTickets();
    }
    public boolean canDrawCards() {
        return ogs.canDrawCards();
    }
    public List<SortedBag<Card>> possibleClaimCards(Route r) {
        return ops.possibleClaimCards(r);
    }
    
    
}
