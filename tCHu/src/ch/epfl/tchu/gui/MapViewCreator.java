package ch.epfl.tchu.gui;

class MapViewCreator {
	
	private MapViewCreator() {}
	
	public static createMapView(ObservableGameState ogs, ObjectProperty<ClaimRouteHandler> property, CardChooser chooser) {
		
	}
	
	@FunctionalInterface
	interface CardChooser {
	  void chooseCards(List<SortedBag<Card>> options,
			   ChooseCardsHandler handler);
	}
}
