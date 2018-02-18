package people.dict;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.assertj.core.util.Arrays;
import org.codehaus.plexus.util.StringUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import lombok.Getter;
import lombok.Setter;
import people.dict.model.DeclinationRule;
import people.dict.model.PersonName;
import people.dict.model.NounDeclination;
import people.dict.model.SpeechPart;
import people.dict.model.WordRef;

public class WordDictionaryReader {

	@Getter @Setter	
	public static class WordDictRecord {
		String NAME;
		Boolean SEX;
		String SPEECH;
		String DECLINATION;
		String REFERENCE; 
	}
	
	public final static CellProcessor[] processors = new CellProcessor[] { 
			new NotNull(), // name
			new ParseBool(), // sex
			new NotNull(), //SPEECH,
			new NotNull(), //DECLINATION,
			new Optional()  // REFERENCE
	};
	
	public void load(final String fileName, Consumer<PersonName> output) throws IOException {
	       
	       final CsvBeanReader reader = new CsvBeanReader(new InputStreamReader(new FileInputStream(fileName)), CsvPreference.STANDARD_PREFERENCE);
	       final String[] header = reader.getHeader(true);

	       final List<NounDeclination> EMPTY_DECLINATION = new ArrayList<NounDeclination>(1);
	       EMPTY_DECLINATION.add(NounDeclination.UNKNOWN);
	       
	       WordDictRecord nameRecord = null;
	       PersonName name, prevMForm = null; 
	       do {
	    	   nameRecord = reader.read(WordDictRecord.class, header, processors);
	    	   if (nameRecord == null) {
	    		   break;
	    	   }
	    	   
	    	   List<NounDeclination> declList = EMPTY_DECLINATION;
	    	   if (StringUtils.isNotBlank(nameRecord.DECLINATION)) {
	    		   String[] declArray = nameRecord.DECLINATION.split("\\:");
	    		   declList = new ArrayList<NounDeclination>(declArray.length);
	    		   for (String s : declArray) {
	    			   declList.add(NounDeclination.valueOf(s));	    			   
	    		   }
	    	   } 
			
	    	   for (NounDeclination decl : declList) {
	    		   name = new PersonName(nameRecord.NAME);
	    		   name.setName(nameRecord.REFERENCE);
	    		   name.setMale(nameRecord.SEX);
	    		   name.setSpeechPart(SpeechPart.NOUN);
	    		   name.setNounDeclination(decl);
	    		   output.accept(name);
	    	   }
	    	   
	       } while (nameRecord != null);
	       
	}	
	
}
