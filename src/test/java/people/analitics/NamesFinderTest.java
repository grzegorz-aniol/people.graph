package people.analitics;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import lombok.val;
import people.dict.DeclinationRulesSet;
import people.dict.NamesDictionary;
import people.dict.model.Person;

public class NamesFinderTest {

	private static NamesDictionary namesDict = new NamesDictionary();
	
	private static DeclinationRulesSet rules;
	
	private static final String NAMES_DICT_FILE_PATH = "./names_fulldict3.csv";
	private static final String LASTNAMES_DECLIN_RULES = "/lastnames_declination_rules.csv";
	
	@BeforeClass
	public static void onInitClass() throws IOException {
		namesDict.loadFromFile(NAMES_DICT_FILE_PATH);
		rules = new DeclinationRulesSet(DeclinationRulesSet.class.getResourceAsStream(LASTNAMES_DECLIN_RULES));
	}
	
	protected void verifyPersons(final String text, final Person[] results) {
		PeopleFinder finder = new PeopleFinder(namesDict, rules);
		
		List<Person> persons = finder.identifyPeopleInText(text);
		
		assertThat(persons, notNullValue());
		assertThat(persons.size(), equalTo(results.length));
			
		// create persons map
		final val personsMap = new HashMap<String, Person>();
		persons.forEach((p)->personsMap.put(p.toString(), p));
		
		for (Person expected : results) {
			Person actual = personsMap.get(expected.toString());
			if (actual == null && results.length == 1) {
				actual = results[0];
			}
			assertThat("Cannot find person:" + expected.toString(), actual, notNullValue());
			assertThat("Gender is different", actual.getMale(), equalTo(expected.getMale()));
			assertThat("Person '" + expected.toString() + "' not found", actual, notNullValue());
		}
	}
	
	@Test
	public void testDeclinedNames() {
		final String TEXT = 
				"posła na sejm Andrzeja Drzycimskiego i innych";
		
		verifyPersons(TEXT, new Person[] { 
				Person.builder().male(true).firstName("Andrzej").lastName("Drzycimski").build()				
		});
	}
	
	@Test
	public void testWomanName() {
		final String TEXT = 
				"poszła tam razem z Wiktorią Kownacką oraz innymi osobami";
		
		verifyPersons(TEXT, new Person[] { 
				Person.builder().male(false).firstName("Wiktoria").lastName("Kownacka").build()				
		});
	}
	
	@Test
	public void testTwoPeopleName() {
		final String TEXT = 
				"byli to Jan Rokita oraz Stefania Wesołowska z rodziną";
		
		verifyPersons(TEXT, new Person[] { 
				Person.builder().male(true).firstName("Jan").lastName("Rokita").build(),				
				Person.builder().male(false).firstName("Stefania").lastName("Wesołowska").build()				
		});
	}	
	
	@Test
	public void testTwoLastNames() {
		final String TEXT = 
				"Spotkał tam się z Joanną Czeplik-Nowicką. ";
		
		verifyPersons(TEXT, new Person[] { 
				Person.builder().male(false).firstName("Joanna").lastName("Czeplik-Nowicka").build()				
		});
	}

	
}
