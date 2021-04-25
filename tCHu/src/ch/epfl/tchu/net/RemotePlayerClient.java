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
            try(ServerSocket s0 = new ServerSocket(port);
                Socket s = s0.accept();
                BufferedReader r =
                        new BufferedReader(
                        new InputStreamReader(s.getInputStream(), US_ASCII));
                BufferedWriter w =
                        new BufferedWriter(
                        new OutputStreamWriter(s.getOutputStream(), US_ASCII) ) ){
                
                RemotePlayerProxy proxy = new RemotePlayerProxy(s);
                List<String> messageList = Arrays.asList(r.readLine().split(Pattern.quote(" "), -1))   ;
                switch(MessageId.valueOf(messageList.get(0))) {
                
                case INIT_PLAYERS:
                    List<String> namesList = Serdes.stringListSerde.deserialize(messageList.get(2));
                    Map<PlayerId, String> namesMap = Map.of(PLAYER_1, namesList.get(0), PLAYER_2, namesList.get(1));
                    proxy.initPlayers(Serdes.playerIdSerde.deserialize(messageList.get(1)), namesMap);
                    break;
                
                case RECEIVE_INFO:
                    String info = Serdes.stringSerde.deserialize(messageList.get(1));
                    proxy.receiveInfo(info);
                    break;
                
                case UPDATE_STATE: 
                    PublicGameState gs = Serdes.publicGameStateSerde.deserialize(messageList.get(1));
                    PlayerState ps = Serdes.playerStateSerde.deserialize(messageList.get(2));
                    proxy.updateState(gs, ps);
                    break;
                
                case SET_INITIAL_TICKETS:
                    SortedBag<Ticket> tickets = Serdes.ticketBagSerde.deserialize(messageList.get(1));
                    proxy.setInitialTicketChoice(tickets);
                    break;
                
                case CHOOSE_INITIAL_TICKETS:
                    SortedBag<Ticket> initialTickets = proxy.chooseInitialTickets();
                    w.write(Serdes.ticketBagSerde.serialize(initialTickets));
                    break;
                
                case NEXT_TURN:
                    TurnKind nextTurn = proxy.nextTurn();
                    w.write(Serdes.turnKindSerde.serialize(nextTurn));
                    break;
                
                case CHOOSE_TICKETS:
                    SortedBag<Ticket> options = Serdes.ticketBagSerde.deserialize(messageList.get(1));
                    SortedBag<Ticket> chosenTickets = proxy.chooseTickets(options);
                    w.write(Serdes.ticketBagSerde.serialize(chosenTickets));
                    break;
                
                case DRAW_SLOT: 
                    int drawSlot = proxy.drawSlot();
                    w.write(Serdes.intSerde.serialize(drawSlot));
                    break;
                
                case ROUTE: 
                    Route claimedRoute = proxy.claimedRoute();
                    w.write(Serdes.routeSerde.serialize(claimedRoute));
                    break;
                
                case CARDS: 
                    SortedBag<Card> cards = proxy.initialClaimCards();
                    w.write(Serdes.cardBagSerde.serialize(cards));
                    break;
                
                case CHOOSE_ADDITIONAL_CARDS:    
                    List<SortedBag<Card>>  additionalOptions = Serdes.cardListBagSerde.deserialize(messageList.get(1));
                    SortedBag<Card> chosenAdditionalCards = proxy.chooseAdditionalCards(additionalOptions);
                    w.write(Serdes.cardBagSerde.serialize(chosenAdditionalCards));
                    break;
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
    
}
