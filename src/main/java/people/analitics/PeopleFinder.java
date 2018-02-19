package people.analitics;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import people.dict.DeclinationRulesSet;
import people.dict.NamesDictionary;
import people.dict.model.Gender;
import people.dict.model.NounDeclination;
import people.dict.model.Person;
import people.dict.model.PersonName;

public class PeopleFinder {

	private NamesDictionary namesDict;
	
	private DeclinationRulesSet lastNameDeclRules;
	
	private Context context;
	
	@Getter @Setter
	private static class Context {
		Boolean male = null;
		NounDeclination declination = NounDeclination.UNKNOWN;
		PersonName firstName;
		PersonName secondName; 
		PersonName lastName;
		Position position; 
		
		public Context(Position p) {
			this.position = p;
		}
	};
	
	@Getter @Setter 
	private static class Position {
		 ListIterator<WordText> iterator; 
		 public Position(ListIterator<WordText> i) {
			 this.iterator = i;
		 }
	}

	private static <T> boolean matchCases(T value1, T value2, T exceptionCase) {
		if (value1 == null || value2 == null) {
			return false;
		}
		if (value1.equals(value2)) {
			return true;
		}
		if (value1.equals(exceptionCase) || value2.equals(exceptionCase)) {
			return true;
		}
		return false;
	}

	public PeopleFinder(NamesDictionary dict, DeclinationRulesSet rules) {
		this.namesDict = dict; 
		this.lastNameDeclRules = rules;
	}
	
	private List<PersonName> isName(WordText word) {
		if (word.startsWithUpperCase()) {
			return namesDict.get(word.getText());
		}
		return null;
	}
	
	private List<PersonName> isLastName(WordText word, Gender knownGender) {
		if (word.startsWithUpperCase()) {
			String[] parts = word.getText().split("-");
			if (parts.length > 2) {
				return null;
			}
			if (parts.length > 1 && !Character.isUpperCase(parts[1].charAt(0))) {					
				return null;
			}
			return matchLastName(parts[0], parts.length > 1 ? parts[1] : null, knownGender);
		}
		
		return null;
	}
	
	private Person createPerson(List<PersonName> firstNames, List<PersonName> secondNames, List<PersonName> lastNames) {
		
		if (firstNames == null | lastNames == null || firstNames.size() == 0 || lastNames.size() == 0) {
			return null; 
		}
		
		if (firstNames.size() == 1 && (secondNames==null || secondNames.size()==1) && lastNames.size() == 1) {
			return Person.builder()
				.firstName(firstNames.get(0).findMainForm())
				.secondName(secondNames != null ? secondNames.get(0).findMainForm() : null)
				.lastName(lastNames.get(0).findMainForm())
				.gender(firstNames.get(0).getGender())
				.build();
		}

		Gender knownGender = (firstNames!=null && firstNames.size()==1 ? firstNames.get(0).getGender() : Gender.UNKNOWN);
		
		Iterator<PersonName> fnIter = firstNames.iterator();
		while (fnIter.hasNext()) {
			PersonName n1 = fnIter.next();
			
			PersonName l1 = lastNames.stream()
				.filter( p -> matchCases(p.getGender(), n1.getGender(), Gender.UNKNOWN))
				.filter( p -> matchCases(p.getNounDeclination(), n1.getNounDeclination(), NounDeclination.UNKNOWN) )
				.findFirst()
				.orElse(null);
				
			PersonName s1 = Optional.ofNullable(secondNames)
				.map(list -> list.stream()
					.filter( p -> matchCases(p.getGender(), n1.getGender(), Gender.UNKNOWN))
					.filter( p -> matchCases(p.getNounDeclination(), n1.getNounDeclination(), NounDeclination.UNKNOWN) )
					.findFirst()
					.orElse(null)
				)
				.orElse(null);
			
			if (n1 != null && l1 != null) {
				return Person.builder()
					.firstName(n1.findMainForm())
					.secondName(s1 != null ? s1.findMainForm() : null)
					.lastName(l1.findMainForm())
					.gender(n1.getGender())
					.build();
				
			}
		}
		
		return null;
	}
	
