package ch.epfl.tchu.net;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Player.TurnKind;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicCardState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.PublicPlayerState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

public final class Serdes {
    public static final Serde<Integer> intSerde = Serde.of(
            (i) -> Integer.toString(i) ,
            Integer::parseInt);
    
    public static final Serde<String> stringSerde = Serde.of(
            (i) -> Base64.getEncoder().encodeToString(i.getBytes(StandardCharsets.UTF_8)) ,
            (i) -> new String (Base64.getDecoder().decode(i.getBytes(StandardCharsets.UTF_8))));
    
    public static final Serde<PlayerId> playerIdSerde = Serde.oneOf(PlayerId.ALL);
    public static final Serde<TurnKind> turnKindSerde = Serde.oneOf(TurnKind.ALL);
    public static final Serde<Card> cardSerde = Serde.oneOf(Card.ALL);
    public static final Serde<Route> routeSerde = Serde.oneOf(ChMap.routes());
    public static final Serde<Ticket> ticketSerde = Serde.oneOf(ChMap.tickets());
    
    public static final Serde<List<String>> stringListSerde = Serde.listOf(stringSerde, ',');
    public static final Serde<List<Card>> cardListSerde = Serde.listOf(cardSerde, ',');
    public static final Serde<List<Route>> routeListSerde = Serde.listOf(routeSerde, ',');
    public static final Serde<SortedBag<Card>> cardBagSerde = Serde.bagOf(cardSerde, ',');
    public static final Serde<SortedBag<Ticket>> ticketBagSerde = Serde.bagOf(ticketSerde, ',');
    public static final Serde<List<SortedBag<Card>>> cardListBagSerde = Serde.listOf(cardBagSerde, ';');
    
    public static final Serde<PublicCardState> publicCardStateSerde = Serde.of(
            (i) -> cardListSerde.serialize(i.faceUpCards()) + 
            ";" + intSerde.serialize(i.deckSize()) + 
            ";" + intSerde.serialize(i.discardsSize())
            
            , 
            
            (i) -> {
                List<String> tempList = Arrays.asList(i.split(Pattern.quote(";"), -1));
                System.out.println(cardListSerde.deserialize(tempList.get(0)));
                System.out.println(intSerde.deserialize(tempList.get(1)));
                System.out.println(intSerde.deserialize(tempList.get(2)));
                return new PublicCardState(
                        cardListSerde.deserialize(tempList.get(0)), 
                        intSerde.deserialize(tempList.get(1)), 
                        intSerde.deserialize(tempList.get(2)));
            });
    
    public static final Serde<PublicPlayerState> publicPlayerStateSerde = Serde.of(
            (i) -> intSerde.serialize(i.ticketCount()) + 
            ";" + intSerde.serialize(i.cardCount()) + 
            ";" + routeListSerde.serialize(i.routes())
            
            , 
            
            (i) -> {
                List<String> tempList = Arrays.asList(i.split(Pattern.quote(";"), -1));
                return new PublicPlayerState(
                        intSerde.deserialize(tempList.get(0)), 
                        intSerde.deserialize(tempList.get(1)), 
                        routeListSerde.deserialize(tempList.get(2)));
            });
    
    public static final Serde<PlayerState> playerStateSerde = Serde.of(
            (i) -> ticketBagSerde.serialize(i.tickets()) + 
            ";" + cardBagSerde.serialize(i.cards()) + 
            ";" + routeListSerde.serialize(i.routes())
            
            , 
            
            (i) -> {
                List<String> tempList = Arrays.asList(i.split(Pattern.quote(";"), -1));
                System.out.println(ticketBagSerde.deserialize(tempList.get(0)));
                System.out.println(cardBagSerde.deserialize(tempList.get(1)));
                System.out.println(routeListSerde.deserialize(tempList.get(2)));
                return new PlayerState(
                        ticketBagSerde.deserialize(tempList.get(0)), 
                        cardBagSerde.deserialize(tempList.get(1)), 
                        routeListSerde.deserialize(tempList.get(2)));
            });
    
    public static final Serde<PublicGameState> publicGameStateSerde = Serde.of(
            (i) -> 
            intSerde.serialize(i.ticketsCount()) + 
            ":" + publicCardStateSerde.serialize(i.cardState()) + 
            ":" + playerIdSerde.serialize(i.currentPlayerId()) + 
            ":" + publicPlayerStateSerde.serialize(i.playerState(PlayerId.PLAYER_1)) +
            ":" + publicPlayerStateSerde.serialize(i.playerState(PlayerId.PLAYER_2)) +
            ":" + playerIdSerde.serialize(i.lastPlayer())
            
            , 
            
            (i) -> {
                List<String> tempList = Arrays.asList(i.split(Pattern.quote(":"), -1));
                Map<PlayerId, PublicPlayerState> playerStates = Map.of(
                        PlayerId.PLAYER_1,
                        publicPlayerStateSerde.deserialize(tempList.get(3)),
                        PlayerId.PLAYER_2,
                        publicPlayerStateSerde.deserialize(tempList.get(4)));
                System.out.println("wagons: " + intSerde.deserialize(tempList.get(0)).intValue());
                System.out.println("playerId: " +playerIdSerde.deserialize(tempList.get(2)));
                System.out.println("lastPlayer " +playerIdSerde.deserialize(tempList.get(5)));
                return new PublicGameState(
                        intSerde.deserialize(tempList.get(0)).intValue(),
                        publicCardStateSerde.deserialize(tempList.get(1)),
                        playerIdSerde.deserialize(tempList.get(2)),
                        playerStates,
                        playerIdSerde.deserialize(tempList.get(5))
                        );
            }); 
}
