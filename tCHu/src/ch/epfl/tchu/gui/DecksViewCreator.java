package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Ticket;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

class DecksViewCreator {
	
	private DecksViewCreator() {};
	
	public static void createHandView(ObservableGameState ogs) {
		ListView<Ticket> ticketsInHand = new ListView<Ticket>(ogs.tickets());
		
		StackPane blackCardHand = new StackPane();
		blackCardHand.getStyleClass().add("BLACK");
		Rectangle outsideBlack = new Rectangle(insideBlack);
		outsideBlack.getStyleClass().add("outside");
		Rectangle insideBlack = new Rectangle();
		insideBlack.getStyleClass().add("filled");
		insideBlack.getStyleClass().add("inside");
		Rectangle trainImageBlack = new Rectangle();
		
		HBox handPane = new HBox();
		handPane.setId("hand-pane");
		
		HBox handView = new HBox(ticketsInHand, handPane);
		handView.getStylesheets().add("decks.css");
		handView.getStylesheets().add("colors.css");
	}
}
