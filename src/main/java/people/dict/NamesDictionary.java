package people.dict;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import people.dict.model.PersonName;

public class NamesDictionary {

	private ArrayListValuedHashMap<String, PersonName> mapNames = new ArrayListValuedHashMap<>();
	
	@Getter @Setter
	public static class FileRecordEntry {
		private String name;
		private boolean sex; 
	}

	public void loadNamesFromResource() throws IOException {
		
		mapNames.clear();
		
       final CellProcessor[] processors = new CellProcessor[] { 
                new NotNull(), // name
                new ParseBool() // sex 
       };
       
       try (CsvBeanReader reader = new CsvBeanReader(new InputStreamReader(NamesDictionary.class.getResourceAsStream("/first_names.csv")), CsvPreference.STANDARD_PREFERENCE)) {
    	   final String[] header = reader.getHeader(true);
    	   
    	   FileRecordEntry record;
    	   while ( ( record = reader.read(FileRecordEntry.class, header, processors)) != null ) {
    		   PersonName p = new PersonName(record.getName());
    		   mapNames.put(p.getText(), p);
    	   }    	   
       }
       
	}
	
	public void loadFromFile(String filePath) throws IOException {
		WordDictionaryReader reader = new WordDictionaryReader();
		reader.load(filePath, (name) -> {
			mapNames.put(name.getText(), name);
		});
	}
	
	public boolean contains(final String text) {
		return (mapNames != null && mapNames.containsKey(text));
	}
	
	public PersonName getFirst(final String name) {
		return Optional.ofNullable(mapNames.get(name))
				.filter( list -> list.size() > 0 )
				.map( list -> list.iterator().next() )
				.orElse(null);
	}
	
	public List<PersonName> get(final String name) {
		List<PersonName> list = mapNames.get(name);
		if (list != null && list.size() > 0) {
			return list.stream()
					.map(d -> {
						val p = new PersonName(d.getText(), d.findMainForm(), d.getNounDeclination());
						p.setMale(d.isMale());
						return p;
					} )
					.collect(Collectors.toList());
		}
		return null;
	}
	
	public String findPerson(final String text) {
		Matcher m = regexpPerson2.matcher(text);
		if (m.find()) {
			if (contains(m.group(1))) {
				return m.group();
			}
		}
		return null;
	}

	public static Pattern regexpPerson2 = Pattern.compile(
			"([\\p{Upper}][\\p{Lower}]+) ([\\p{Upper}][\\p{Lower}]+)(?:\\-([\\p{Upper}][\\p{Lower}]+))?",
			Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);
	
	public static Pattern regexpSentences = Pattern.compile("\\.[^\\.]",
	Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);
		
	public Stream<String> stream() {
		return mapNames.keySet().stream();
	}
	
}
