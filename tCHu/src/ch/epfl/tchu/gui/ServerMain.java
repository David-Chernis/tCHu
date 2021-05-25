package ch.epfl.tchu.gui;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

public class ServerMain extends Application{

    public static void main(String[] args) {
        launch(args);
        
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub
        List<String> names = getParameters().getRaw();
        Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, names.get(0), PlayerId.PLAYER_2, names.get(1));
        ServerSocket server = new ServerSocket(5108);
        Socket s0 = server.accept();
        Random r = new Random(600743829L);
        
        GraphicalPlayerAdapter gpa = new GraphicalPlayerAdapter();
        gpa.initPlayers(PlayerId.PLAYER_1, playerNames);
        
        RemotePlayerProxy playerProxy = new RemotePlayerProxy(s0);
        
        Map<PlayerId, Player> players = Map.of(PlayerId.PLAYER_1, gpa, PlayerId.PLAYER_2, playerProxy);
        new Thread(() -> Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), r)).start();
    }
}
