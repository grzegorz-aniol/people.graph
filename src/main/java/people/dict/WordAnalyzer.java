package people.dict;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.junit.experimental.theories.PotentialAssignment.CouldNotGenerateValueException;

import com.sun.jndi.toolkit.ctx.ComponentContext;
import com.sun.org.apache.xml.internal.utils.Hashtree2Node;

import people.dict.CharListExt.ListIteratorEx;

public class WordAnalyzer {

	private static final Set<String> COMPONENT_CONSONANTS = new HashSet<String>(Arrays.asList(new String[] {"sz", "rz", "cz", "ch", "dż", "dź"}));
	private static final Set<String> COMPONENT_VOWELS = new HashSet<String>(Arrays.asList(new String[] {"ea", "au"}));
	
	public static String[] splitWordIntoSyllable(final String word) {

		CharListExt list = new CharListExt(word);
		
		List<String> ret = new ArrayList<String>(word.length() + 1);
		StringBuilder sb = new StringBuilder();
		int cntVowelsLeft = list.getVowelsCount();

		// #1 Do not split words with only one or less vowels
		if (cntVowelsLeft <= 1) {
			return new String[] {word};
		}
		//
		
		boolean isPrevVowels = false;
		boolean hasSyllableVowel = false; 
		ListIteratorEx iter = list.listExtIterator();
		
		int i = 0;
		boolean contTwo = false;
		
		while (iter.hasNext()) {
			Character c = iter.next();
		
			boolean isVowel = iter.isVowel();
			
			boolean cont = false;
			boolean split = false; 
			boolean isComponentConsonant = false; // like sz, cz, rz, dź etc. 
			
			switch (i) {
				case 0:
					cont = true;
					break;
					
				default:
					
					if (contTwo) {
						contTwo = false;
						cont = true;
						isComponentConsonant = false; 
						break; 
					}
					
					// continue if there is not any vowel left
					cont |= (!isVowel && cntVowelsLeft == 0);
					
					//  check component consonant 
					isComponentConsonant = (iter.getNextCharsCount() >= 2 && COMPONENT_CONSONANTS.contains(iter.getChars(2)));

					// #6 - always split if a consonants is between two vowels
					split = (i>0 && isPrevVowels && !isVowel && iter.hasNext() && (iter.isNextVowel() || isComponentConsonant));
					if (split) {
						break; 
					}
					
					// #2 continue if 2 characters are component consonant (e.g. rz, ch, sz, cz)
					cont |= isComponentConsonant;
					if (cont) {
						contTwo = true;
						break; 
					}
					
					// #8 - continue for two consecutive vowels like: au, eu (e.g. hy - drau -lik, Eu - ro - pa)
					cont |= (iter.getNextCharsCount() >= 2 && COMPONENT_VOWELS.contains(iter.getChars(2)));
					if (cont) {
						contTwo = true;
						break;
					}
					
					// #5 two same consecutive consonants need to be break
					split |= (!isVowel && iter.hasNext() && iter.getNext()==c);
					
					// Split is there is already vowel and then are two consonants
					split |= (hasSyllableVowel && !isVowel && !isPrevVowels);
					
					if (split) break;

					
					// #3 continue if there is only 2 characters left (or less)
					cont |= (iter.getNextCharsCount() <= 2);
					
					// #4 - At least one vowel is required in the syllable
					cont |= !hasSyllableVowel;
					
					if (cont) break;
					
					// #7 - two consecutive consonants can be brake between or before them 
					split = (hasSyllableVowel && !isPrevVowels && !isVowel);
					
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
			
			sb.append(c.charValue());
			isPrevVowels = isVowel;
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
