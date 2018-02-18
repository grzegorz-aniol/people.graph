package people.dict.model;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class Word implements Comparable<Word> {

	public static class DefaultComparator implements Comparator<PersonName> {
	
		private Collator coll;
	
		public DefaultComparator() {
			coll = Collator.getInstance(Locale.getDefault());
			coll.setStrength(Collator.PRIMARY);
		}
		
		@Override
		public int compare(PersonName o1, PersonName o2) {
			return coll.compare(o1.getText(), o2.getText());
		}
		
	}

	protected String text;
	
	protected WordRef ref = null;
	
	protected SpeechPart speechPart; 
	
	protected NounDeclination nounDeclination = NounDeclination.UNKNOWN;
	
	public Word(String text) {
		this.text = text;
	}

	public Word(String text, SpeechPart speechPart, NounDeclination decl) {
		this.text = text;
		this.nounDeclination = decl;
		this.speechPart = speechPart; 
	}

	@Override
	public int compareTo(Word o) {
		if (text == null && o == null) {
			return 0;
		}
		if (o == null) {
			return 1;
		}
		return text.compareTo(o.getText());
	}
	
	
}
