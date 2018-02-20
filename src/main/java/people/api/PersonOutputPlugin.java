package people.api;

import people.dict.model.Person;

import java.util.function.Consumer;

public interface PersonOutputPlugin {

    void beforeNewSet(final String sourceName, final String sourceId);

    void afterNewSet();

    void onPerson(Person person);

    void onRelation(Person person1, Person person2, String relationName);
}
