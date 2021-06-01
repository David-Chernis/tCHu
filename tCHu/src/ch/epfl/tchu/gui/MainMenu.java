package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Constants;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public final class MainMenu extends Application{
	
	public MainMenu(Stage primStage) {
		Text mainMenuIntroText = new Text("Choissisez le nombre des joueurs qui vont jouer !");
		mainMenuIntroText.setTextAlignment(TextAlignment.CENTER);
		
		Button twoPlayerButton = new Button("2 Joueurs");
		Button threePlayerButton = new Button("3 Joueurs");
		HBox buttonBox = new HBox(twoPlayerButton, threePlayerButton);
		
		BorderPane mainMenuPane = new BorderPane(buttonBox, mainMenuIntroText, null, null, null);
		Scene mainMenuScene = new Scene(mainMenuPane);
		Stage mainMenuStage = primStage;
		System.out.println("hi");
		mainMenuStage.setScene(mainMenuScene);
		mainMenuStage.setTitle("tCHu - Main Menu");
		mainMenuStage.show();
		
		twoPlayerButton.setOnMouseClicked((e) -> {
			Constants.THREE_PLAYER = false;
			twoPlayerButton.disableProperty().set(true);
			buttonBox.setVisible(false);
			mainMenuIntroText.setText("Attente de la connexion de joueur !");
		});
		threePlayerButton.setOnMouseClicked((e) -> {
			Constants.THREE_PLAYER = true;
			threePlayerButton.disableProperty().set(true);;
			buttonBox.setVisible(false);
			mainMenuIntroText.setText("Attente de la connexion des joueurs !");
		});
		
		
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		new MainMenu(primaryStage);
	}
}
