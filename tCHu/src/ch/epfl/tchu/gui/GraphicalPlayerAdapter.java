package ch.epfl.tchu.gui;

import java.util.List;
import java.util.Map;
import static javafx.application.Platform.runLater;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;
import javafx.application.Platform;

public class GraphicalPlayerAdapter implements Player{
	
	private GraphicalPlayer graphicalPlayer;
	
	public GraphicalPlayerAdapter() {
		
	}

	@Override
	public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
		assert Platform.isFxApplicationThread();
		graphicalPlayer = new GraphicalPlayer(ownId, playerNames);
	}

	@Override
	public void receiveInfo(String info) {
		runLater(() -> graphicalPlayer.receiveInfo(info));
	}

	@Override
	public void updateState(PublicGameState newState, PlayerState ownState) {
		runLater(() -> graphicalPlayer.setState(newState, ownState));
	}

	@Override
	public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
		runLater(() -> graphicalPlayer.setState(tickets, ));
	}

	@Override
	public SortedBag<Ticket> chooseInitialTickets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TurnKind nextTurn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
		runLater(() -> graphicalPlayer.chooseTickets(options, );
	}

	@Override
	public int drawSlot() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Route claimedRoute() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortedBag<Card> initialClaimCards() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
