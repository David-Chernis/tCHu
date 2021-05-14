package ch.epfl.tchu.gui;

import java.util.List;
import java.util.Map;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ChooseTicketsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;

public class GraphicalPlayer {
    ObservableGameState ogs;
    ObservableList <Text> infos;

    public GraphicalPlayer(PlayerId id,  Map<PlayerId, String> playerNames) {

    }

    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {         
        ogs.setState(newGameState, newPlayerState);     
    }
    
    public void receiveInfo(String message) {
        infos.add(new Text(message));
        if(infos.size() > 5) {
            infos.remove(0);
        }
    }
    
    public void startTurn(DrawTicketsHandler dth, DrawCardHandler dch, ClaimRouteHandler crh) {
        
    }
    
    public void chooseTickets(SortedBag<Ticket> ticketChoices, ChooseTicketsHandler ticketChoiceHandler) {
        Preconditions.checkArgument(ticketChoices.size() == 5 || ticketChoices.size() == 3);
        
    }
    
    public void drawCard(DrawCardHandler dch) {
        
    }
    
    public void chooseClaimCards(List<SortedBag<Card>> claimCards, ChooseCardsHandler cch) { 
        
    } 
    public void chooseAdditionalCards(List<SortedBag<Card>> claimCards, ChooseCardsHandler cch) {    
        
    }

}
