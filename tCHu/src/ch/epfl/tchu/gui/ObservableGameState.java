package ch.epfl.tchu.gui;

import java.util.List;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import javafx.beans.property.*;

public class ObservableGameState {
    private  PublicGameState ogs;
    private PlayerState ops;
    // Group 1
    private final ReadOnlyIntegerProperty ticketPercentage; 
    private final ReadOnlyIntegerProperty cardPercentage;
    private final List<ReadOnlyObjectProperty<Card>> faceUpCards;
    private final List<ReadOnlyObjectProperty<PlayerId>> routes;
    
    // Group 2
    private final ReadOnlyIntegerProperty ticketsP1;
    private final ReadOnlyIntegerProperty ticketsP2;
    private final ReadOnlyIntegerProperty cardsP1;
    private final ReadOnlyIntegerProperty cardsP2;
    private final ReadOnlyIntegerProperty wagonsP1;
    private final ReadOnlyIntegerProperty wagonsP2;
    
    
    
    
    ObservableGameState(PlayerId id){
        
    }
    
    public void setState(PublicGameState ogs, PlayerState ops) {
       this.ogs = ogs;
       this.ops = ops;
    }
    
    
}
