package people.dict;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PhoneConst {

	static final Set<String> COMPONENT_CONSONANTS = new HashSet<String>(Arrays.asList(new String[] {"sz", "rz", "cz", "ch", "dż", "dź"}));
	
	static final Set<String> COMPONENT_VOWELS = new HashSet<String>(Arrays.asList(new String[] {"ea", "au"}));
	
	public static final String VOWELS = " aąAĄeęEĘuUiIoOóÓyY";
	
	public static final boolean isComponentConsonant(final String inputChars) {
		return (inputChars != null && inputChars.length()==2 && PhoneConst.COMPONENT_CONSONANTS.contains(inputChars.toLowerCase()));
	}
	
	public static final boolean isComponentVowels(final String inputChars) {
		return (inputChars != null && inputChars.length()==2 && PhoneConst.COMPONENT_VOWELS.contains(inputChars.toLowerCase()));
	}
	
	public static final boolean isVowel(final String inputChar) {
		return (inputChar != null && inputChar.length()==1 && (PhoneConst.VOWELS.indexOf(inputChar.charAt(0), 0) != -1));
	}

}
