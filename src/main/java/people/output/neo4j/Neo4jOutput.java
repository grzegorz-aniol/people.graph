package people.output.neo4j;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import people.api.PersonOutputPlugin;
import people.dict.model.Person;
import people.performance.DataObjectMetric;
import people.performance.MetricsFormatter;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import static org.neo4j.driver.v1.Values.parameters;

@Slf4j
public class Neo4jOutput implements PersonOutputPlugin {

    private Driver driver;
    private Session session;

    private HashMap<String, Long> personHashCache = new HashMap<>();
    private HashMap<Long, Person> personCache = new HashMap<>();

    private Long sourceNodeId;

    private DataObjectMetric personsMetrics = new DataObjectMetric();
    private DataObjectMetric sourcesMetrics = new DataObjectMetric();
    private DataObjectMetric relationsMetrics = new DataObjectMetric();

    public Neo4jOutput() {
        driver = GraphDatabase.driver( "bolt://localhost:7687",
            //AuthTokens.basic( "neo4j", "password" )
            AuthTokens.none()
        );
        session = driver.session();
    }

    @Override
    public void onInit() {
        personsMetrics.getInitialCount().add( session.run("match (p:Person) return count(p)").single().get(0).asLong());
        sourcesMetrics.getInitialCount().add( session.run("match (s:Source) return count(s)").single().get(0).asLong());
        long v1 = session.run("match ()-[w:inwiki]->() return count(w)").single().get(0).asLong();
        long v2 = session.run("match ()-[w:insource]->() return count(w)").single().get(0).asLong();
        relationsMetrics.getInitialCount().add(v1 + v2);

        log.info("# of Person nodes in db = {}", personsMetrics.getInitialCount().longValue());
        log.info("# of Source nodes in db = {}", sourcesMetrics.getInitialCount().longValue());
        log.info("# of relations in db = {}", relationsMetrics.getInitialCount().longValue());
    }

    @Override
    public void beforeNewSet(String sourceType, String sourceUrl) {
        this.sourceNodeId = getOrCreateSource(sourceType, sourceUrl);
    }

    @Override
    public void afterNewSet() {
    }

    @Override
    public void onPerson(Person person) {
        log.debug("Create person : {}..", person.toString());
        person = getOrCreate(person);
        createSourceRelation(person);
    }

    @Override
    public void onRelation(Person person1, Person person2, String relationName) {
        log.debug("Adding relation: {}-{}..", person1.toString(), person2.toString());
        createRelation(person1, person2, relationName);
    }

    private void createRelation(Person person1, Person person2, String relationName) {
        val p1 = getOrCreate(person1);
        val p2 = getOrCreate(person2);

        relationsMetrics.getSaveTimer().time(()->{
            session.run("MATCH (p1:Person), (p2:Person) WHERE ID(p1)=$id1 AND ID(p2)=$id2 MERGE (p1)-[r:inwiki]->(p2) RETURN r",
                parameters("id1", p1.getId(), "id2", p2.getId()));
        });
    }

    @SneakyThrows
    private Long getOrCreateSource(String sourceType, String sourceUrl) {

        return personsMetrics.getSaveTimer().time(()->{
            StatementResult statement = session.run(" merge (s:Source {type: $t, url: $u}) return ID(s)",
                parameters("t", sourceType,
                    "u", sourceUrl));
            Long id = statement.single().get(0).asLong();
            return id;
        });

    }

    private void createSourceRelation(Person person1) {
        Objects.requireNonNull(person1);
        Objects.requireNonNull(person1.getId());
        Objects.requireNonNull(sourceNodeId);

        relationsMetrics.getSaveTimer().time(()->{
            session.run("MATCH (p1:Person) WHERE ID(p1)=$id1 with(p1) MATCH(s:Source) where ID(s)=$id2 MERGE (p1)-[i:insource]->(s) RETURN i",
                parameters("id1", person1.getId(), "id2", sourceNodeId));
        });
    }

    @SneakyThrows
    private Person getOrCreate(Person person) {
        if (person.getId() != null) {
            return person;
        }
        Person foundPerson = findPersonInCache(person);
        if (foundPerson != null) {
            return foundPerson;
        }

        Long id = personsMetrics.getSaveTimer().time(()->{
            StatementResult statement = session.run("MERGE (ee:Person { name: $n, fname: $fn, sname: $sn, lname: $ln }) RETURN ID(ee)",
                parameters("n", person.getLastName(),
                    "fn", person.getFirstName(),
                    "sn", Optional.ofNullable(person.getSecondName()).orElse(""),
                    "ln", person.getLastName()));
            return statement.single().get(0).asLong();
        });

        Objects.requireNonNull(id);

        person.setId(id.toString());
        addPersonToCache(id, person);

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

    @Override
    public void printStats() {
        log.info("RESULT PersonTimer {}", MetricsFormatter.getMetricDescription(personsMetrics.getSaveTimer(), ChronoUnit.NANOS));
        log.info("RESULT RelationTimer {}", MetricsFormatter.getMetricDescription(relationsMetrics.getSaveTimer(), ChronoUnit.NANOS));
    }
}
