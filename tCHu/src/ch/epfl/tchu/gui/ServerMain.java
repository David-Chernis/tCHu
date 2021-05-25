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
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public class ServerMain extends Application{

    public static void main(String[] args) {
        launch(args);
        
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Setting up Server
        RemotePlayerProxy playerProxy = null;
        GraphicalPlayerAdapter gpa = new GraphicalPlayerAdapter();
        try{
            ServerSocket server = new ServerSocket(5108);
            Socket s = server.accept();
            playerProxy = new RemotePlayerProxy(s);
            
        } catch(Exception e) {
            
        }
        
        List<String> names = getParameters().getRaw();
        Random rand = new Random();
        
        //Initializaing Player Maps and GraphicalPlayerAdapter through analyzing parameters
        Map<PlayerId, String> playerNames = Map.of( 
                PLAYER_1, names.isEmpty() ? "Ada" : names.get(0),  
                PLAYER_2, names.isEmpty() ? "Charles" : names.get(1)); 
        Map<PlayerId, Player> players = Map.of( 
                PLAYER_1, gpa,
                PLAYER_2, playerProxy);
        
        new Thread(() -> Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), rand)).start();
    }
}
