package people.jobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import people.dict.NamesDictionary;
import people.dict.PhoneConst;
import people.dict.PhoneSplitList;
import people.dict.WordAnalyzer;
import people.dict.model.PersonName;

public class LastNamesSyllableSplit {

	static class EndingData {
		
		public EndingData(final String input) {
			this.ending = input;
			this.examples = new LinkedList<String>(); 
		}
		
		public String ending;
		
		public int count = 0; 
		
		public List<String> examples;
		
		public EndingData add(final String example) {
			++count;
			examples.add(example);
			return this; 
		}
		
		public int getCount() {
			return count; 
		}
	}
	
	private static Map<Boolean, Map<String, EndingData>> endings = new TreeMap<>();
	private static Map<String, PersonName> dict; 
	
	private static void loadFromFile() {
		
		try {
			dict = new HashMap<String, PersonName>();
			
			Files.lines(Paths.get("D:/dev/workspace/people.graph/nazwiska.log"))
				.distinct()
				.forEach( (p) -> {
					if (p!=null) {
						String[] s = p.split("\\s+");
						if (s != null)
							dict.put(s[0], new PersonName(s[0]));
					}
				} ); 
				
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void start() throws FileNotFoundException {
		
		loadFromFile();
		
		Iterator<Entry<String, PersonName>> iter = dict.entrySet().iterator();
		
		while (iter.hasNext()) {
			Entry<String, PersonName> item = iter.next();
			
			String name = item.getKey();
			String[] syllables = WordAnalyzer.splitWordIntoSyllable(name);
			
			if (syllables == null || syllables.length <= 0) {
				System.err.println(String.format("Can't split word '%s' into syllables.", name));
			} else {
				
				// get last syllable
				String ending = syllables[syllables.length-1];
				
				// get last vowel from earlier syllable (if any)
				if (syllables.length > 1) {
					String syllable2 = syllables[syllables.length-2];				
					if (syllable2.length() > 0) {
						PhoneSplitList list = new PhoneSplitList(syllable2);
						int last = list.size()-1;
						int count = 0;
						while (last>=0) {
							String s = list.get(last);
							if (count > 0 && !PhoneConst.isVowel(s)) {
								break; 
							}
							ending = s + ending;
							last--;
							count++;
						}
					}
				}
				EndingData data = endings.get(item.getValue().isMale()).get(ending);
				if (data == null) {
					data = new EndingData(ending);
					endings.get(item.getValue().isMale()).put(ending, data);
				}
				data.add(name);
			}
		}
		
		// print results
		PrintStream out = new PrintStream(new File("./output.txt"));
		for (boolean b : new Boolean[] {false, true}) {
			
			endings.get(b).values().stream()
				.sorted(Comparator.comparing(EndingData::getCount).reversed())
				.forEach(d -> 
				{
					out.println(String.format("%s - %d - %s", d.ending, d.count, d.examples.stream().limit(10).collect(Collectors.joining(", "))));
					
				});
			
		}
		
		
	}
	
	public static void main(String[] args) {
		
		endings.put(false, new TreeMap<String, EndingData> ());
		endings.put(true, new TreeMap<String, EndingData> ());
		try {
			start();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
