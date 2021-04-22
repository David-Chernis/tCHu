package ch.epfl.tchu.net;

import java.util.List;
import java.util.function.Function;

import ch.epfl.tchu.SortedBag;

/**
 * Interface representing (de)serialization algorithms for communications between the players and the server.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 * @param <T> represents the type of object that will be (de)serialized.
 */
public interface Serde<T> {
	
	/**
	 * Returns the String that represents the serialized representation of the given object.
	 * @param deserialized (T): the object to be serialized.
	 * @return (String): the String that represents the serialized representation of the given object.
	 */
	String serialize(T deserialized);
	
	/**
	 * Returns the Object that the given serialized String represents.
	 * @param serialized (String): the String to be deserialized.
	 * @return (T): the Object that the given serialized String represents.
	 */
	T deserialize(String serialized);
	
	/**
	 * Returns the corresponding Serde based on the given serialization and deserialization functions.
	 * @param <T> the Object type that will be (de)serialized with the given functions.
	 * @param serialization (Function<T, String>): the serialization function.
	 * @param deserialization (Function<T, String>): the deserialization function.
	 * @return
	 */
	public static <T> Serde<T> of(Function<T, String> serialization, Function<String, T> deserialization){
		return new Serde<T>() {
			public String serialize(T deserialized) {
				return serialization.apply(deserialized);
			}

			public T deserialize(String serialized) {
				return deserialization.apply(serialized);
			}
		};
	}
	
	/**
	 * Returns a Serde that can (de)serialize enumerable or enumerable-like values based on a list containing
	 * all the possible enumerable or enumerable-like values, which is given as parameter to the method.
	 * @param <T> the Object type to be (de)serialized.
	 * @param allEnumValues (List<T>): the list containing all the possible enumerable or enumerable-like
	 * values.
	 * @return (Serde<T>): a Serde that can (de)serialize enumerable or enumerable-like values based on a list
	 * containing all the possible enumerable or enumerable-like values, which is given as parameter to the
	 * method.
	 */
	public static <T> Serde<T> oneOf(List<T> allEnumValues){
		return new Serde<T>() {
			public String serialize(T deserialized) {
				Integer integerRepresentation = allEnumValues.indexOf(deserialized);
				return integerRepresentation.toString();
			}

			public T deserialize(String serialized) {
				return allEnumValues.get(Integer.parseInt(serialized));
			}
		};
	}
	
	/**
	 * 
	 * @param <T>
	 * @param oldSerde
	 * @param separator
	 * @return
	 */
	public static <T> Serde<List<T>> listOf(Serde<T> oldSerde, char separator){
		return new Serde<List<T>>() {
			public String serialize(List<T> deserialized) {
				return null;
			}

			public List<T> deserialize(String serialized) {
				return null;
			}
		};
	}
	
	/**
	 * 
	 * @param <T>
	 * @param values
	 * @param separator
	 * @return
	 */
	public static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(SortedBag<T> values, char separator){
		return new Serde<SortedBag<T>>() {
			public String serialize(SortedBag<T> deserialized) {
				return null;
			}

			public SortedBag<T> deserialize(String serialized) {
				return null;
			}
		};
	}
}
