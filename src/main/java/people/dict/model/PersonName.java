package people.dict.model;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PersonName extends Word {

	private boolean male;
	
	private String name; 

	public PersonName() {
	}
	
	public PersonName(String text) {
		super(text, SpeechPart.NOUN, NounDeclination.UNKNOWN);
		this.name = text; 
	}
	
	public PersonName(String text, NounDeclination nounForm) {
		super(text, SpeechPart.NOUN, nounForm);
		this.name = text; 
	}	
	
	public PersonName(String text, String name, NounDeclination nounForm) {
		super(text, SpeechPart.NOUN, nounForm);
		this.name = name; 
	}	

	@Override
	public String toString() {
		return name + " [" + super.text + "]";
	}
	
	public String findMainForm() {
		if (this.nounDeclination != null && this.nounDeclination.equals(NounDeclination.M)) {
			return this.text;
		}
		if (StringUtils.isNotBlank(this.name)) {
			return this.name; 
		}
		return this.text; 
	}

}
