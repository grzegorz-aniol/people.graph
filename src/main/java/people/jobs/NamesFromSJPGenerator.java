package people.jobs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import people.dict.NamesDictionary;
import people.dict.WordDictionaryWriter;
import people.dict.model.PersonName;
import people.dict.model.NounDeclination;
import people.dict.model.WordRef;

/**
 * Filter Polish dictionary published by SJP.pl and filter all first names
 * 
 */
public class NamesFromSJPGenerator {
	
	private static NamesDictionary dict = new NamesDictionary();
	
	public static void main(final String[] args) {
		System.out.println("Processing...");
		try {
			dict.loadNamesFromResource();
			WordDictionaryWriter output = new WordDictionaryWriter("./names_fulldict2.csv");
			readSPJDict("d:/databases/aspell/odm.txt", output);
			output.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		System.out.println("Done.");
	}
	
	private static void readSPJDict(String filePath, WordDictionaryWriter output) throws IOException {
		//read file into stream, try-with-resources
		try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
			stream.forEach(line -> {
				String[] args = line.split(", ?");
				if (args != null && args[0] != null) {
					PersonName name = dict.getFirst(args[0]);
					if (name != null) {
						PersonName mainForm = generateMainForm(name.getText(), name.isMale());
						output.accept(mainForm);
						for (int i=1; i < args.length; ++i) {
							output.accept(generateDeclinedForm(mainForm, args[i]));
						}
					}
				}
			});
		}		
	}
	
	private static PersonName generateMainForm(String text, boolean isMale) {
		PersonName fn = new PersonName(text, NounDeclination.M);
		fn.setMale(isMale);
		return fn; 
	}
	
	private static PersonName generateDeclinedForm(PersonName mainForm, String text) {
		PersonName fn = new PersonName(text, NounDeclination.UNKNOWN);
		fn.setMale(mainForm.isMale());
		fn.setRef(new WordRef(mainForm));
		return fn; 
	}	
	
}
