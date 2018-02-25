package people.dict;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class WordAnalyzerTest {

	private String makeSyllableString(final String[] input) {
		return String.join("-", input);
	}
	
	private void printCase(final String[] expected, final String[] acctual) {
		System.out.println("Expected: " + makeSyllableString(expected));
		System.out.println("Result: " + makeSyllableString(acctual));
	}
	
	private void testData(final String input, final String[] expected) {
		String[] result = WordAnalyzer.splitWordIntoSyllable(input);
		if (expected.length != result.length) {
			printCase(expected, result);
			Assert.assertEquals(expected.length, result.length);
		}
		for (int i=0; i < expected.length; ++i) {
			if (expected[i].length() != result[i].length()) {
				printCase(expected, result);
				Assert.assertEquals(expected[i].length(), result[i].length());
			}
		}
	}
	
	@Test
	public void testKominiarka() {
		final String input = "kominiarka";
		final String[] result = {"ko", "mi", "niar", "ka"};
		testData(input, result);
	}

	@Test
	public void testPakuneczek() {
		final String input = "pakuneczek";
		final String[] result = {"pa", "ku", "ne", "czek"};
		testData(input, result);
	}

	@Test
	public void testArtysta() {
		final String input = "artysta";
		final String[] result = {"ar", "tys", "ta"};
		testData(input, result);
	}
	
	@Test
	public void testPapier() {
		final String input = "papier";
		final String[] result = {"pa", "pier"};
		testData(input, result);
	}	

	@Test
	public void testArtur() {
		final String input = "Artur";
		final String[] result = {"Ar", "tur"};
		testData(input, result);
	}

	@Test
	public void testDominika() {
		final String input = "Dominika";
		final String[] result = {"Do", "mi", "ni", "ka"};
		testData(input, result);
	}


	@Test
	public void testKrzak() {
		final String input = "krzak";
		final String[] result = {"krzak"};
		testData(input, result);
	}

	@Test
	public void testCieszyć() {
		final String input = "cieszyć";
		final String[] result = {"cie", "szyć"};
		testData(input, result);
	}	
	
	@Test
	public void testRatunek() {
		final String input = "ratunek";
		final String[] result = {"ra", "tu", "nek"};
		testData(input, result);
	}
	
	@Test
	@Ignore
	public void testJoanna() {
		final String input = "Joanna";
		final String[] result = {"Jo", "an", "na"};
		testData(input, result);
	}
	
	@Test
	public void testApoloniusz() {
		final String input = "Apoloniusz";
		final String[] result = {"A", "po", "lo", "niusz"};
		testData(input, result);
	}		
	
	@Test
	public void testHubert() {
		final String input = "Hubert";
		final String[] result = {"Hu", "bert"};
		testData(input, result);
	}
	
	@Test
	public void testCzarneckiego() {
		final String input = "Czarneckiego";
		final String[] result = {"Czar", "nec", "kie", "go"};
		testData(input, result);
	}		
	
}
