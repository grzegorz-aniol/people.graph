package people.jobs;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import people.dict.DeclinationRulesReader;
import people.dict.DeclinationRulesSet;
import people.dict.NamesDictionary;
import people.dict.WordDictionaryWriter;
import people.dict.model.DeclinationRule;
import people.dict.model.PersonName;
import people.dict.model.NounDeclination;
import people.dict.model.Word;
import people.dict.model.WordRef;

/**
 * Generate full dictionary of Polish first names including all declination forms
 * 
 * @author gangel
 *
 */
public class NamesDeclinationGenerator {

    private final static CellProcessor[] cvsCellProcessor = new CellProcessor[] { 
            new NotNull(), // word suffix
            new ParseBool(),// sex (is man)
            new NotNull(), // D 
            new NotNull(), // C 
            new NotNull(), // B
            new NotNull(), // N
            new NotNull(), // M
            new NotNull(), // W
    };
    
	private static DeclinationRulesSet nameDeclRules;
	
	private static NamesDictionary dict = new NamesDictionary();
	
	public static void main(final String[] args) {
		
		try {
			dict.loadNamesFromResource();
		} catch (IOException e1) {
			System.err.println("Can't load names dictionary. Error: " + e1.getMessage());
			return; 
		}		
		
		try {
			loadRulesCsvFile();
		} catch (IOException e) {
			System.err.println("Can't load names declination rule file. Error: " + e.getMessage());
			return; 
		}
		
		List<PersonName> allNames = new LinkedList<>(); 
		fillDictionary((n)->allNames.add(n));
		
		Collections.sort(allNames, new Word.DefaultComparator());
		
		try (WordDictionaryWriter outputNames = new WordDictionaryWriter("./names_fulldict.csv")) {
			allNames.stream().forEach(outputNames);
			
		} catch (Exception e) {
			System.err.println("Error during exporting names to the new dictionary file. " + e.getMessage());
			return; 
		}
	}

	private static PersonName generateDeclinedForm(PersonName mainForm, String text, 
			String suffix, String formSuffix, NounDeclination decl) {
		PersonName fn = new PersonName(text, DeclinationRulesSet.replaceSuffix(text, suffix, formSuffix), decl);
		fn.setGender(mainForm.getGender());
		fn.setRef(new WordRef(mainForm));
//		System.out.print(fn.getName()+",");
		return fn; 
	}
	
	private static PersonName createNamesWithDeclination(String text, DeclinationRule declRule, Consumer<PersonName> output) {
		PersonName fn = new PersonName(text, NounDeclination.M);
		output.accept(fn);
		//System.out.print(text+",");
		
		if (declRule != null) {
			output.accept(generateDeclinedForm(fn, text, declRule.getSuffix(), declRule.getD(), NounDeclination.D));
			output.accept(generateDeclinedForm(fn, text, declRule.getSuffix(), declRule.getC(), NounDeclination.C));
			output.accept(generateDeclinedForm(fn, text, declRule.getSuffix(), declRule.getB(), NounDeclination.B));
			output.accept(generateDeclinedForm(fn, text, declRule.getSuffix(), declRule.getN(), NounDeclination.N));
			output.accept(generateDeclinedForm(fn, text, declRule.getSuffix(), declRule.getMs(), NounDeclination.Ms));
		}
		
		return fn;
	}

	private static void fillDictionary(Consumer<PersonName> output) {

		final AtomicLong positive = new AtomicLong(0);
		final AtomicLong negative = new AtomicLong(0);
		
		dict.stream()
			.forEach(name -> {
				DeclinationRule rule = nameDeclRules.matchDeclinRule(name);
				createNamesWithDeclination(name, rule, output);
				if (rule != null) {
					positive.incrementAndGet();
				} else {
					System.out.println(name);
					negative.incrementAndGet();
				}
			});
		
		System.out.println("Names declined: " + positive.get());
		System.out.println("Names not declined: " + negative.get());
	}

	public static void loadRulesCsvFile() throws IOException {
	       
		nameDeclRules = new DeclinationRulesSet(NamesDictionary.class.getResourceAsStream("/names_declination_rules.csv"));

	}
}
