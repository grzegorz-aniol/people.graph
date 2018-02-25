package people.analitics;

import java.util.ArrayList;
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

	private static class Position {
		public int pos;
		public int len;
		public Position(int p, int l) {
			this.pos = p;
			this.len = l;
		}
	}

	public TextAnalyzer(final String t) {
		this.text = t;
	}
	
	private SentencePart buildSentencePart(int startPos, int endPos) {
		if (text == null) {
			return null;
		}
		String str = text.substring(startPos, endPos);
		final val result = new SentencePart();
		Matcher matcher = wordSeparators.matcher(str);
		while (matcher.find()) {
			String w = matcher.group();
			if (w != null && StringUtils.isNotBlank(w)) {
				val wt = new WordText(w);
				wt.setTotalStartPos(startPos + matcher.start());
				result.add(wt);
			}
		}

		if (result.size() == 0) {
			return null;
		}
		return result;
	}

	private static Position findPart(final String str, int startPos,
									 final String breakSeparator) {
		if (str == null || startPos >= str.length()) {
			return null;
		}

		for (int i=startPos; i < str.length(); ++i) {
			char c = str.charAt(i);
			if (-1 != breakSeparator.indexOf(c)){
				return new Position(startPos, i - startPos);
			}
		}

		return new Position(startPos, str.length() - startPos);
	}
	
	public Sentence getNextSentence() {

		Position pos = findPart(text, currentPos, ".");
		if (pos == null) {
			return null;
		}
		currentPos = pos.pos + pos.len + 1;

		val sentence = new Sentence();
		val parts = new ArrayList<AbstractSentence>(2);

		int idx = pos.pos;
		while (idx < pos.pos + pos.len) {
			Position subPos = findPart(text, idx, ",;");
			if (subPos == null) {
				break;
			}
			val part = buildSentencePart(subPos.pos, subPos.pos + subPos.len);
			if (part != null) {
				parts.add(part);
			}
			idx = subPos.pos + subPos.len + 1;
		}
		sentence.setParts(parts);
		return sentence;
	}
}
