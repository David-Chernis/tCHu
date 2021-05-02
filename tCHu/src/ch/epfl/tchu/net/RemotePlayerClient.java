package ch.epfl.tchu.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static java.nio.charset.StandardCharsets.US_ASCII;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.Player.TurnKind;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * Represents the client device of the distant player.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class RemotePlayerClient {

	/**
	 * (Player): the player to which it must provide remote access.
	 */
    private Player player;
    
    /**
     * (String): the name of the socket used for communication between itself and the proxy.
     */
    private final String name;
    
    /**
     * (int): the port to be used.
     */
    private final int port;

    /**
     * Default RemotePlayerClient constructor. Initializes an instance by using a player, a
     * socket name and a port provided as a parameter.
     * @param player (Player): the player to which it must provide remote access.
     * @param name (String): the name of the socket used for communication between itself and the proxy.
     * @param port (int): the port to be used.
     */
    public RemotePlayerClient(Player player, String name, int port) {
        this.player = player;
        this.name = name;
        this.port = port;
    }

    /**
     * Method that sets up a socket and lets it communicate with the proxy.
     */
    public void run() {
        try(
                Socket s = new Socket(name, port);
                
                BufferedReader r =
                        new BufferedReader(
                                new InputStreamReader(s.getInputStream(), US_ASCII));
                
                BufferedWriter w =
                        new BufferedWriter(
                                new OutputStreamWriter(s.getOutputStream(), US_ASCII) )) {

            while(true) {
                String readLine = r.readLine();
                if(readLine == null) {
                    break;
                }
                List<String> messageList = Arrays.asList(readLine.split(Pattern.quote(" "), -1))   ;

                switch(MessageId.valueOf(messageList.get(0))) {

                case INIT_PLAYERS:
                    List<String> namesList = Serdes.stringListSerde.deserialize(messageList.get(2));
                    Map<PlayerId, String> namesMap = Map.of(PLAYER_1, namesList.get(0), PLAYER_2, namesList.get(1));
                    player.initPlayers(Serdes.playerIdSerde.deserialize(messageList.get(1)), namesMap);
                    break;

                case RECEIVE_INFO:
                    String info = Serdes.stringSerde.deserialize(messageList.get(1));
                    player.receiveInfo(info);
                    break;

                case UPDATE_STATE: 
                    PublicGameState gs = Serdes.publicGameStateSerde.deserialize(messageList.get(1));
                    PlayerState ps = Serdes.playerStateSerde.deserialize(messageList.get(2));
                    player.updateState(gs, ps);
                    break;

                case SET_INITIAL_TICKETS:
                    SortedBag<Ticket> tickets = Serdes.ticketBagSerde.deserialize(messageList.get(1));
                    player.setInitialTicketChoice(tickets);
                    break;

                case CHOOSE_INITIAL_TICKETS:
                    SortedBag<Ticket> initialTickets = player.chooseInitialTickets();
                    w.write(Serdes.ticketBagSerde.serialize(initialTickets) + "\n");
                    w.flush();
                    break;

                case NEXT_TURN:
                    TurnKind nextTurn = player.nextTurn();
                    w.write(Serdes.turnKindSerde.serialize(nextTurn)+ "\n");
                    w.flush();
                    break;

                case CHOOSE_TICKETS:
                    SortedBag<Ticket> options = Serdes.ticketBagSerde.deserialize(messageList.get(1));
                    SortedBag<Ticket> chosenTickets = player.chooseTickets(options);
                    w.write(Serdes.ticketBagSerde.serialize(chosenTickets)+ "\n");
                    w.flush();
                    break;

                case DRAW_SLOT: 
                    int drawSlot = player.drawSlot();
                    w.write(Serdes.intSerde.serialize(drawSlot)+ "\n");
                    w.flush();
                    break;

                case ROUTE: 
                    Route claimedRoute = player.claimedRoute();
                    w.write(Serdes.routeSerde.serialize(claimedRoute)+ "\n");
                    w.flush();
                    break;

                case CARDS: 
                    SortedBag<Card> cards = player.initialClaimCards();
                    w.write(Serdes.cardBagSerde.serialize(cards)+ "\n");
                    w.flush();
                    break;

                case CHOOSE_ADDITIONAL_CARDS:    
                    List<SortedBag<Card>>  additionalOptions = Serdes.cardListBagSerde.deserialize(messageList.get(1));
                    SortedBag<Card> chosenAdditionalCards = player.chooseAdditionalCards(additionalOptions);
                    w.write(Serdes.cardBagSerde.serialize(chosenAdditionalCards)+ "\n");
                    w.flush();
                    break;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }
}




