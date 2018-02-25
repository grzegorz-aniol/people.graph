package people.analitics;

import lombok.val;
import org.junit.BeforeClass;
import org.junit.Test;
import people.dict.DeclinationRulesSet;
import people.dict.NamesDictionary;
import people.dict.model.Gender;
import people.dict.model.Person;
import people.dict.model.PersonName;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NamesFinderTest {

	private static NamesDictionary namesDict = new NamesDictionary();
	
	private static DeclinationRulesSet rules;
	
	private static final String NAMES_DICT_FILE_PATH = "./names_fulldict4.csv";
	private static final String LASTNAMES_DECLIN_RULES = "/lastnames_declination_rules.csv";
	
	@BeforeClass
	public static void onInitClass() throws IOException {
		namesDict.loadFromFile(NAMES_DICT_FILE_PATH);
		assertThat(namesDict.size()).isGreaterThan(0L);

		rules = new DeclinationRulesSet(DeclinationRulesSet.class.getResourceAsStream(LASTNAMES_DECLIN_RULES));
		assertThat(rules.size()).isGreaterThan(0L);
	}
	
	protected void verifyPersons(final String text, final Person[] expectedResultPerson) {
		PeopleFinder finder = new PeopleFinder(namesDict, rules);

		List<Person> actualResultPersons = finder.identifyPeopleInText(text);

		assertThat(actualResultPersons).isNotNull();

		// create actualResultPersons map
		final val actualPersonsMap = new HashMap<String, Person>();
		actualResultPersons.forEach((p) -> {
			String id = p.toString();
			actualPersonsMap.put(id, p);
			System.out.print(id + ",");
		});
		System.out.println();

		assertThat(actualResultPersons.size()).isEqualTo(expectedResultPerson.length);

		for (Person expected : expectedResultPerson) {
			Person actual = actualPersonsMap.get(expected.toString());
			if (actual == null && expectedResultPerson.length == 1 && actualResultPersons.size()==1) {
				actual = actualResultPersons.get(0);
			}
			assertThat(actual).as("Cannot find person:" + expected.toString()).isNotNull();
			assertThat(actual.toString()).as("Not same names").isEqualTo(expected.toString());
			assertThat(actual.getGender()).as("Gender is different").isEqualTo(expected.getGender());
		}
	}

	@Test
	public void testSkipAcronym() {

		PeopleFinder finder = new PeopleFinder(namesDict, rules);
		assertThat(finder.isUpperCase("Welcome")).isEqualTo(false);
		assertThat(finder.isUpperCase("welcome")).isEqualTo(false);
		assertThat(finder.isUpperCase("PZPR")).isEqualTo(true);

		assertThat(finder.isLastName(new WordText("DYWIZJA"), Gender.UNKNOWN)).isEqualTo(null);

	}
	
	@Test
	public void testDeclinedNames() {

		assertThat(namesDict.getFirst("Andrzeja")).isNotNull();

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

		PersonName testName = namesDict.getFirst("Aleksandrą");
		assertThat(testName).isNotNull();

		final String TEXT =
			"W spotkaniu uczestniczył prezydent Aleksander Kwaśniewski wraz z małżonką Aleksandrą Kwaśniewską.";

		verifyPersons(TEXT, new Person[] {
			Person.builder().gender(Gender.MALE).firstName("Aleksander").lastName("Kwaśniewski").build(),
			Person.builder().gender(Gender.FEMALE).firstName("Aleksandra").lastName("Kwaśniewska").build()
		});
	}


	@Test
	public void testMoreSentences() {

		assertThat(namesDict.getFirst("Marek")).isNotNull();
		assertThat(namesDict.getFirst("Bertold")).isNotNull();

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
