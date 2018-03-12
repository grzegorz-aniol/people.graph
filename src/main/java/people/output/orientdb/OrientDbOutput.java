package people.output.orientdb;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import people.api.PersonOutputPlugin;
import people.dict.model.Person;
import people.performance.DataObjectMetric;
import people.performance.MetricsFormatter;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Objects;

@Slf4j
public class OrientDbOutput implements PersonOutputPlugin {

    private OrientDbConnection connection;
    private ODatabaseSession session;

    private HashMap<String, Person> personHashCache = new HashMap<>();

    private DataObjectMetric personsMetrics = new DataObjectMetric();
    private DataObjectMetric sourcesMetrics = new DataObjectMetric();
    private DataObjectMetric relationsMetrics = new DataObjectMetric();

    public OrientDbOutput() {
    }

    @Override
    public void onInit() {
        final String dbName = "people";
        final String user = "root";
        final String pwd = "admin";
        connection = OrientDbConnectionProducer.createRemoteConnection("localhost", dbName, user, pwd);
        session = connection.getDb().open(dbName, user, pwd);
    }

    @Override
    public void onDone() {

    }

    @Override
    public void beforeNewSet(String sourceName, String sourceId) {

    }

    @Override
    public void afterNewSet() {

    }

    @Override
    public void onPerson(Person person) {
        getOrSave(person);
    }

    @Override
    public void onRelation(Person person1, Person person2, String relationName) {

        final Person p1 = getOrSave(person1);
        final Person p2 = getOrSave(person2);

        Objects.requireNonNull(p1);
        Objects.requireNonNull(p2);

        relationsMetrics.getSaveTimer().time(()->{
            String person1Id = Objects.requireNonNull(p1.getId());
            String person2Id = Objects.requireNonNull(p2.getId());

            // checking if relation exists
            try(OResultSet result = session.execute("sql", "select from Wiki where out=? and in=? ",
                    person1Id, person2Id)) {
                if (!result.hasNext()) {
                    // create relation
                    try (OResultSet result2 = session.execute("sql", "create edge Wiki from ? to ? ",
                            new ORecordId(person1Id), new ORecordId(person2Id))) {
                    }
                }
            }
        });
    }

    @Override
    public void printStats() {
        log.info("RESULT PersonTimer {}", MetricsFormatter.getMetricDescription(personsMetrics.getSaveTimer(), ChronoUnit.NANOS));
        log.info("RESULT RelationTimer {}", MetricsFormatter.getMetricDescription(relationsMetrics.getSaveTimer(), ChronoUnit.NANOS));
    }

    @Override
    public void close()  {
        connection.getDb().close();
    }


    @SneakyThrows
    private Person getOrSave(Person person) {

        Objects.requireNonNull(person);

        if (person.getId() != null) {
            return person;
        }

        Person cachedPerson  = personHashCache.get(person.toString());
        if (cachedPerson != null) {
            return cachedPerson;
        }

        String id = personsMetrics.getSaveTimer().time(() -> {
            try(OResultSet resultSet = session.execute("sql", "update Person MERGE {name:?, fname:?, sname:?, lname:?} " +
                            "UPSERT return after @rid WHERE name=?",
                    person.toString(), person.getFirstName(), person.getSecondName(), person.getLastName(), person.toString())) {
                if (resultSet.hasNext()) {
                    OResult result = resultSet.next();
                    ORecordId recordId = (ORecordId) result.getProperty("@rid");
                    return recordId.toString();
                }
            }
            throw new UnsupportedOperationException("Can't read ID of created Vertex");
        });

        session.getTransaction().commit();

        person.setId(id);

        personHashCache.put(person.toString(), person);

        return person;
    }

}
