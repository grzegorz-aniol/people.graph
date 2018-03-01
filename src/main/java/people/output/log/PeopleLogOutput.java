package people.output.log;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import people.api.PersonOutputPlugin;
import people.dict.model.Person;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class PeopleLogOutput implements PersonOutputPlugin {

    private AtomicLong totalPerson = new AtomicLong(0);
    private AtomicLong totalRelations = new AtomicLong(0);

    private Logger peopleLog = LoggerFactory.getLogger("PEOPLE");
    private Logger lastNamesLog = LoggerFactory.getLogger("LASTNAMES");

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
        long total = totalPerson.incrementAndGet();
        log.info("Person #{} : {}", total, person.toString());
        peopleLog.info(person.toString());
        lastNamesLog.info(person.getLastName());
    }

    @Override
    public void onRelation(Person person1, Person person2, String relationName) {
        long total = totalRelations.incrementAndGet();
        log.info("Relation #{} : {} <- {} -> {}", total, person1.toString(), relationName, person2.toString());
    }

}
