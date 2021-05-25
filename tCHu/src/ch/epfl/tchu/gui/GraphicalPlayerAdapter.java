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
    private BlockingQueue<SortedBag<Ticket>> ticketQ;
    private BlockingQueue<TurnKind> turnQ;
    private BlockingQueue<Integer> drawSlotQ;
    private BlockingQueue<Route> routeQ;
    private BlockingQueue<SortedBag<Card>> cardBagQ;
    
    
	private GraphicalPlayer graphicalPlayer;
	
	/**
	 * Default GraphicalPlayerAdapter Constructor. Initializes all the blocking queues needed for
	 * the GraphicalPlayerAdapter to run as intended.
	 */
	public GraphicalPlayerAdapter() {
		ticketQ = new ArrayBlockingQueue<>(1);
		turnQ = new ArrayBlockingQueue<>(1);
		drawSlotQ = new ArrayBlockingQueue<>(1);
		routeQ = new ArrayBlockingQueue<>(1);
		cardBagQ = new ArrayBlockingQueue<>(1);
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
            try {
                ticketQ.put(ticketBag);
            } catch (InterruptedException e) {
                throw new Error();
            }
        }));
	}

	@Override
	public SortedBag<Ticket> chooseInitialTickets() {
	    try {
            return ticketQ.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
	}

	@Override
	public TurnKind nextTurn() {
		runLater(() -> graphicalPlayer.startTurn(
		        
		        () -> {
                    try {
                        turnQ.put(TurnKind.DRAW_TICKETS);
                    } catch (InterruptedException e) {
                        throw new Error();
                    }
                }
		        
		        , 
		        
		        (a) -> {
		                try {
                            turnQ.put(TurnKind.DRAW_CARDS);
                            drawSlotQ.put(a);
                        } catch (InterruptedException e) {
                            throw new Error();
                        }
		            }
		        
		        , 
		        
		        (r, bag) -> {
		            try {
                        turnQ.put(TurnKind.CLAIM_ROUTE);
                        routeQ.put(r);
                        cardBagQ.put(bag);
                    } catch (InterruptedException e) {
                        throw new Error();
                    }
		        }
		        
		        ));
		try {
            return turnQ.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
		
	}

	@Override
	public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
	    setInitialTicketChoice(options);
        return chooseInitialTickets();
	}

	@Override
	public int drawSlot() {
	    if(!drawSlotQ.isEmpty()) {
	        try {
                return drawSlotQ.take();
            } catch (InterruptedException e) {
                throw new Error();
            }
	    } else {
	        runLater(() -> graphicalPlayer.drawCard((a) -> {
                try {
                    drawSlotQ.put(a);
                } catch (InterruptedException e) {
                    throw new Error();
                }
            }));
	        
	        try {
	            return drawSlotQ.take();
	        } catch (InterruptedException e) {
	            throw new Error();
	        }
	    }
	    
	}

	@Override
	public Route claimedRoute() {
	    
		try {
            return routeQ.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
	}

	@Override
	public SortedBag<Card> initialClaimCards() {
	    try {
            return cardBagQ.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
	}

	@Override
	public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
	    runLater( () -> graphicalPlayer.chooseAdditionalCards(options, 
	            (bag) -> {
	                try {
                        cardBagQ.put(bag);
                    } catch (InterruptedException e) {
                        throw new Error();
                    }
	            })
	    );
	    
		try {
            return cardBagQ.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
	}
	
	
}
