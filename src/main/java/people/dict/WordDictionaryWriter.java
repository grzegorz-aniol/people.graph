package people.dict;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Optional;
import java.util.function.Consumer;

import lombok.SneakyThrows;
import people.dict.model.Gender;
import people.dict.model.PersonName;
import people.dict.model.Word;
import people.dict.model.WordRef;

public class WordDictionaryWriter implements Consumer<PersonName>, AutoCloseable{

	private OutputStreamWriter writer;

	public WordDictionaryWriter(String fileName) throws IOException {
		writer = new OutputStreamWriter(new FileOutputStream(fileName));
		writer.write("NAME,SEX,SPEECH,DECLINATION,REFERENCE\n");
	}
	
	@Override
	@SneakyThrows
	public void accept(PersonName t) {
		StringBuilder sb = new StringBuilder();
		sb.append(t.getText()).append(",");
		sb.append(t.getGender().equals(Gender.MALE)).append(",");
		sb.append(t.getSpeechPart()).append(",");
		sb.append(t.getNounDeclination()).append(",");
		sb.append(Optional.ofNullable(t.getRef())
					.map(WordRef::getNext)
					.map(Word::getText)
					.orElse(""));
		sb.append("\n");
		
		writer.write(sb.toString());
	}

	@Override
	public void close() throws Exception {
		writer.flush();
		writer.close();
	}
	
}