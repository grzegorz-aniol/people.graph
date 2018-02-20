package people.analitics.people.output.log;

import people.api.PersonOutputPlugin;
import people.dict.model.Person;

public class PeopleLogOutput implements PersonOutputPlugin {

    @Override
    public void beforeNewSet(final String sourceName, final String sourceId) {
        System.out.println("Starting new set of data..");
    }

    @Override
    public void afterNewSet() {
        System.out.println("New set of data is completed.");
    }

    @Override
    public void onPerson(Person person) {
        System.out.println("New person: " + person.toString());
    }

    @Override
    public void onRelation(Person person1, Person person2, String relationName) {
        System.out.println(String.format("%s <- %s -> %s", person1.toString(), relationName, person2.toString()));
    }

}
