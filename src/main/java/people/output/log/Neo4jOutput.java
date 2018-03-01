package people.output.log;

import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import people.api.PersonOutputPlugin;
import people.dict.model.Person;

import java.util.HashMap;

import static org.neo4j.driver.v1.Values.parameters;

@Slf4j
public class Neo4jOutput implements PersonOutputPlugin, AutoCloseable {

    private Driver driver;
    private Session session;

    private HashMap<String, Long> personHashCache = new HashMap<>();
    private HashMap<Long, Person> personCache = new HashMap<>();

    public Neo4jOutput() {
        driver = GraphDatabase.driver( "bolt://localhost:7687",
            //AuthTokens.basic( "neo4j", "password" )
            AuthTokens.none()
        );
        session = driver.session();
    }

    @Override
    public void beforeNewSet(String sourceName, String sourceId) {

    }

    @Override
    public void afterNewSet() {

    }

    @Override
    public void onPerson(Person person) {
        log.debug("Create person : {}..", person.toString());
        getOrCreate(person);
    }

    @Override
    public void onRelation(Person person1, Person person2, String relationName) {
        log.debug("Adding relation: {}-{}..", person1.toString(), person2.toString());
        createRelation(person1, person2, relationName);
    }

    private void createRelation(Person person1, Person person2, String relationName) {
        person1 = getOrCreate(person1);
        person2 = getOrCreate(person2);
        session.run("MATCH (p1:Person), (p2:Person) WHERE ID(p1)=$id1 AND ID(p2)=$id2 CREATE (p1)-[r:inwiki]->(p2) RETURN r",
                parameters("id1", person1.getId(), "id2", person2.getId()));
    }

    private Person getOrCreate(Person person) {
        if (person.getId() != null) {
            return person;
        }
        Person foundPerson = findPersonInCache(person);
        if (foundPerson != null) {
            return foundPerson;
        }

        StatementResult statement = session.run("CREATE (ee:Person { name: $n, sname: $sn, lname: $ln }) RETURN ID(ee)",
                parameters("n", person.getFirstName(), "sn", person.getSecondName(), "ln", person.getLastName()));
        long id = statement.single().get(0).asLong();
        addPersonToCache(id, person);
        person.setId(id);
        return person;
    }

    private String getPersonCache(Person p) {
        return p.toString();
    }

    private void addPersonToCache(Long id, Person p) {
        personCache.put(id, p);
        personHashCache.put(getPersonCache(p),id);
    }

    private Person findPersonInCache(Person p) {
        Long id = personHashCache.get(getPersonCache(p));
        if (id == null) {
            return null;
        }
        return personCache.get(id);
    }

    @Override
    public void close() throws Exception {
        if (session != null) {
            session.close();
        }
        if (driver != null) {
            driver.close();
        }
    }
}
