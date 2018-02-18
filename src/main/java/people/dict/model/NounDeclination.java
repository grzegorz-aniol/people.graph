package people.dict.model;

public enum NounDeclination {

	UNKNOWN("nieznane"),
	M("miejscownik"),
	D("dopełniacz"),
	C("celownik"),
	B("biernik"),
	N("narzędnik"),
	Ms("miejscownik"),
	W("wołacz");
	
	NounDeclination(final String d) {
		this.desc = d;
	}
	
	private String desc;
	
	public String getDescription() {
		return desc; 
	}
	
}
