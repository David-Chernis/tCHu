package ch.epfl.tchu.gui;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static ch.epfl.tchu.game.PlayerId.PLAYER_3;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.MessageId;
import ch.epfl.tchu.net.RemotePlayerClient;
import ch.epfl.tchu.net.RemotePlayerProxy;
import ch.epfl.tchu.net.Serdes;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public final class MainMenu extends Application{
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Text newIntroText = new Text("choisissez si vous souhaitez héberger un serveur ou en rejoindre un");
        
        Button joinGameButton = new Button("Rejoins une partie");
        Button hostGameButton = new Button("héberger un jeu");
        HBox newButtonBox = new HBox(joinGameButton, hostGameButton);

        BorderPane mainMenuPane = new BorderPane(newButtonBox, newIntroText, null, null, null);
        
        Scene mainMenuScene = new Scene(mainMenuPane);
        Stage mainMenuStage = new Stage();
        System.out.println("hi");
        mainMenuStage.setScene(mainMenuScene);
        mainMenuStage.setTitle("tCHu - Main Menu");
        mainMenuStage.show();
        
        joinGameButton.setOnMouseClicked((e) -> {
            newButtonBox.setVisible(false);
            newIntroText.setText("veuillez saisir l'adresse IP et le port que vous souhaitez rejoindre");
            TextField address = new TextField("Address");
            TextField port = new TextField("Port");
            Button Submit = new Button("Submit");
            
            VBox infos = new VBox(address, port, Submit);
            mainMenuStage.setScene(new Scene(infos));
            
            Submit.setOnMouseClicked((event) -> {
                List<String> arguments = List.of(address.textProperty().get(), port.textProperty().get());
                
                try {
                    initializeClient(arguments);
                } catch (Exception e1) {
                    throw new Error();
                }
            });
            
        });
        
        hostGameButton.setOnMouseClicked((e) -> {
            newButtonBox.setVisible(false);
            newIntroText.setText("Choissisez le nombre des joueurs qui vont jouer !");
            
            Button twoPlayerButton = new Button("2 Joueurs");
            Button threePlayerButton = new Button("3 Joueurs");
            HBox buttonBox = new HBox(twoPlayerButton, threePlayerButton);
            
            BorderPane newPane = new BorderPane(buttonBox, newIntroText, null, null, null);
            
            twoPlayerButton.setOnMouseClicked((event) -> {
                Constants.THREE_PLAYER = false;
                twoPlayerButton.disableProperty().set(false);
                newPane.setVisible(false);
                initializeMenu(false, twoPlayerButton, buttonBox, newIntroText, mainMenuStage);
            });
            threePlayerButton.setOnMouseClicked((event) -> {
                Constants.THREE_PLAYER = true;
                threePlayerButton.disableProperty().set(false);
                newPane.setVisible(false);
                initializeMenu(true, threePlayerButton, buttonBox, newIntroText, mainMenuStage);
            });
            mainMenuStage.setScene(new Scene(newPane));
            
        });
    }
    
    private void initializeClient(List<String> param) throws Exception{

        // analysis of parameters in order to instantiate variables necessary to the creation of RemotePlayerClient 
        String address = param.isEmpty() 
                ? "localhost" 
                : param.get(0);
        int port = param.isEmpty() 
                ? 5108 
                : Integer.parseInt(param.get(1));
        GraphicalPlayerAdapter playerAdapter = new GraphicalPlayerAdapter();
        
        // Creation of the Client.
        RemotePlayerClient client = new RemotePlayerClient(playerAdapter, address, port);
        
        // Running the thread.
        new Thread(() -> client.run()).start();
    }
    
    private void initializeMenu(boolean isThreePlayer, Button button, HBox buttonBox, Text introText, Stage stage) {
        
        button.disableProperty().set(true);
        buttonBox.setVisible(false);
        introText.setText("Attente de la connexion des joueurs !");
        
        Button startServerButton = new Button("Start Server");
        
        startServerButton.setOnMouseClicked((e) -> {
            try {
                initializeServer( getParameters().getRaw());
            } catch (Exception e1) {
                throw new Error();
            }
        });
        stage.setScene(new Scene(startServerButton));
        
    }
    
    private void initializeServer(List<String> names) throws Exception{
        System.out.println(names);
        
        
        if(Constants.THREE_PLAYER) {
            // Setting up Server
            RemotePlayerProxy playerProxy1 = null;
            RemotePlayerProxy playerProxy2 = null;
            GraphicalPlayerAdapter gpa = new GraphicalPlayerAdapter();
            
            try{
                ServerSocket server = new ServerSocket(5108);
                Socket s1 = server.accept();
                Socket s2 = server.accept();
                
                
                playerProxy1 = new RemotePlayerProxy(s1);
                playerProxy2 = new RemotePlayerProxy(s2);
                
                
            } catch(IOException e) {
                throw new UncheckedIOException(e);
            }
            
            Random rand = new Random();
            
            //Initializaing Player Maps and GraphicalPlayerAdapter through analyzing parameters
            Map<PlayerId, String> playerNames = Map.of( 
                    PLAYER_1, names.isEmpty() ? "Ada" : names.get(0),  
                    PLAYER_2, names.isEmpty() ? "Charles" : names.get(1),
                    PLAYER_3, names.isEmpty() ? "Michel" : names.get(2)); 
            Map<PlayerId, Player> players = Map.of( 
                    PLAYER_1, gpa,
                    PLAYER_2, playerProxy1,
                    PLAYER_3, playerProxy2);
            
            new Thread(() -> Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), rand, 1)).start();
        }
        else {

            // Setting up Server
            RemotePlayerProxy playerProxy = null;
            GraphicalPlayerAdapter gpa = new GraphicalPlayerAdapter();
            
            try{
                ServerSocket server = new ServerSocket(5108);
                Socket s = server.accept();
                
                playerProxy = new RemotePlayerProxy(s);
                
            } catch(IOException e) {
                throw new UncheckedIOException(e);
            }
            
            Random rand = new Random();
            
            //Initializaing Player Maps and GraphicalPlayerAdapter through analyzing parameters
            Map<PlayerId, String> playerNames = Map.of( 
                    PLAYER_1, names.isEmpty() ? "Ada" : names.get(0),  
                    PLAYER_2, names.isEmpty() ? "Charles" : names.get(1)); 
            Map<PlayerId, Player> players = Map.of( 
                    PLAYER_1, gpa,
                    PLAYER_2, playerProxy);
            
            new Thread(() -> Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), rand, 0)).start();
        }
    }

	
}
