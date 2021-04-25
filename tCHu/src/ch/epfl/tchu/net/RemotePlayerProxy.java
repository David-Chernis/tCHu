package ch.epfl.tchu.net;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import static java.nio.charset.StandardCharsets.US_ASCII;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

public class RemotePlayerProxy implements Player{

	private Socket socket;
	
	public RemotePlayerProxy(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
		String toBeSent = MessageId.INIT_PLAYERS.name() + " " + Serdes.playerIdSerde.serialize(ownId)
				+ " " + Serdes.stringListSerde.serialize(List.of(playerNames.get(PlayerId.PLAYER_1),
						playerNames.get(PlayerId.PLAYER_2)));
		this.sendThroughSocket(toBeSent);
	}

	@Override
	public void receiveInfo(String info) {
		String toBeSent = MessageId.RECEIVE_INFO.name() + " " + Serdes.stringSerde.serialize(info);
		this.sendThroughSocket(toBeSent);
	}

	@Override
	public void updateState(PublicGameState newState, PlayerState ownState) {
		String toBeSent = MessageId.UPDATE_STATE.name() + " " + Serdes.publicGameStateSerde.serialize(newState) 
				+ " " + Serdes.playerStateSerde.serialize(ownState);
		this.sendThroughSocket(toBeSent);
	}

	@Override
	public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
		String toBeSent = MessageId.SET_INITIAL_TICKETS + " " + Serdes.ticketBagSerde.serialize(tickets);
		this.sendThroughSocket(toBeSent);
	}

	@Override
	public SortedBag<Ticket> chooseInitialTickets() {
		return Serdes.ticketBagSerde.deserialize(this.receiveThroughSocket());
	}

	@Override
	public TurnKind nextTurn() {
		return Serdes.turnKindSerde.deserialize(this.receiveThroughSocket());
	}

	@Override
	public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
		return null;
	}

	@Override
	public int drawSlot() {
		return Serdes.intSerde.deserialize(this.receiveThroughSocket());
	}

	@Override
	public Route claimedRoute() {
		return Serdes.routeSerde.deserialize(this.receiveThroughSocket());
	}

	@Override
	public SortedBag<Card> initialClaimCards() {
		return Serdes.cardBagSerde.deserialize(this.receiveThroughSocket());
	}

	@Override
	public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
		return null;
	}
	
	/**
	 * Returns the String that it receives from the socket connection.
	 * @return (String): the String that it received from the socket connection.
	 */
	private String receiveThroughSocket() {
		try(BufferedReader reader = 
				new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII))){
			return reader.readLine();
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Sends the given String through the socket connection to the remote player.
	 * @param toBeSent (String): the String to be sent.
	 */
	private void sendThroughSocket(String toBeSent) {
		try(BufferedWriter writer =
				   new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII))){
			writer.write(toBeSent + "\n");
			writer.flush();
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
