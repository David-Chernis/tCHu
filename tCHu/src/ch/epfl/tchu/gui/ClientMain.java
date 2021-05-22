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
        List<String> prop = getParameters().getRaw();
        String name = prop.get(0);
        int port = Integer.parseInt(prop.get(1));
        GraphicalPlayerAdapter playerAdapter = new GraphicalPlayerAdapter();
        RemotePlayerClient client = new RemotePlayerClient(playerAdapter, name, port);
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
