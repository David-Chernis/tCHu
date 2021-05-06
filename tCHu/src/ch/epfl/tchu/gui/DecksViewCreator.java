package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Ticket;
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
		
		//The cards part of the player's hand
		StackPane blackCards = carteCompteur("BLACK");
		StackPane violetCards = carteCompteur("VIOLET");
		StackPane blueCards = carteCompteur("BLUE");
		StackPane greenCards = carteCompteur("GREEN");
		StackPane yellowCards = carteCompteur("YELLOW");
		StackPane orangeCards = carteCompteur("ORANGE");
		StackPane redCards = carteCompteur("RED");
		StackPane whiteCards = carteCompteur("WHITE");
		StackPane locomotiveCards = carteCompteur("NEUTRAL");
		
		HBox handPane = new HBox(blackCards, violetCards, blueCards, greenCards, yellowCards,
				orangeCards, redCards, whiteCards, locomotiveCards);
		handPane.setId("hand-pane");

		//The hand of the player, containing all of their cards and tickets
		HBox handView = new HBox(billets, handPane);
		handView.getStylesheets().add("decks.css");
		handView.getStylesheets().add("colors.css");
		
		return handView;
	}
	
	/**
	 * Returns a graphical view of the cards and tickets of the game.
	 * @param ogs (ObservableGameState): the observable game state of the game.
	 * @param tirageBillets (EventHandler<>): the event handler for drawing tickets.
	 * @param tirageCartes(EventHandler<>): the event handler for drawing cards.
	 * @return (VBox): a graphical view of the cards and tickets of the game.
	 */
	public static VBox createCardsView(ObservableGameState ogs, EventHandler<> tirageBillets, EventHandler<> tirageCartes) {
		
		//The deck of cards and tickets
		Button piocheBillets = pioche();
		Button piocheCartes = pioche();
		
		//The 5 face-up cards
		StackPane faceUp1 = carteOnly("RED");
		StackPane faceUp2 = carteOnly("RED");
		StackPane faceUp3 = carteOnly("RED");
		StackPane faceUp4 = carteOnly("RED");
		StackPane faceUp5 = carteOnly("RED");
		
		/*
		The cards and tickets view of the game, containing all the face-up cards,
		the deck of cards and the deck of tickets
		*/
		VBox cardView = new VBox(piocheBillets, faceUp1, faceUp2,
				faceUp3, faceUp4, faceUp5, piocheCartes);
		cardView.getStylesheets().add("decks.css");
		cardView.getStylesheets().add("colors.css");
		cardView.setId("card-pane");
		
		return cardView;
	}
	
	/**
	 * Returns a StackPane that resembles a card of the specified color with a number on top
	 * if there are multiple such cards in the player's hand.
	 * @param color (String): the color of the card.
	 * @return (StackPane): a StackPane that resembles a card of the specified color with a
	 * number on top if there are multiple such cards in the player's hand.
	 */
	private static StackPane carteCompteur(String color) {
		Rectangle carteTrainImage = new Rectangle(60, 90);
		carteTrainImage.getStyleClass().add("train-image");
		Rectangle carteInside = new Rectangle(40, 70);
		carteInside.getStyleClass().add("filled");
		carteInside.getStyleClass().add("inside");
		Rectangle carteOutside = new Rectangle(40, 70);
		carteOutside.getStyleClass().add("outside");
		Text compteur = new Text();
		compteur.getStyleClass().add("count");
		StackPane carteCompteur = new StackPane(carteOutside, carteInside, carteTrainImage, compteur);
		carteCompteur.getStyleClass().add(color);
		
		return carteCompteur;
	}
	
	/**
	 * Returns a StackPane that resembles a card of the specified color.
	 * @param color (String): the color of the card.
	 * @return (StackPane): a StackPane that resembles a card of the specified color.
	 */
	private static StackPane carteOnly(String color) {
		Rectangle carteTrainImage = new Rectangle(60, 90);
		carteTrainImage.getStyleClass().add("train-image");
		Rectangle carteInside = new Rectangle(40, 70);
		carteInside.getStyleClass().add("filled");
		carteInside.getStyleClass().add("inside");
		Rectangle carteOutside = new Rectangle(40, 70);
		carteOutside.getStyleClass().add("outside");
		StackPane carteCompteur = new StackPane(carteOutside, carteInside, carteTrainImage);
		carteCompteur.getStyleClass().add(color);
		
		return carteCompteur;
	}
	
	/**
	 * Returns a Button which will act as a deck and has a gauge attached to it, which
	 * corresponds to the amount of cards/tickets remaining.
	 * @return (Button): a Button which will act as a deck and has a gauge attached to
	 * it, which corresponds to the amount of cards/tickets remaining.
	 */
	private static Button pioche() {
		Rectangle background = new Rectangle();
		background.getStyleClass().add("background");
		Rectangle foreground = new Rectangle();
		foreground.getStyleClass().add("foreground");
		Group grouped = new Group(background, foreground);
		Button pioche = new Button();
		pioche.setGraphic(grouped);
		
		return pioche;
	}
}
