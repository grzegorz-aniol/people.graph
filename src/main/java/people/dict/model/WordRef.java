package people.dict.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder @NoArgsConstructor
public class WordRef {

	private Word next;
	
	public WordRef(Word next) {
		this.next = next; 
	}
	
}
