package people.dict;

import java.util.ArrayList;
import java.util.List;

import people.dict.PhoneSplitList.ListIteratorEx;

public class WordAnalyzer {

	public static String[] splitWordIntoSyllable(final String word) {

		PhoneSplitList list = new PhoneSplitList(word);
		
		List<String> ret = new ArrayList<String>(word.length() + 1);
		StringBuilder sb = new StringBuilder();
		int cntVowelsLeft = list.getVowelsCount();

		// #1 Do not split words with only one or less vowels
		if (cntVowelsLeft <= 1) {	
			return new String[] {word};
		}
		//
		
		boolean isPrevVowel = false;
		boolean hasSyllableVowel = false; 
		ListIteratorEx iter = list.listExtIterator();
		
		int i = 0;
		
		while (iter.hasNext()) {
			String str = iter.next();
		
			boolean isVowel = iter.isVowel();
			
			boolean cont = false;
			boolean split = false; 
			boolean isComponentConsonant = false; // like sz, cz, rz, dź etc. 
			
			switch (i) {
				case 0:
					cont = true;
					break;
					
				default:
					
					// continue if there is not any vowel left
					cont |= (!isVowel && cntVowelsLeft == 0);
					
					//  check component consonant 
					isComponentConsonant = PhoneConst.isComponentConsonant(str);

					// #6 - always split if a consonants is between two vowels
					split = (i>0 && isPrevVowel && !isVowel && iter.hasNext() && iter.isNextVowel());
					if (split) {
						break; 
					}
					
					// #2 continue if 2 characters are component consonant (e.g. rz, ch, sz, cz)
					cont |= isComponentConsonant;
					if (cont) {
						break; 
					}
					
					// #8 - continue for two consecutive vowels like: au, eu (e.g. hy - drau -lik, Eu - ro - pa)
					cont |= PhoneConst.isComponentVowels(str);
					if (cont) {
						break;
					}
					
					// #5 two same consecutive consonants need to be break
					split |= (!isVowel && iter.hasNext() && iter.getNext().compareToIgnoreCase(str)==0);
					
					// Split is there is already vowel and then are two consonants
					split |= (hasSyllableVowel && !isVowel && !isPrevVowel);

					if (split) break;

					
					// #3 continue if there is only 2 characters left (or less)
					cont |= (iter.getNextCharsCount() <= 2);
					
					// #4 - At least one vowel is required in the syllable
					cont |= !hasSyllableVowel;
					
					if (cont) break;
					
					// #7 - two consecutive consonants can be brake between or before them 
					split = (hasSyllableVowel && !isPrevVowel && !isVowel);
					
					if (!split) {
						cont = true;
					}
					
			}
			
			// split
			if (!cont || split) {
				ret.add(sb.toString());
				sb = new StringBuilder();
				hasSyllableVowel = false; 
			}
			
			sb.append(str);
			isPrevVowel = isVowel;
			hasSyllableVowel |= isVowel; 
			++i;
			if (isVowel) {
				--cntVowelsLeft;
			}
			
		}
		
		if (sb.length() > 0) {
			ret.add(sb.toString());
		}
		
		return ret.toArray(new String[] {} );
	}
	
	
}
