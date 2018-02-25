package people.analitics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class WordText extends AbstractText {

	public WordText(final String s) {
		this.text = s;
	}
}
