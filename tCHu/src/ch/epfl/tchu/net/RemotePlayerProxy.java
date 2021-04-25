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

public final class RemotePlayerProxy implements Player{

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
		String toBeSent = MessageId.SET_INITIAL_TICKETS.name() + " " + Serdes.ticketBagSerde.serialize(tickets);
		this.sendThroughSocket(toBeSent);
	}

	@Override
	public SortedBag<Ticket> chooseInitialTickets() {
	    String toBeSent = MessageId.CHOOSE_INITIAL_TICKETS.name();
	    this.sendThroughSocket(toBeSent);
		return Serdes.ticketBagSerde.deserialize(this.receiveThroughSocket());
	}

	@Override
	public TurnKind nextTurn() {
	    String toBeSent = MessageId.NEXT_TURN.name();
        this.sendThroughSocket(toBeSent);
		return Serdes.turnKindSerde.deserialize(this.receiveThroughSocket());
	}

	@Override
	public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
	    String toBeSent = MessageId.CHOOSE_TICKETS.name() + " " + Serdes.ticketBagSerde.serialize(options);
	    this.sendThroughSocket(toBeSent);
		return Serdes.ticketBagSerde.deserialize(this.receiveThroughSocket());
	}

	@Override
	public int drawSlot() {
	    String toBeSent = MessageId.DRAW_SLOT.name();
        this.sendThroughSocket(toBeSent);
		return Serdes.intSerde.deserialize(this.receiveThroughSocket());
	}

	@Override
	public Route claimedRoute() {
	    String toBeSent = MessageId.ROUTE.name();
        this.sendThroughSocket(toBeSent);
		return Serdes.routeSerde.deserialize(this.receiveThroughSocket());
	}

	@Override
	public SortedBag<Card> initialClaimCards() {
	    String toBeSent = MessageId.CARDS.name();
        this.sendThroughSocket(toBeSent);
		return Serdes.cardBagSerde.deserialize(this.receiveThroughSocket());
	}

	@Override
	public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
		String toBeSent = MessageId.CHOOSE_ADDITIONAL_CARDS.name() + " " + Serdes.cardListBagSerde.serialize(options);
		this.sendThroughSocket(toBeSent);
		return Serdes.cardBagSerde.deserialize(this.receiveThroughSocket());
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
			writer.write(toBeSent + " " + "\n");
			writer.flush();
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
