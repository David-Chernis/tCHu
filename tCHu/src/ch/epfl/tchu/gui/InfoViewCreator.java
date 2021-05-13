package ch.epfl.tchu.gui;

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
public class InfoViewCreator {
	
	/**
	 * Private InfoViewCreator constructor, whose sole aim is to make this class uninstanciable.
	 */
	private InfoViewCreator() {};
	
	/**
	 * Returns a graphical view of the infos of the players.
	 * @param ogs (ObservableGameState): the observable game state of the game.
	 * @return (VBox): a graphical view of the infos of the players.
	 */
	public static VBox createInfoView(ObservableGameState ogs) {
		
		//The circles that represents the color of each player
		Circle cBlue = new Circle(5);
		cBlue.getStyleClass().add("filled");
		Circle cRed = new Circle(5);
		cRed.getStyleClass().add("filled");
		
		//The text part of the player statistics
		Text textBlue = new Text(String.format(StringsFr.PLAYER_STATS, playername, playertickets, playercards, playerwagons, playerpoints));
		Text textRed = new Text(String.format(StringsFr.PLAYER_STATS, playername, playertickets, playercards, playerwagons, playerpoints));
		
		//The TextFlow of each player
		TextFlow textFlowBlue = new TextFlow(cBlue, textBlue);
		textFlowBlue.getStyleClass().add("PLAYER_n");
		TextFlow textFlowRed = new TextFlow(cRed, textRed);
		textFlowRed.getStyleClass().add("PLAYER_n");
		
		//The VBox containing the player statistics
		VBox playerStats = new VBox(textFlowBlue, textFlowRed);
		playerStats.setId("player-stats");
		
		//The separator that separates the player statistics and the last 5 game infos
		Separator separator = new Separator();
		
		//The last 5 game infos
		Text textLastGI = new Text();
		Text textSecondLastGI = new Text();
		Text textThirdLastGI = new Text();
		Text textFourthLastGI = new Text();
		Text textFifthLastGI = new Text();
		
		//The TextFlow for the last 5 game infos
		TextFlow lastFiveGameInfos = new TextFlow(textLastGI, textSecondLastGI, textThirdLastGI,
														textFourthLastGI, textFifthLastGI);
		lastFiveGameInfos.setId("game-info");
		
		//The final Info View VBox, containing all the elements described above
		VBox infoView = new VBox(playerStats, separator, lastFiveGameInfos);
		infoView.getStylesheets().addAll("info.css", "colors.css");
		
		return infoView;
	}
	
}
