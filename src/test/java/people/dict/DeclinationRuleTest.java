package people.dict;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import people.dict.model.PersonName;

public class DeclinationRuleTest {

	private static DeclinationRulesSet rules;
	
	@BeforeClass
	public static void onTestInit() throws IOException {
		rules = new DeclinationRulesSet(new File("./src/test/resources/declination-test.csv"));
	}

	@Test
	public void testEnding_eckiego() {
		List<PersonName> result = rules.getProposedNames("Bieleckiego");
		
		assertThat(result.stream().map(PersonName::getName).distinct().collect(Collectors.toList()))
			.hasSize(1)
			.contains("Bielecki");	
	}
	
	@Test
	public void testEnding_ckiej() {
		List<PersonName> result = rules.getProposedNames("Kownackiej");
		
		assertThat(result.stream().map(PersonName::getName).distinct().collect(Collectors.toList()))
			.hasSize(1)
			.contains("Kownacka");
	}	
	
	@Test
	public void testEnding_skim() {
		List<PersonName> result = rules.getProposedNames("Kowalskim");
		
		assertThat(result.stream().map(PersonName::getName).distinct().collect(Collectors.toList()))
			.hasSize(1)
			.contains("Kowalski");
	}	
	
	@Test
	@Ignore
	public void testEnding_yka() {
		List<PersonName> result = rules.getProposedNames("Bondaryka");
		
		assertThat(result.stream().map(p->p.getText()).distinct().collect(Collectors.toList()))
			.hasSize(1)
			.contains("Bondaryk");		
	}	
	
}
