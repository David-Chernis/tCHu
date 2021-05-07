package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Class allows for the creation of the graphical elements needed to display the card and ticket
 * view as well as the view of the player's hand of cards and tickets.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
class DecksViewCreator {
	
	/**
	 * Private DecksViewCreator constructor, whose sole aim is to make this class uninstanciable.
	 */
	private DecksViewCreator() {};
	
	/**
	 * Returns a graphical view of the hand of the player.
	 * @param ogs (ObservableGameState): the observable game state of the game.
	 * @return (HBox): a graphical view of the hand of the player.
	 */
	public static HBox createHandView(ObservableGameState ogs) {
		//The List of the tickets in the player's hand
		ListView<Ticket> billets = new ListView<Ticket>(ogs.playerTickets());
		billets.setId("tickets");
		
		HBox handPane = new HBox();
        handPane.setId("hand-pane");
		//The cards part of the player's hand
		for(Card c : Card.ALL) {
		    StackPane colorCards = cardCompteur(c, c.name(), ogs);
		    handPane.getChildren().add(colorCards);
		}
		
		//The hand of the player, containing all of their cards and tickets
		HBox handView = new HBox(billets, handPane);
		handView.getStylesheets().add("decks.css");
		handView.getStylesheets().add("colors.css");
		
		return handView;
	}
	
	/**
	 * Returns a graphical view of the cards and tickets of the game.
	 * @param ogs (ObservableGameState): the observable game state of the game.
	 * @param drawTickets (ObjectProperty<DrawTicketsHandler>): the object property for drawing tickets.
	 * @param drawCard (EventHandler<DrawCardHandler>): the object property for drawing cards.
	 * @return (VBox): a graphical view of the cards and tickets of the game.
	 */
	public static VBox createCardsView(ObservableGameState ogs, ObjectProperty<DrawTicketsHandler> drawTickets, ObjectProperty<DrawCardHandler> drawCard) {
		
		//The deck of cards and tickets
		Button deckTickets = deck(ogs.ticketPercentage());
		Button deckCards = deck(ogs.cardPercentage());
		
		deckTickets.disableProperty().bind(drawTickets.isNull());
	    deckCards.disableProperty().bind(drawCard.isNull());
	    
	    deckTickets.setOnMouseClicked((e) -> drawTickets.get().onDrawTickets());
	    deckCards.setOnMouseClicked((e) -> drawCard.get().onDrawCard(-1));
		/*
        The cards and tickets view of the game, containing all the face-up cards,
        the deck of cards and the deck of tickets
        */
		VBox cardView = new VBox(deckTickets, deckCards);
        cardView.getStylesheets().add("decks.css");
        cardView.getStylesheets().add("colors.css");
        cardView.setId("card-pane");
        
        //The 5 face-up cards
		for(int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++) {
		    Card fuCard = ogs.faceUpCard(i).get();
		    StackPane faceUp = cardOnly("Red");
		    cardView.getChildren().add(faceUp);

            int slot = i;
		    faceUp.setOnMouseClicked((e) -> drawCard.get().onDrawCard(slot));
		    ogs.faceUpCard(i).addListener((o, oV, nV) -> {
		        faceUp.getStyleClass().remove(oV.name());
		        faceUp.getStyleClass().add(nV.name()); 
		    });
		}
		
		return cardView;
	}
	
	/**
	 * Returns a StackPane that resembles a card of the specified color with a number on top
	 * if there are multiple such cards in the player's hand.
	 * @param color (String): the color of the card.
	 * @return (StackPane): a StackPane that resembles a card of the specified color with a
	 * number on top if there are multiple such cards in the player's hand.
	 */
	private static StackPane cardCompteur(Card card, String color, ObservableGameState ogs) {
		Text compteur = new Text();
		compteur.getStyleClass().add("count");
		StackPane carteCompteur = cardOnly(color);
		carteCompteur.getChildren().add(compteur);
		
		ReadOnlyIntegerProperty count = ogs.playerCards(card);
		carteCompteur.visibleProperty().bind(Bindings.greaterThan(count, 0));
        compteur.visibleProperty().bind(Bindings.greaterThan(count, 1));
        compteur.textProperty().bind(Bindings.convert(count));
        
		return carteCompteur;
	}
	
	/**
	 * Returns a StackPane that resembles a card of the specified color.
	 * @param color (String): the color of the card.
	 * @return (StackPane): a StackPane that resembles a card of the specified color.
	 */
	private static StackPane cardOnly(String color) {
		Rectangle carteTrainImage = new Rectangle(40, 70);
		carteTrainImage.getStyleClass().add("train-image");
		Rectangle carteInside = new Rectangle(40, 70);
		carteInside.getStyleClass().add("filled");
		carteInside.getStyleClass().add("inside");
		Rectangle carteOutside = new Rectangle(60, 90);
		carteOutside.getStyleClass().add("outside");
		
		StackPane carteCompteur = new StackPane(carteOutside, carteInside, carteTrainImage);
		carteCompteur.getStyleClass().add(color);
		carteCompteur.getStyleClass().add("card");
		
		return carteCompteur;
	}
	
	/**
	 * Returns a Button which will act as a deck and has a gauge attached to it, which
	 * corresponds to the amount of cards/tickets remaining.
	 * @return (Button): a Button which will act as a deck and has a gauge attached to
	 * it, which corresponds to the amount of cards/tickets remaining.
	 */
	private static Button deck(ReadOnlyIntegerProperty pctProp) {
		Rectangle background = new Rectangle(50, 5);
		background.getStyleClass().add("background");
		Rectangle foreground = new Rectangle(50, 5);
		foreground.getStyleClass().add("foreground");
		Group grouped = new Group(background, foreground);
		Button pioche = new Button();
		pioche.getStyleClass().add("gauged");
		pioche.setGraphic(grouped);
		
		foreground.widthProperty().bind(pctProp.multiply(50).divide(100));
		return pioche;
	}
}
