package people.api;

import people.analitics.SentenceText;

import java.util.function.Supplier;

public interface TextResourceConsumer {

    void addNewResource(TextResource resource);

}
