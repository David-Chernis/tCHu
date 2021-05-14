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
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GraphicalPlayer {
    ObservableGameState ogs;
    ObservableList <Text> infos;
    ObjectProperty<DrawTicketsHandler> dthProperty;
    ObjectProperty<DrawCardHandler> dchProperty;
    ObjectProperty<ClaimRouteHandler> crhProperty;
    Stage mainStage;

    public GraphicalPlayer(PlayerId id,  Map<PlayerId, String> playerNames) {
        
        Node mapView = MapViewCreator.createMapView(ogs, crhProperty, null);
        Node cardsView = DecksViewCreator.createCardsView(ogs, dthProperty, dchProperty);
        Node handView = DecksViewCreator.createHandView(ogs);
        Node infoView = InfoViewCreator.createInfoView(ogs, id, playerNames, infos);
     
        Stage playerStage = new Stage();
        BorderPane mainPane = new BorderPane(mapView , null , cardsView, handView,  infoView);
        Scene mainScene = new Scene(mainPane);
        playerStage.setScene(mainScene);
        playerStage.setTitle("tCHu — " + playerNames.get(id));
        this.mainStage = playerStage;
    }

    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {   
        assert Platform.isFxApplicationThread();
        ogs.setState(newGameState, newPlayerState);     
    }
    
    public void receiveInfo(String message) {
        assert Platform.isFxApplicationThread();
        infos.add(new Text(message));
        if(infos.size() > 5) {
            infos.remove(0);
        }
    }
    
    public void startTurn(DrawTicketsHandler dth, DrawCardHandler dch, ClaimRouteHandler crh) {
        assert Platform.isFxApplicationThread();
        
        dthProperty.set(!ogs.canDrawTickets() ? 
                null 
                : () -> {
                    disableHandlers();
                    dth.onDrawTickets();
                });
        
        dchProperty.set(!ogs.canDrawCards() 
                ? null 
                : (int i) -> {
                    disableHandlers();
                    dch.onDrawCard(i);
                    drawCard(dch);
                });
        
        crhProperty.set(
                    (route, claimCards) -> {
                        disableHandlers();
                        crh.onClaimRoute(route, claimCards);
                    }
                );
    }
    
    public void chooseTickets(SortedBag<Ticket> ticketChoices, ChooseTicketsHandler ticketChoiceHandler) {
        assert Platform.isFxApplicationThread();
        Preconditions.checkArgument(ticketChoices.size() == 5 || ticketChoices.size() == 3);
        boolean multiple = ticketChoices.size() == 5;
        Stage ticketChooserStage = createChooser(String.format(StringsFr.CHOOSE_TICKETS, multiple ? "5" : "3" , multiple ? "s" : "" ));
        ticketChooserStage.setTitle(StringsFr.TICKETS_CHOICE);
        for(Ticket ticket : ticketChoices) {
            
        }
        
    }
    
    public void drawCard(DrawCardHandler dch) {
        assert Platform.isFxApplicationThread();
        dchProperty.set((int i ) -> {
            disableHandlers();
            dch.onDrawCard(i);
        });
    }
    
    public void chooseClaimCards(List<SortedBag<Card>> claimCards, ChooseCardsHandler cch) { 
        assert Platform.isFxApplicationThread();
        
    } 
    public void chooseAdditionalCards(List<SortedBag<Card>> claimCards, ChooseCardsHandler cch) {   
        assert Platform.isFxApplicationThread();
        
    }
    
    private void disableHandlers() {
        dthProperty.set(null);
        dchProperty.set(null);
        crhProperty.set(null);
    }
    
    private Stage createChooser(String introText) {
        Text text = new Text(introText);
        
        TextFlow texts = new TextFlow(text);
        ListView list = new ListView();
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Button chooseButton = new Button();
        
        VBox options = new VBox(texts, list, chooseButton);
        
        Scene chooser = new Scene(options);
        chooser.getStylesheets().add("chooser.css");
        
        Stage chooserStage = new Stage(StageStyle.UTILITY);
        chooserStage.setScene(chooser);
        chooserStage.initOwner(mainStage);
        chooserStage.initModality(Modality.WINDOW_MODAL);
        return chooserStage;
        
    }

}
