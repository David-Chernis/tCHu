package ch.epfl.tchu.gui;

import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.GameState;
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
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

public class GraphicalPlayer {
    private ObservableGameState ogs;
    private ObservableList <Text> infos = FXCollections.observableArrayList();
    private ObjectProperty<DrawTicketsHandler> dthProperty;
    private ObjectProperty<DrawCardHandler> dchProperty;
    private ObjectProperty<ClaimRouteHandler> crhProperty;
    private Stage mainStage;
    private final Node mapView;
    
    
    public GraphicalPlayer(PlayerId id,  Map<PlayerId, String> playerNames) {
        ogs = new ObservableGameState(id);
        dthProperty = new SimpleObjectProperty<>();
        dchProperty = new SimpleObjectProperty<>();
        crhProperty = new SimpleObjectProperty<>();
        
        mapView = MapViewCreator.createMapView(ogs, crhProperty, this::chooseClaimCards);
        Node cardsView = DecksViewCreator.createCardsView(ogs, dthProperty, dchProperty);
        Node handView = DecksViewCreator.createHandView(ogs);
        Node infoView = InfoViewCreator.createInfoView(id, playerNames, ogs,infos);
        
        mainStage = new Stage();
        BorderPane mainPane = new BorderPane(mapView , null , cardsView, handView,  infoView);
        Scene mainScene = new Scene(mainPane);
        mainStage.setScene(mainScene);
        mainStage.setTitle("tCHu — " + playerNames.get(id));
        mainStage.show();
    }

    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {   
        assert Platform.isFxApplicationThread();
        ogs.setState(newGameState, newPlayerState);     
    }
    
    public void receiveInfo(String message) {
        assert Platform.isFxApplicationThread();
        System.out.println(message);
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
    
    public void chooseTickets(SortedBag<Ticket> ticketChoices, ChooseTicketsHandler tch) {
        assert Platform.isFxApplicationThread();
        Preconditions.checkArgument(ticketChoices.size() == 5 || ticketChoices.size() == 3);
        boolean multiple = ticketChoices.size() == 5;
        Stage ticketChooser = createChooser(StringsFr.TICKETS_CHOICE, String.format(StringsFr.CHOOSE_TICKETS, multiple ? "5" : "3" , multiple ? "s" : "" ), ticketChoices.size()-2, true);
        Button ticketButton = (Button) ticketChooser.getScene().rootProperty().get().getChildrenUnmodifiable().get(2);
        ListView<Ticket> list = (ListView<Ticket>) ticketChooser.getScene().rootProperty().get().getChildrenUnmodifiable().get(1);
        
        ticketButton.setOnAction((e) -> {
            tch.onChooseTickets(SortedBag.of(list.getSelectionModel().getSelectedItems()));
            ticketChooser.hide();
        });
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
        Stage claimCard = createChooser(StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_CARDS, 1, false);
        Button claimCardButton = (Button) claimCard.getScene().rootProperty().get().getChildrenUnmodifiable().get(2);
        ListView<SortedBag<Card>> list = (ListView<SortedBag<Card>>) claimCard.getScene().rootProperty().get().getChildrenUnmodifiable().get(1);
        
        //Creation of Button Handler
        claimCardButton.setOnAction((e) -> {
            
            cch.onChooseCards(list.getSelectionModel().getSelectedItem());
            claimCard.hide();
        });
    } 
    public void chooseAdditionalCards(List<SortedBag<Card>> claimCards, ChooseCardsHandler cch) {   
        assert Platform.isFxApplicationThread();
        Preconditions.checkArgument(claimCards.size() > 0);
        Stage additionalCard = createChooser(StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_ADDITIONAL_CARDS, 1, false);
        Button additionalCardButton = (Button) additionalCard.getScene().rootProperty().get().getChildrenUnmodifiable().get(2);
        ListView<SortedBag<Card>> list = (ListView<SortedBag<Card>>) additionalCard.getScene().rootProperty().get().getChildrenUnmodifiable().get(1);
        
        //Creation of Button Handler
        additionalCardButton.setOnAction((e) -> {
            
            cch.onChooseCards(list.getSelectionModel().getSelectedItem());
            additionalCard.hide();
        });
    }
    
    private void disableHandlers() {
        dthProperty.set(null);
        dchProperty.set(null);
        crhProperty.set(null);
    }
    
    private Stage createChooser(String titleText, String introText, int minimum, boolean multiple) {
        Text text = new Text(introText);
        
        TextFlow texts = new TextFlow(text);
        Stage chooserStage;
        
        if(multiple) {
            ListView<Ticket> list = new ListView<>();
            list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            Button chooserButton = new Button();
            chooserButton.disableProperty().bind(Bindings.size(list.getSelectionModel().getSelectedItems()).lessThan(minimum));
            
            VBox options = new VBox(texts, list, chooserButton);
            
            Scene chooser = new Scene(options);
            chooser.getStylesheets().add("chooser.css");
            
            chooserStage = new Stage(StageStyle.UTILITY);
            chooserStage.setScene(chooser);
            chooserStage.initOwner(mainStage);
            chooserStage.initModality(Modality.WINDOW_MODAL);
            
            chooserStage.setOnCloseRequest((e) -> {e.consume();});
            chooserStage.setTitle(titleText);
        }
        
        else {
            ListView<SortedBag<Card>> list = new ListView<>();
            list.setCellFactory(v ->  new TextFieldListCell<>(new CardBagStringConverter()));
            list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            Button chooserButton = new Button();
            chooserButton.disableProperty().bind(Bindings.size(list.getSelectionModel().getSelectedItems()).lessThan(minimum));
            
            VBox options = new VBox(texts, list, chooserButton);
            
            Scene chooser = new Scene(options);
            chooser.getStylesheets().add("chooser.css");
            
            chooserStage = new Stage(StageStyle.UTILITY);
            chooserStage.setScene(chooser);
            chooserStage.initOwner(mainStage);
            chooserStage.initModality(Modality.WINDOW_MODAL);
            
            chooserStage.setOnCloseRequest((e) -> {e.consume();});
            chooserStage.setTitle(titleText);
        }
        
        return chooserStage;
    }

    public class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

        @Override
        public String toString(SortedBag<Card> cards) {
            int size = cards.size();
            String temp = "";
            for(int i = 0; i < size -1 ; i++) {
                temp += cards.countOf(cards.get(i)) + " " + cards.get(i).name().toLowerCase() + ", ";
            }
            
            return temp + " et " + cards.countOf(cards.get(size-1)) + cards.get(size-1).name().toLowerCase();
        }

        @Override
        public SortedBag<Card> fromString(String string) {
            throw new UnsupportedOperationException();
        }
            
    };
}
