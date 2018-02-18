package people.analitics;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
public class SentenceText {
	
	private LinkedList<WordText> content = new LinkedList<WordText>(); 
	private ArrayList<WordText> tabContent = new ArrayList<WordText>(); 
	
	public ListIterator<WordText> getIterator() {
		return content.listIterator();
	}

	public void add(WordText word) {
		content.add(word);
		tabContent.add(word);
	}
	
	public WordText get(int index) {
		if (index >= tabContent.size()) {
			return null;
		}
		return tabContent.get(index);
	}
	
	public int size() {
		return content.size();
	}
	
}
