package people.dict;

import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import people.graph.NamesDictionary;

public class CheckFirstNameEndingsMain {

	class EndingData {
		
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
	
	public void start() {
		
		
		Map<String, FirstName> dict = NamesDictionary.getNamesDict();
		
		Iterator<Entry<String, FirstName>> iter = dict.entrySet().iterator();
		
		while (iter.hasNext()) {
			Entry<String, FirstName> item = iter.next();
			
			String firstName = item.getKey();
			String[] syllables = WordAnalyzer.splitWordIntoSyllable(firstName);
			
			if (syllables == null || syllables.length <= 0) {
				System.err.println(String.format("Can't split word '%s' into syllables.", firstName));
			} else {
				
				// get last syllable
				String ending = syllables[syllables.length-1];
				
				// get last vowel from earlier syllable (if any)
				if (syllables.length > 1) {
					String syllable2 = syllables[syllables.length-2];
					if (syllable2.length() > 0) {
						String v2 = syllable2.substring(syllable2.length()-1);
						if (PhoneConst.isVowel(v2)) {
							ending = v2 + ending; 
						}
					}
				}
				EndingData data = endings.get(item.getValue().sex).get(ending);
				if (data == null) {
					data = new EndingData(ending);
					endings.get(item.getValue().sex).put(ending, data);
				}
				data.add(firstName);
			}
		}
		
		// print results
		for (boolean b : new Boolean[] {false, true}) {
			
			endings.get(b).values().stream()
				.sorted(Comparator.comparing(EndingData::getCount).reversed())
				.forEach(d -> 
				{
					System.out.println(String.format("%s - %d - %s", d.ending, d.count, d.examples.stream().limit(10).collect(Collectors.joining(", "))));
					
				});
			
//			Iterator<String> keyIter = endings.get(b).keySet().iterator();
//			while (keyIter.hasNext()) {
//				String key = keyIter.next();
//				EndingData value = endings.get(b).get(key);
//				
//				System.out.println(String.format("%s - %d - %s", key, value.count, value.examples.stream().limit(10).collect(Collectors.joining(", "))));
//			}
		}
		
		
	}
	
	public static void main(String[] args) {
		
		endings.put(false, new TreeMap<String, EndingData> ());
		endings.put(true, new TreeMap<String, EndingData> ());
		
		try {
			NamesDictionary.loadNamesFromResource();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		new CheckFirstNameEndingsMain().start();
	}
}
