package people.dict;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CheckFileLog {

	@Test
	public void testLogFileChange() throws Exception {
		Logger log = LoggerFactory.getLogger("PEOPLE");
		String key = UUID.randomUUID().toString();
		log.info(key);
		
		try (Stream<String> stream = Files.lines(Paths.get("people.log"))) {
			stream.filter(line -> line.contains(key))
				.findFirst()
				.orElseThrow(() -> new Exception("Can't find log change"));
		}
		
	}
}
