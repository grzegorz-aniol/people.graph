package people.dict;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class FirstNameTest {

	@Test
	public void readNamesFromResource() {
		try {
			NamesDictionary dict = new NamesDictionary();
			dict.loadNamesFromResource();
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}
	
}
