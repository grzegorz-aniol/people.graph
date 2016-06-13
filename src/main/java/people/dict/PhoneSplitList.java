package people.dict;

import java.util.ArrayList;
import java.util.ListIterator;

public class PhoneSplitList extends ArrayList<String> {

	private static final long serialVersionUID = -7844116025378762427L;

	private int countVowels = 0;
	
	public PhoneSplitList(final String input) {
		
		// 1. Do not split words with only one or less vowels
		for (int i = 0; i < input.length(); ++i) {
			
			if (i+1 < input.length()) {
				
				String substr2 = input.substring(i, i+2);
				
				boolean isComponentConsonant = PhoneConst.isComponentConsonant(substr2);
				if (isComponentConsonant) {
					this.add(substr2);
					++i;
					continue; 
				}
				
				boolean isComponentVowels = PhoneConst.isComponentVowels(substr2);
				if (isComponentVowels) {
					this.add(substr2);
					++i;
					continue; 
				}				
			}
			
			String substr1 = input.substring(i,i+1);
			boolean isVowel = PhoneConst.isVowel(substr1);
			if (isVowel) {
				++countVowels;
			}
			
			this.add(substr1);
		}
		
	}
	
	public int getVowelsCount() {
		return this.countVowels;
	}
	
	public class ListIteratorEx implements ListIterator<String> {

		private int pos;
		
		public ListIteratorEx() {
			this.pos = -1;
		}
		
		public int getNextCharsCount() {
			return (PhoneSplitList.this.size() - pos);
		}
		
//		public String getChars(int num) {
//			if (num <=0) {
//				throw new IllegalArgumentException();
//			}
//			if (pos+num > PhoneSplitList.this.size()) {
//				throw new IllegalArgumentException();
//			}
//			return PhoneSplitList.this.
//		}
		
		public int getPosition() {
			return pos; 
		}
		
		@Override
		public boolean hasNext() {
			return (pos + 1 < PhoneSplitList.this.size());
		}

		@Override
		public String next() {
			++pos; 
			return PhoneSplitList.this.get(pos);
		}
		
		public String getNext() {
			if (hasNext()) {
				return PhoneSplitList.this.get(pos+1);
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean hasPrevious() {
			return (pos > 0);
		}

		@Override
		public String previous() {
			if (pos == 0) {
				throw new IllegalStateException();
			}
			--pos;
			return PhoneSplitList.this.get(pos);
		}
		
		public String getPrevious() {
			if (pos > 0) {
				return PhoneSplitList.this.get(pos-1);
			}
			throw new IllegalStateException();
		}

		@Override
		public int nextIndex() {
			return pos + 1;
		}

		@Override
		public int previousIndex() {
			if (pos <= 0) {
				return -1; 
			}
			return pos - 1;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void set(String e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(String e) {
			throw new UnsupportedOperationException();
		}
		
		public boolean isVowel() {
			if (pos >= PhoneSplitList.this.size()) {
				throw new IllegalArgumentException();
			}
			final String item = PhoneSplitList.this.get(pos);
			return PhoneConst.isVowel(item);
		}
		
		public boolean isNextVowel() {
			if (hasNext()) {
				final String item = PhoneSplitList.this.get(pos+1);
				return PhoneConst.isVowel(item);
			}
			return false;
		}

	}
	
	public ListIteratorEx listExtIterator() {
		return new ListIteratorEx();
	}
	
}
