package ch.epfl.tchu.gui;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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

/**
 * Class that adapts an instance of GraphicalPlayer into a variable of type Player. Also makes
 * the methods described below to force some of the processes in the game to be run on the JavaFX
 * thread.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class GraphicalPlayerAdapter implements Player{
    private static final int QUEUE_SIZE = 1;

    private final BlockingQueue<SortedBag<Ticket>> ticketQ;
    private final BlockingQueue<TurnKind> turnQ;
    private final BlockingQueue<Integer> drawSlotQ;
    private final BlockingQueue<Route> routeQ;
    private final BlockingQueue<SortedBag<Card>> cardBagQ;


    private GraphicalPlayer graphicalPlayer;

    /**
     * Default GraphicalPlayerAdapter Constructor. Initializes all the blocking queues needed for
     * the GraphicalPlayerAdapter to run as intended.
     */
    public GraphicalPlayerAdapter() {
        ticketQ = new ArrayBlockingQueue<>(QUEUE_SIZE);
        turnQ = new ArrayBlockingQueue<>(QUEUE_SIZE);
        drawSlotQ = new ArrayBlockingQueue<>(QUEUE_SIZE);
        routeQ = new ArrayBlockingQueue<>(QUEUE_SIZE);
        cardBagQ = new ArrayBlockingQueue<>(QUEUE_SIZE);
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
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
        runLater(() -> graphicalPlayer.chooseTickets(tickets, 
                (ticketBag) -> {
                    QPut(ticketQ, ticketBag);
                }));
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return QTake(ticketQ);
    }

    @Override
    public TurnKind nextTurn() {
        runLater(() -> graphicalPlayer.startTurn(

                () -> {     
                    QPut(turnQ, TurnKind.DRAW_TICKETS);  
                }  , 

                (a) -> {
                    QPut(turnQ, TurnKind.DRAW_CARDS);
                    QPut(drawSlotQ, a);
                }  , 

                (r, bag) -> {
                    QPut(turnQ, TurnKind.CLAIM_ROUTE);
                    QPut(routeQ, r);
                    QPut(cardBagQ, bag);
                }));

        return QTake(turnQ);
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        setInitialTicketChoice(options);
        return chooseInitialTickets();
    }

    @Override
    public int drawSlot() {
        if(!drawSlotQ.isEmpty()) {
            return QTake(drawSlotQ);
        } else {

            runLater(() -> graphicalPlayer.drawCard((a) -> {
                QPut(drawSlotQ, a);
            }));

            return QTake(drawSlotQ);
        }

    }

    @Override
    public Route claimedRoute() {
        return QTake(routeQ);
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        return QTake(cardBagQ);
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        runLater( () -> graphicalPlayer.chooseAdditionalCards(options, 
                    (bag) -> {
                        QPut(cardBagQ, bag);
                    })
                );

        return QTake(cardBagQ);
    }


    private <T> T QTake(BlockingQueue<T> genericQ) {
        try {
            return genericQ.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    private <T> void QPut(BlockingQueue<T> genericQ, T genericVar) {
        try {
            genericQ.put(genericVar);
        } catch (InterruptedException e) {
            throw new Error();
        }
    }
}
