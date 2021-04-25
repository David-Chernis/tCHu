package ch.epfl.tchu.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
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

public final class RemotePlayerClient {

    private Player player;
    private final String name;
    private final int port;

    public RemotePlayerClient(Player player, String name, int port) {
        this.player = player;
        this.name = name;
        this.port = port;
    }

    public void run() {
        while(true) {
            try(
                    Socket s = new Socket(name, port);
                    BufferedReader r =
                            new BufferedReader(
                                    new InputStreamReader(s.getInputStream(), US_ASCII));
                    BufferedWriter w =
                            new BufferedWriter(
                                    new OutputStreamWriter(s.getOutputStream(), US_ASCII) ) ){

                List<String> messageList = Arrays.asList(r.readLine().split(Pattern.quote(" "), -1))   ;
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
                    w.write(Serdes.ticketBagSerde.serialize(initialTickets));
                    w.flush();
                    break;

                case NEXT_TURN:
                    TurnKind nextTurn = player.nextTurn();
                    w.write(Serdes.turnKindSerde.serialize(nextTurn));
                    w.flush();
                    break;

                case CHOOSE_TICKETS:
                    SortedBag<Ticket> options = Serdes.ticketBagSerde.deserialize(messageList.get(1));
                    SortedBag<Ticket> chosenTickets = player.chooseTickets(options);
                    w.write(Serdes.ticketBagSerde.serialize(chosenTickets));
                    w.flush();
                    break;

                case DRAW_SLOT: 
                    int drawSlot = player.drawSlot();
                    w.write(Serdes.intSerde.serialize(drawSlot));
                    w.flush();
                    break;

                case ROUTE: 
                    Route claimedRoute = player.claimedRoute();
                    w.write(Serdes.routeSerde.serialize(claimedRoute));
                    w.flush();
                    break;

                case CARDS: 
                    SortedBag<Card> cards = player.initialClaimCards();
                    w.write(Serdes.cardBagSerde.serialize(cards));
                    w.flush();
                    break;

                case CHOOSE_ADDITIONAL_CARDS:    
                    List<SortedBag<Card>>  additionalOptions = Serdes.cardListBagSerde.deserialize(messageList.get(1));
                    SortedBag<Card> chosenAdditionalCards = player.chooseAdditionalCards(additionalOptions);
                    w.write(Serdes.cardBagSerde.serialize(chosenAdditionalCards));
                    w.flush();
                    break;
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
    
    

}
