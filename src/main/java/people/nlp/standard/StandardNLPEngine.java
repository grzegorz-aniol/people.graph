package people.nlp.standard;

import people.analitics.PeopleFinder;
import people.api.NLPEnginePlugin;
import people.conf.PeopleConfig;
import people.dict.DeclinationRulesSet;
import people.dict.NamesDictionary;
import people.dict.model.Person;
import people.source.webcrawler.WikiCrawlerController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public class StandardNLPEngine implements NLPEnginePlugin {

    private static NamesDictionary namesDict;

    private static DeclinationRulesSet lastNameDeclRules;

    private PeopleFinder peopleFinder;

    public StandardNLPEngine() throws IOException {
        namesDict = new NamesDictionary();
        namesDict.loadFromFile(PeopleConfig.DATA_FOLDER + "/names_fulldict4.csv");
        lastNameDeclRules = new DeclinationRulesSet(WikiCrawlerController.class.getResourceAsStream("/lastnames_declination_rules.csv"));
        peopleFinder = new PeopleFinder(namesDict, lastNameDeclRules);
    }

    @Override
    public Stream<Person> apply(String text) {
        List<Person> people = peopleFinder.identifyPeopleInText(text);
        if (people == null) {
            return Stream.empty();
        }
        return people.stream();
    }

}
