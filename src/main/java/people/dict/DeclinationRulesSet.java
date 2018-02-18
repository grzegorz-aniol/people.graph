package people.dict;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import people.dict.model.DeclinationRule;
import people.dict.model.NounDeclination;
import people.dict.model.PersonName;
import people.dict.model.SpeechPart;
import people.dict.model.Word;

public class DeclinationRulesSet {

	private static String[] maleLastNameSufixes = {"ski", "cki", "dzki", "ewy", "owy"};
	
	private static String[] femaleLastNameSufixes = {"ska", "cka", "dzka", "ewa", "owa"};
	
	@Getter @Setter
	@NoArgsConstructor @AllArgsConstructor @Builder
	private static class RulesNode {
		public String text;
		public DeclinationRule rule;
		public NounDeclination declination;
	};
	
	private TreeMap<String, DeclinationRule> nameDeclRules = new TreeMap<>();
	private ArrayListValuedHashMap<String, RulesNode> allSuffixes = new ArrayListValuedHashMap<>();
	
	public DeclinationRulesSet(File inputFile) throws IOException {
		this(new FileInputStream(inputFile.getCanonicalPath()));
	}
	
	public void addEnding(DeclinationRule rule, String ending, NounDeclination declination) {
		if (ending!=null) {
			allSuffixes.put(ending, RulesNode.builder().text(ending).rule(rule).declination(declination).build());
		}
	}
	
	public DeclinationRulesSet(InputStream inputStream) throws IOException {
		DeclinationRulesReader.load(inputStream, 
				(rule) -> { 
					nameDeclRules.put(rule.getSuffix(), rule);
					addEnding(rule, rule.getSuffix(), NounDeclination.M);
					addEnding(rule, rule.getD(), NounDeclination.D);
					addEnding(rule, rule.getC(), NounDeclination.C);
					addEnding(rule, rule.getB(), NounDeclination.B);
					addEnding(rule, rule.getN(), NounDeclination.N);
					addEnding(rule, rule.getMs(), NounDeclination.Ms);
					addEnding(rule, rule.getW(), NounDeclination.W);
				});
	}
	
	public DeclinationRule matchDeclinRule(final String name) {
		return nameDeclRules.entrySet().stream()
			.filter( i -> name.endsWith(i.getKey()))
			.map(i -> i.getValue())
			.findFirst()
			.orElse(null);
	}
	
	public void matchEndings(final String word, Consumer<RulesNode> consumer) {
		for(int i=1; i < word.length()-1; i++) {
			String ending = word.substring(i);
			allSuffixes.get(ending).stream()
				.filter(node -> node != null)
				.forEach(node -> {
					consumer.accept(node);
				});
		}
	}
	
	public List<PersonName> getProposedNames(final String word) {
		val result = new LinkedList<PersonName>();
		matchEndings(word, node -> {
			String mainForm = replaceSuffix(word, node.getText(), node.getRule().getSuffix());
			val personName = new PersonName(word, mainForm, node.declination);
			personName.setMale(node.getRule().isMan());
			result.add(personName);
		});
		if (result.size() == 0) {
			result.add(new PersonName(word));
		}
		return result; 
	}
	
	public static String replaceSuffix(String input, String suffix, String newSuffix) {
		int pos = input.lastIndexOf(suffix, input.length()-1);
		if (pos == -1) {
			return input;
		}
		return input.substring(0,pos) + newSuffix;
	}	
}
