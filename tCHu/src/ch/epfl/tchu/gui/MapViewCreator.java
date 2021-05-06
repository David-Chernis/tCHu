package ch.epfl.tchu.gui;

import java.util.List;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Class that allows for the creation of the graphical elements needed to display the map of the game.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
class MapViewCreator {
	
	/**
	 * Private MapViewCreator constructor, whose sole aim is to make this class uninstanciable.
	 */
	private MapViewCreator() {}
	
	/**
	 * Creates a view of the map of the game, based on the given parameters.
	 * @param gameState (ObservableGameState): the observable game state of the game.
	 * @param property (ObjectProperty<ClaimRouteHandler>): the property of the game.
	 * @param chooser (CardChooser): the card chooser of the game.
	 */
	public static Pane createMapView(ObservableGameState gameState, ObjectProperty<ClaimRouteHandler> claimRouteHP, CardChooser chooser) {
		Pane Carte = new Pane();
		Carte.getStylesheets().add("map.css");
		Carte.getStylesheets().add("colors.css");
		ImageView fond = new ImageView("map.png");
		Carte.getChildren().add(fond);
		
		// Group called route on diagram
		
		for(Route r : ChMap.routes()) {
		    Group routeGroup = new Group();
		    gameState.routeId(r).addListener((o, oV, nV) -> routeGroup.getStyleClass().add(nV.name()));
		    routeGroup.disableProperty().bind(claimRouteHP.isNull().or(gameState.claimable(r).not()));
	        routeGroup.setId(r.id());
	        routeGroup.getStyleClass().add("route");
	        routeGroup.getStyleClass().add(r.level().name());
	        routeGroup.getStyleClass().add(r.color() == null ? "NEUTRAL" : r.color().name());
	        
	        
	        for(int j = 0; j < r.length(); j++) {
	            Group caseGroup = new Group();
	            caseGroup.setId(r.id() + "_" + (j+1));
	            
	            // Voie on Diagram
	            Rectangle Voie = new Rectangle(36, 12);
	            Voie.getStyleClass().add("track");
	            Voie.getStyleClass().add("filled");
	            
	            // Group called wagon on the diagram 
	            Group wagonGroup = new Group();
	            wagonGroup.getStyleClass().add("car");
	            
	            
	            Rectangle wagonR;
	            wagonR = new Rectangle(36 , 12);
	            wagonR.getStyleClass().add("filled");
	            Circle circleR1;
	            circleR1 = new Circle(12, 6, 3);           
	            Circle circleR2; 
	            circleR2 = new Circle(24, 6, 3);
	            
	            routeGroup.getChildren().add(caseGroup);
	            caseGroup.getChildren().addAll(Voie, wagonGroup);
                wagonGroup.getChildren().addAll(wagonR, circleR1, circleR2);
	        }
	        Carte.getChildren().add(routeGroup);
	        
	        
		}
        return Carte;
	}
	
	/**
	 * Functional interface which is embedded into the MapViewCreator class, and whose aim
	 * is to force implementing classes to define a method which allows it to choose cards.
	 * @author Shrey Mittal (312275)
	 * @author David Chernis (310298)
	 */
	@FunctionalInterface
	interface CardChooser {
		/**
		 * Chooses cards from the list of options and based on the handler provided.
		 * @param options (List<SortedBag<Card>>): the options of cards that can be drawn.
		 * @param handler (ChooseCardsHandler): the handler which is used to choose cards.
		 */
		void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
	}
}
