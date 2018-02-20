package people.engine;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import people.analitics.people.output.log.PeopleLogOutput;
import people.api.TextResource;
import people.api.TextResourceConsumer;
import people.dict.model.Person;
import people.nlp.standard.StandardNLPEngine;
import people.source.webcrawler.WikiCrawlerController;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
public class PeopleGraphMain implements TextResourceConsumer {

    private static final int RESOURCE_QUEUE_SIZE_LIMIT = 1_000;

    private PeopleLogOutput outputPlugin;

    private StandardNLPEngine nlpEngine;

    private WikiCrawlerController wikiCrawler;

    private Queue<TextResource> resourceQueue = new LinkedBlockingQueue<>(RESOURCE_QUEUE_SIZE_LIMIT);

    private static AtomicLong cntPersonFound = new AtomicLong(0);
    private static AtomicLong cntRelationFound = new AtomicLong(0);

    public void run() throws IOException {
        log.info("Engine started.");
        outputPlugin = new PeopleLogOutput();
        nlpEngine = new StandardNLPEngine();
        wikiCrawler = new WikiCrawlerController(this);

        wikiCrawler.start();
        try {
            proceesQueue();
        } catch (InterruptedException e) {
            return;
        }
        wikiCrawler.stop();

        log.info("Engine is done.");
    }

    private void proceesQueue() throws InterruptedException {
        do {
            TextResource resource = resourceQueue.poll();
            if (resource != null) {
                processResource(resource);
            }
        } while (true);
    }

    private void processResource(TextResource resource) {

        outputPlugin.beforeNewSet(resource.getSourceTypeName(), resource.getResourceId());
        List<Person> personsInTitle = nlpEngine.process(resource.getResourceTitle())
            .peek(person -> {
                outputPlugin.onPerson(person);
            })
            .collect(Collectors.toList());

        boolean isTitleAboutPerson = (personsInTitle != null && !personsInTitle.isEmpty());
        Person personFromTitle = (personsInTitle.size() > 0 ? personsInTitle.get(0) : null);

        nlpEngine.process(resource.getText())
            .forEach(person -> {
                boolean isTitlePerson = (isTitleAboutPerson && person.toString().equals(personFromTitle.toString()));
                if (!isTitlePerson) {
                    outputPlugin.onPerson(person);
                    if (isTitleAboutPerson) {
                        outputPlugin.onRelation(personFromTitle, person, "wiki-page");
                    }
                }
            });

    }

    public static void main(final String[] args) {

        try {
            new PeopleGraphMain().run();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    @Override
    @SneakyThrows
    public void addNewResource(TextResource resource) {
        resourceQueue.add(resource);
    }
}
