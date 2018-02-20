package people.api;

import people.dict.model.Person;

import java.util.function.Function;
import java.util.stream.Stream;

public interface NLPEnginePlugin extends Function<String, Stream<Person>> {

    default Stream<Person> process(final String text) {
        return this.apply(text);
    }

}
