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
import people.dict.model.Gender;
import people.dict.model.Person;

public class NamesFinderTest {

	private static NamesDictionary namesDict = new NamesDictionary();
	
	private static DeclinationRulesSet rules;
	
	private static final String NAMES_DICT_FILE_PATH = "./names_fulldict4.csv";
	private static final String LASTNAMES_DECLIN_RULES = "/lastnames_declination_rules.csv";
	
	@BeforeClass
	public static void onInitClass() throws IOException {
		namesDict.loadFromFile(NAMES_DICT_FILE_PATH);
		rules = new DeclinationRulesSet(DeclinationRulesSet.class.getResourceAsStream(LASTNAMES_DECLIN_RULES));
	}
	
	protected void verifyPersons(final String text, final Person[] expectedResultPerson) {
		PeopleFinder finder = new PeopleFinder(namesDict, rules);
		
		List<Person> actualResultPersons = finder.identifyPeopleInText(text);
		
		assertThat(actualResultPersons, notNullValue());

		// create actualResultPersons map
		final val actualPersonsMap = new HashMap<String, Person>();
		actualResultPersons.forEach((p) -> {
			String id = p.toString();
			actualPersonsMap.put(id, p);
			System.out.print(id + ",");
		});
		System.out.println();

		assertThat(actualResultPersons.size(), equalTo(expectedResultPerson.length));

		for (Person expected : expectedResultPerson) {
			Person actual = actualPersonsMap.get(expected.toString());
			if (actual == null && expectedResultPerson.length == 1 && actualResultPersons.size()==1) {
				actual = actualResultPersons.get(0);
			}
			assertThat("Cannot find person:" + expected.toString(), actual, notNullValue());
			assertThat("Not same names", expected.toString(), equalTo(actual.toString()));
			assertThat("Gender is different", actual.getGender(), equalTo(expected.getGender()));
			assertThat("Person '" + expected.toString() + "' not found", actual, notNullValue());
		}
	}
	
	@Test
	public void testDeclinedNames() {

		assertThat(namesDict.getFirst("Andrzeja"), notNullValue());

		final String TEXT =
				"posła na sejm Andrzeja Drzycimskiego i innych";

		verifyPersons(TEXT, new Person[] {
				Person.builder().gender(Gender.MALE).firstName("Andrzej").lastName("Drzycimski").build()
		});
	}
	
	@Test
	public void testWomanName() {

		final String TEXT =
				"poszła tam razem z Wiktorią Kownacką oraz innymi osobami";
		
		verifyPersons(TEXT, new Person[] { 
				Person.builder().gender(Gender.FEMALE).firstName("Wiktoria").lastName("Kownacka").build()				
		});
	}
	
	@Test
	public void testTwoPeopleName() {
		final String TEXT = 
				"byli to Jan Rokita oraz Stefania Wesołowska z rodziną";
		
		verifyPersons(TEXT, new Person[] { 
				Person.builder().gender(Gender.MALE).firstName("Jan").lastName("Rokita").build(),				
				Person.builder().gender(Gender.FEMALE).firstName("Stefania").lastName("Wesołowska").build()				
		});
	}	
	
	@Test
	public void testTwoLastNames() {
		final String TEXT = 
				"Spotkał tam się z Joanną Czeplik-Nowicką. ";

		verifyPersons(TEXT, new Person[] {
				Person.builder().gender(Gender.FEMALE).firstName("Joanna").lastName("Czeplik-Nowicka").build()				
		});
	}

	@Test
	public void test3People() {
		final String TEXT =
			"Fundacja, z siedzibą w Warszawie, powstała z inicjatywy żony Mariusza Kazany, Barbary Kazany, oraz jego córki, Justyny Kazany";

		verifyPersons(TEXT, new Person[] {
			Person.builder().gender(Gender.MALE).firstName("Mariusz").lastName("Kazana").build(),
			Person.builder().gender(Gender.FEMALE).firstName("Barbara").lastName("Kazana").build(),
			Person.builder().gender(Gender.FEMALE).firstName("Justyna").lastName("Kazana").build()
		});
	}

	@Test
	public void testSimilarPeople() {
		final String TEXT =
			"W spotkaniu uczestniczył prezydent Aleksander Kwaśniewski wraz z małżonką Aleksandrą Kwaśniewską.";

		verifyPersons(TEXT, new Person[] {
			Person.builder().gender(Gender.MALE).firstName("Aleksander").lastName("Kwaśniewski").build(),
			Person.builder().gender(Gender.FEMALE).firstName("Aleksandra").lastName("Kwaśniewska").build()
		});
	}


	@Test
	public void testMoreSentences() {

		assertThat(namesDict.getFirst("Marek"), notNullValue());
		assertThat(namesDict.getFirst("Bertold"), notNullValue());

		final String TEXT =
			"W związku z publikacją Doroty Kani w Super Expressie dotyczącą Marka Siwca i " +
			"skierowaniem przez niego sprawy do sądu, dziennikarka i redaktor Bertold Kittel " +
			"zostali skazani za naruszenie dóbr osobistych i zapłatę odszkodowania. W 2011 " +
			"prawomocnym wyrokiem sądu Dorota Kania została skazana na karę grzywny za " +
			"zniesławienie pułkownika SB, Ryszarda Bieszyńskiego, w związku z treścią artykułu "+
			"pt. „Matka chrzestna”, opublikowanego w tygodniku Wprost z 2007[36][37]. W tej "+
			"sprawie Wiktor Świetlik w imieniu Centrum Monitoringu Wolności Prasy Stowarzyszenia "+
			"Dziennikarzy Polskich wystąpił do Rzecznika Praw Obywatelskich o kasację wyroku w "+
			"sprawie karnej przeciwko redaktor Dorocie Kani.";

		verifyPersons(TEXT, new Person[] {
			Person.builder().gender(Gender.FEMALE).firstName("Dorota").lastName("Kania").build(),
			Person.builder().gender(Gender.MALE).firstName("Marek").lastName("Siwiec").build(),
			Person.builder().gender(Gender.MALE).firstName("Bertold").lastName("Kittel").build(),
			Person.builder().gender(Gender.MALE).firstName("Ryszard").lastName("Bieszyński").build(),
			Person.builder().gender(Gender.MALE).firstName("Wiktor").lastName("Świetlik").build()
		});
	}

}
