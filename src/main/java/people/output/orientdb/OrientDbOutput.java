package people.output.orientdb;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import people.api.PersonOutputPlugin;
import people.dict.model.Person;
import people.performance.DataObjectMetric;

import java.util.Optional;

public class OrientDbOutput implements PersonOutputPlugin {

    private OrientDbConnection connection;
    private ODatabaseSession session;

    private DataObjectMetric personsMetrics = new DataObjectMetric();
    private DataObjectMetric sourcesMetrics = new DataObjectMetric();
    private DataObjectMetric relationsMetrics = new DataObjectMetric();

    public OrientDbOutput() {
    }

    @Override
    public void onInit() {
        final String dbName = "people";
        final String user = "admin";
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
        session.execute("sql", "update Person MERGE {name=?, fname=?, sname=?, lname=?} " +
                        "UPSERT WHERE name=? AND fname=? AND sname=? AND lname=?",
                person.toString(), person.getFirstName(), person.getSecondName(), person.getLastName());
    }

    @Override
    public void onRelation(Person person1, Person person2, String relationName) {

    }

    @Override
    public void printStats() {

    }

    @Override
    public void close()  {
        connection.getDb().close();
    }
}