	public List<Person> identifyPeopleInText(final String text) {
		val textAnalyzer = new TextAnalyzer(text);
		
		val persons = new LinkedList<Person>();
		val foundPeople = new HashSet<String>();

		SentenceText sentence = textAnalyzer.getNextSentence();
		while (sentence != null) {
			ListIterator<WordText> iter = sentence.getIterator();
			
			Position pos = new Position(iter);
			Context ctx = new Context(pos);
			
			while (iter.hasNext()) {				
				WordText w = iter.next();
				List<PersonName> n1 = isName(w);
				Gender knownGender = (n1!=null && n1.size()==1 ? n1.get(0).getGender() : Gender.UNKNOWN);
				Person personFound = null;
				if (n1 != null) {
					
					if (!iter.hasNext()) {
						break;
					}
					WordText w2 = iter.next();
					List<PersonName> n2 = isName(w2);
					List<PersonName> ln2 = isLastName(w2, knownGender);
					boolean matchSecondName = (n2 != null);
					boolean matchLastName = (ln2 != null);
					
					// second word is not the name
					if (!matchSecondName && matchLastName) {
						personFound = createPerson(n1, null, ln2);
					} else if (matchSecondName && matchLastName) {						
						if (!iter.hasNext()) {
							personFound = createPerson(n1, null, ln2);
						} else {
							WordText w3 = iter.next();
							List<PersonName> ln3 = isLastName(w3, knownGender);
							if (ln3 != null) {
								personFound = createPerson(n1, n2, ln3);
							} else {
								personFound = createPerson(n1, null, ln2);
								// move back one word cause (3) is not last name
								iter.previous();
							}
						}
					}
				}
				
				if (personFound != null) {
					String personString = personFound.toString();
					if (!foundPeople.contains(personString)) {
						foundPeople.add(personString);
						persons.add(personFound);
					}
				}
			}
			
			sentence = textAnalyzer.getNextSentence();
		}
		
		return persons; 
	}
	
//	public List<Person> identifyPeopleInText(final String text) {
//		
//		val persons = new LinkedList<Person>();
//		val foundPeople = new HashSet<String>();
//		Matcher matcher = NamesDictionary.regexpPerson2.matcher(text);
//		while (matcher.find()) {
//			String firstNameCandidate = matcher.group(1);
//			PersonName firstNameObj = namesDict.getFirst(firstNameCandidate);
//			if (firstNameObj != null) {
//				String fullName = matcher.group();
//				if (!foundPeople.contains(fullName)) {
//					foundPeople.add(fullName);
//					
//					PersonBuilder builder = Person.builder();
//					
//					builder.firstName(firstNameObj.findMainForm());
//
//					String secondNameCandidate = matcher.group(2);
//					PersonName secondNameObj = namesDict.getFirst(secondNameCandidate);
//					
//					if (secondNameObj != null && matcher.group(3) != null) {
//						// if 2nd group is a name and there is still 3rd group then 2nd is second name and 3rd is last name
//						builder.secondName(secondNameObj.findMainForm());
//						
//						PersonName lastNameCandidate = matchLastName(matcher.group(3), null);
//						if (lastNameCandidate != null) {
//							builder.lastName(lastNameCandidate.getText());							
//						}
//						
//					} else {
//						PersonName lastNameCandidate = matchLastName(matcher.group(2), matcher.group(3));
//						if (lastNameCandidate != null) {
//							builder.lastName(lastNameCandidate.getText());							
//						}
//					}
//					
//					Person p = builder.build();
//					
//					persons.add(p);					
//				}				
//			}
//		}
//		return persons; 
//	}	
	
	private List<PersonName> matchLastName(final String lastNameText, final String lastName2Text, Gender knownGender) {
		if (lastNameText == null) {
			return null;
		}
		List<PersonName> lastName = lastNameDeclRules.getProposedNames(lastNameText, knownGender);
		if (lastName == null) {
			return null;
		}
		List<PersonName> lastName2 = (lastName2Text != null ? lastNameDeclRules.getProposedNames(lastName2Text, knownGender) : null);
		
		boolean isPart2 = (lastName2 != null && lastName2.size() > 0);
		
		val result = new LinkedList<PersonName>();
		
		Iterator<PersonName> iter1 = lastName.iterator();
		while (iter1.hasNext()) {
			PersonName item1 = iter1.next();
			
			PersonName n1;
			if (!isPart2) {
				n1 = new PersonName(item1.getText(), item1.getName(), item1.getNounDeclination());				
			} else {
				n1 = new PersonName(lastNameText + "-" + lastName2.get(0).getText(), item1.getName()+"-"+lastName2.get(0).getName(), item1.getNounDeclination());
			}
			n1.setGender(item1.getGender());

			result.add(n1);
		}
				
		if (result != null && result.size() > 0) {
			return result; 
		}
		return null; 
	}
	
}
