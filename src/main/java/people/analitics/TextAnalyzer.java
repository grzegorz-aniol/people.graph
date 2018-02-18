package people.analitics;

import java.util.regex.Pattern;

import lombok.val;

public class TextAnalyzer {
	
	private String text; 
	
	private int currentPos = 0;
	
	private static Pattern wordSeparators = Pattern.compile(
			"\\W+",
			Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE);	
	
	public TextAnalyzer(final String t) {
		this.text = t;
	}
	
	private SentenceText buildSentence(int startPos, int endPos) {
		if (text == null) {
			return null;
		}
		String str = text.substring(startPos, endPos);
		final val result = new SentenceText(); 
		wordSeparators.splitAsStream(str)
		    .filter(w -> w != null && w.length() > 0)
			.forEach(w -> {
				result.add(WordText.builder()
							.text(w)
							.build());
			});
		
		if (result.size() == 0) {
			return null;
		}
		return result;
	}
	
	public SentenceText getNextSentence() {
		if (text == null || currentPos >= text.length()) {
			return null;
		}
		int startPos = currentPos;
		int endPos = text.indexOf(".", currentPos);
		if (endPos < 0) {
			currentPos = text.length()+1;
			return buildSentence(startPos, text.length());
		}
		currentPos = endPos+1;
		return buildSentence(startPos, endPos);
	}
}
