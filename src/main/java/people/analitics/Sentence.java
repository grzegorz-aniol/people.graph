package people.analitics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class Sentence {

    private List<AbstractSentence> parts;

    private Sentence(SentencePart part) {
        parts = new LinkedList<>();
        parts.add(part);
    }

    public Iterator getIterator() {
        return parts.iterator();
    }

}
