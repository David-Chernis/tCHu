<<<<<<< HEAD
package ch.epfl.tchu.gui;

import java.util.List;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientMain extends Application{

	/**
	 * The main method for the client.
	 * @param args (String[]): the arguments passed to the program at the beginning.
	 */
    public static void main(String[] args) {
        launch(args);
        
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> param = getParameters().getRaw();
        
        // Instiantiation of variables necessary to the creation of the RemotePlayerClient
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
}
=======
package ch.epfl.tchu.gui;

public class ClientMain {
	public static void main(String[] args) {
		
	}
}
>>>>>>> 511729f (Hola)
