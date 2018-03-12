package people.dict.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Person {
		
	private String id;
	
	private String firstName;
		
	private String secondName;
	
	private String lastName;
	
	private String title;
	
	private Gender gender;

	@Override
	public String toString() {
		return firstName +
				(secondName != null ? " " + secondName : "")
				+ " "
				+ lastName;
	} 
	
}
