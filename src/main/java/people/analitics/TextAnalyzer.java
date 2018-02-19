package people.analitics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.val;
import org.apache.commons.lang.StringUtils;

public class TextAnalyzer {
	
	private String text; 
	
	private int currentPos = 0;
	
	private static Pattern wordSeparators = Pattern.compile(
			"\\w+(\\-\\w+)?",
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
		Matcher matcher = wordSeparators.matcher(str);
		while (matcher.find()) {
			String w = matcher.group();
			if (w != null && StringUtils.isNotBlank(w)) {
				result.add(WordText.builder()
					.text(w)
					.build());
			}
		}

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
