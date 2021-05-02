package ch.epfl.tchu.gui;

import java.util.List;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

class MapViewCreator {
	
	private MapViewCreator() {}
	
	public static void createMapView(ObservableGameState ogs, ObjectProperty<ClaimRouteHandler> property, CardChooser chooser) {
		Pane Carte = new Pane();
		Carte.getStylesheets().add("map.css");
		Carte.getStylesheets().add("colors.css");
		ImageView fond = new ImageView("map.png");
		
		// Group called route on diagram
		Group routeGroup = new Group();
		routeGroup.setId("AT1_STG_1");
		routeGroup.getStyleClass().add("route");
        routeGroup.getStyleClass().add("UNDERGROUND");
        routeGroup.getStyleClass().add("NEUTRAL");
        
        
        // Group called Case on diagram 
		Group caseGroup = new Group();
		caseGroup.setId("AT1_STG_1_1");
		
		// Voie on Diagram
		Rectangle Voie = new Rectangle(36, 12);
		Voie.getStyleClass().add("track");
	    Voie.getStyleClass().add("filled");
	    
	    // Group called wagon on the diagram 
	    Group wagonGroup = new Group();
	    wagonGroup.getStyleClass().add("car");
	    Rectangle wagonR = new Rectangle(36 , 12);
	    wagonR.getStyleClass().add("filled");
	    Circle circleR1 = new Circle(12, 6, 3);
	    Circle circleR2 = new Circle(24, 6, 3);
	    
	    
	    // Establishing Heirarchy of Nodes.
	    Carte.getChildren().addAll(fond, routeGroup);
	    routeGroup.getChildren().add(caseGroup);
	    caseGroup.getChildren().addAll(Voie, wagonGroup);
        wagonGroup.getChildren().addAll(wagonR, circleR1, circleR2);
	    
	}
	
	@FunctionalInterface
	interface CardChooser {
	  void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
	}
}
