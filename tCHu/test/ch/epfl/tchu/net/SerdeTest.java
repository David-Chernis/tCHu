package ch.epfl.tchu.net;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.epfl.test.TestRandomizer;

public class SerdeTest {
	
	private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    private static String randomName(Random rng, int length) {
        var sb = new StringBuilder();
        for (int i = 0; i < length; i++)
            sb.append(alphabet.charAt(rng.nextInt(alphabet.length())));
        return sb.toString();
    }
	
	@Test
	void ofWorks() {
		Serde<Integer> intSerde = Serde.of(
	            (i) -> String.valueOf(i) ,
	            (i) -> Integer.parseInt(i));
	    
	    Serde<String> stringSerde = Serde.of(
	            (i) -> Base64.getEncoder().encodeToString(i.getBytes(StandardCharsets.UTF_8)) ,
	            (i) -> new String (Base64.getDecoder().decode(i.getBytes(StandardCharsets.UTF_8))));
	    
	    for(int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
	    	int deserializedInt = TestRandomizer.newRandom().nextInt();
	    	String serializedInt = String.valueOf(deserializedInt);
	    	assertEquals(serializedInt, intSerde.serialize(deserializedInt));
	    	assertEquals(deserializedInt, intSerde.deserialize(serializedInt).intValue());
	    	
	    	String deserializedString = randomName(new Random(), (new Random()).nextInt(10));
	    	
	    }
	   
	}
}
