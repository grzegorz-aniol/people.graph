package people.dict;

import java.util.ArrayList;
import java.util.ListIterator;

public class CharListExt extends ArrayList<Character> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7844116025378762427L;

	private static final String VOWELS = " aąAĄeęEĘuUiIoOóÓyY";

	private int length = -1;
	private int countVowels = 0;
	
	private String input;
	
	public CharListExt(final String input) {
		this.input = input; 
		this.length = input.length();
		
		// 1. Do not split words with only one or less vowels
		for (int i = 0; i < input.length(); ++i) {
			boolean isVowel = (VOWELS.indexOf(input.charAt(i), 0) != -1);
			if (isVowel) {
				++countVowels;
			}
		}		
	}
	
	public int getLength() {
		return this.length; 
	}
	
	public int getVowelsCount() {
		return this.countVowels;
	}
	
	public class ListIteratorEx implements ListIterator<Character> {

		private int pos;
		private String data;
		
		public ListIteratorEx(final String data) {
			this.pos = -1;
			this.data = data;
		}
		
		public int getNextCharsCount() {
			return (length - pos);
		}
		
		public String getChars(int num) {
			if (num <=0) {
				throw new IllegalArgumentException();
			}
			if (pos+num > length) {
				throw new IllegalArgumentException();
			}
			return data.substring(pos, pos+num);
		}
		
		public int getPosition() {
			return pos; 
		}
		
		@Override
		public boolean hasNext() {
			return (pos + 1 < length);
		}

		@Override
		public Character next() {
			++pos; 
			return new Character(data.charAt(pos));
		}
		
		public Character getNext() {
			if (hasNext()) {
				return data.charAt(pos+1);
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean hasPrevious() {
			return (pos > 0);
		}

		@Override
		public Character previous() {
			if (pos == 0) {
				throw new IllegalStateException();
			}
			--pos;
			return new Character(data.charAt(pos));
		}
		
		public Character getPrevious() {
			if (pos > 0) {
				return data.charAt(pos-1);
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
		public void set(Character e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(Character e) {
			throw new UnsupportedOperationException();
		}
		
		public boolean isVowel() {
			if (pos >= length) {
				throw new IllegalArgumentException();
			}
			return (VOWELS.indexOf(data.charAt(pos), 0) != -1);
		}
		
		public boolean isNextVowel() {
			if (hasNext()) {
				return (VOWELS.indexOf(data.charAt(pos+1), 0) != -1);
			}
			return false;
		}
		
	}
	
	public ListIteratorEx listExtIterator() {
		return new ListIteratorEx(this.input);
	}
	
}
