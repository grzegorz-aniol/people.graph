package people.dict;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

public class DeclinationRulesReader {

    private final static CellProcessor[] cvsCellProcessor = new CellProcessor[] { 
            new NotNull(), // word suffix
            new ParseBool(),// sex (is man)
            new Optional(), // D 
            new Optional(), // C 
            new Optional(), // B
            new Optional(), // N
            new Optional(), // M
            new Optional(), // W
    };	

	public static void load(final InputStream inputStream, Consumer<DeclinationRule> output) throws IOException {
		
	       try(CsvBeanReader reader = new CsvBeanReader(new InputStreamReader(inputStream, "UTF-8"), CsvPreference.STANDARD_PREFERENCE)) {
	    	   final String[] header = reader.getHeader(true);
	    	   
	    	   DeclinationRule nameRecord = null;
	    	   do {
	    		   nameRecord = reader.read(DeclinationRule.class, header, cvsCellProcessor);
	    		   if (nameRecord == null) {
	    			   break;
	    		   }
	    		   
	    		   output.accept(nameRecord);
	    		   
	    	   } while (nameRecord != null);	    	   
	       }		
	}
    
	public static void load(final String fileName, Consumer<DeclinationRule> output) throws IOException {
		load(new FileInputStream(fileName), output);
	}	
	
}
