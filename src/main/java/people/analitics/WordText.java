package people.analitics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor 
@Builder
@NoArgsConstructor
public class WordText {
	
	private String text;
	
	private int wordPos; 
	
	private int startPos;
	
	private int length;
	
	private String separatorBefore;
	
	private String separatorAfter; 
	
	public boolean startsWithUpperCase() {
		return (text != null && text.length() > 0 && Character.isUpperCase(text.charAt(0)));
	}

	@Override
	public String toString() {
		return text; 
	}
	
}
