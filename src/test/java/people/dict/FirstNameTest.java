package people.dict;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import people.graph.NamesDictionary;

public class FirstNameTest {

	@Test
	public void readNamesFromResource() {
		try {
			NamesDictionary.loadNamesFromResource();
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}
	
}
