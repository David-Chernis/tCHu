package ch.epfl.tchu.gui;

import java.util.Map;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Class that allows for the creation of the graphical elements needed to display the infos of the game.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
class InfoViewCreator {
	
	/**
	 * Private InfoViewCreator constructor, whose sole aim is to make this class uninstanciable.
	 */
	private InfoViewCreator() {};
	
	/**
	 * Returns a graphical view of the infos of the players.
	 * @param ogs (ObservableGameState): the observable game state of the game.
	 * @return (VBox): a graphical view of the infos of the players.
	 */
	public static VBox createInfoView(ObservableGameState ogs, PlayerId id, Map<PlayerId, String> playerNames, ObservableList<Text> infos) {
		
		//The circles that represents the color of each player
		Circle cBlue = new Circle(5);
		cBlue.getStyleClass().add("filled");
		Circle cRed = new Circle(5);
		cRed.getStyleClass().add("filled");
		
		//The text part of the player statistics
		Text textBlue = new Text();
		Text textRed = new Text();
		StringExpression sBlue = Bindings.format(StringsFr.PLAYER_STATS, playerNames.get(id), ogs.playerTickets(id),
				ogs.playerCards(id), ogs.playerWagons(id), ogs.playerClaimPoints(id));
		StringExpression sRed = Bindings.format(StringsFr.PLAYER_STATS, playerNames.get(id.next()), ogs.playerTickets(id.next()),
				ogs.playerCards(id.next()), ogs.playerWagons(id.next()), ogs.playerClaimPoints(id.next()));
		textBlue.textProperty().bind(sBlue);
		textRed.textProperty().bind(sRed);
		
		//The TextFlow of each player
		TextFlow textFlowBlue = new TextFlow(cBlue, textBlue);
		textFlowBlue.getStyleClass().add(id == PlayerId.PLAYER_1 ? "PLAYER_1" : "PLAYER_2");
		TextFlow textFlowRed = new TextFlow(cRed, textRed);
		textFlowRed.getStyleClass().add(id.next() == PlayerId.PLAYER_1 ? "PLAYER_1" : "PLAYER_2");
		
		//The VBox containing the player statistics
		VBox playerStats = new VBox(textFlowBlue, textFlowRed);
		playerStats.setId("player-stats");
		
		//The separator that separates the player statistics and the last 5 game infos
		Separator separator = new Separator();
		separator.orientationProperty().set(Orientation.HORIZONTAL);
		
		//The last 5 game infos
		
		//The TextFlow for the last 5 game infos
		TextFlow messages = new TextFlow();
		Bindings.bindContent(messages.getChildren(), infos);
		messages.setId("game-info");
		
		//The final Info View VBox, containing all the elements described above
		VBox infoView = new VBox(playerStats, separator, messages);
		infoView.getStylesheets().addAll("info.css", "colors.css");
		
		return infoView;
	}
	
}
