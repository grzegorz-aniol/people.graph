package people.graph;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;
import lombok.SneakyThrows;
import people.analitics.PeopleFinder;
import people.dict.DeclinationRulesSet;
import people.dict.NamesDictionary;
import people.dict.model.Person;

public class WikiCrawler extends WebCrawler {

	private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");
	
	private Logger peopleLogger = LoggerFactory.getLogger("PEOPLE");
	private Logger lastNamesLogger = LoggerFactory.getLogger("LASTNAMES");

	private static AtomicLong cntPages = new AtomicLong(0);
	
	private static AtomicLong cntPersonFound = new AtomicLong(0);
	private static AtomicLong cntRelationFound = new AtomicLong(0);
	
	private HashMap<String, Integer> localResult = new HashMap<>();
	
	private static HashMap<String, Integer> globalResult = new HashMap<>();
	
	private ConcurrentHashMap<String, Person> allPeopleFound = new ConcurrentHashMap<>();
	
	private static NamesDictionary namesDict; 
	
	private static DeclinationRulesSet lastNameDeclRules;
	
	private PeopleFinder peopleFinder; 
	
	
	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();

		if (IMAGE_EXTENSIONS.matcher(href).matches()) {
			return false;
		}

		if (href.startsWith("https://pl.wikipedia.org/")) {
			if (href.matches(".+/wiki/.+:.+")) {
				return false;
			}
			
			int pos = href.lastIndexOf("/");
			String pageResource = href.substring(pos+1, href.length());
			
			if (pageResource.startsWith("index.php")) {
				return false; 
			}
			
			if (pageResource != null) {
				pos = pageResource.indexOf("_");
				if (pos != -1) {
					String nameCandidate = WordUtils.capitalize(pageResource.substring(0, pos));
					if (namesDict.contains(nameCandidate)) {
						return true;
					}
				}
			}
			
			
			return false;
		}
		
		return false; 
	}

	@Override
	public void onBeforeExit() {
		synchronized(globalResult) {
			localResult.entrySet().stream().forEach( 
					(a) -> { 
							globalResult.put(a.getKey(), globalResult.getOrDefault(a.getKey(), 0) + a.getValue() ); 
							} );
		}
	}
	

	@Override
	@SneakyThrows
	public void onStart() {
		super.onStart();
		peopleFinder = new PeopleFinder(namesDict, lastNameDeclRules);		
	}

	
	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		cntPages.incrementAndGet();

		int docid = page.getWebURL().getDocid();
		String url = page.getWebURL().getURL();
		String domain = page.getWebURL().getDomain();
		String path = page.getWebURL().getPath();
		String subDomain = page.getWebURL().getSubDomain();
		String parentUrl = page.getWebURL().getParentUrl();
		String anchor = page.getWebURL().getAnchor();

		// logger.debug("Docid: {}", docid);
		// logger.info("URL: {}", url);
		// logger.debug("Domain: '{}'", domain);
		// logger.debug("Sub-domain: '{}'", subDomain);
		logger.debug("Path: '{}'", path);
		// logger.debug("Parent page: {}", parentUrl);
		// logger.debug("Anchor text: {}", anchor);

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String pageTitle = extractPageTitle(htmlParseData.getTitle());
			String pageBody = htmlParseData.getText();
			
			int linksPos = pageBody.lastIndexOf("Linki zewnętrzne");
			linksPos = Math.min(linksPos == -1 ? pageBody.length() : linksPos,  pageBody.lastIndexOf("Menu nawigacyjne"));
			linksPos = Math.min(linksPos == -1 ? pageBody.length() : linksPos,  pageBody.lastIndexOf("Tę stronę ostatnio edytowano"));			
			if (linksPos > 0) {
				pageBody = pageBody.substring(0, linksPos);			
			}
			
//			PrintWriter pr;
//			try {
//				pr = new PrintWriter("./web/" + cntPages.get() + ".txt");
//				pr.print(pageBody);
//				pr.flush();
//				pr.close();
//			} catch (FileNotFoundException e) {
//			}
			
//			String html = htmlParseData.getHtml();
//			Set<WebURL> links = htmlParseData.getOutgoingUrls();
			
			List<Person> personsInTitle = peopleFinder.identifyPeopleInText(pageTitle);
			boolean isTitleAboutPerson = (personsInTitle != null && !personsInTitle.isEmpty());
			
			String titlePersonKey = "";
			if (isTitleAboutPerson) {
				Person personInTitle = personsInTitle.get(0);
				titlePersonKey = personInTitle.toString();
				allPeopleFound.putIfAbsent(titlePersonKey, personInTitle);	
				peopleLogger.info( "#" + cntPersonFound.incrementAndGet() + " : " + titlePersonKey);
				lastNamesLogger.info(personInTitle.getLastName());
			}
			
			List<Person> personsInText = peopleFinder.identifyPeopleInText(pageBody);
			
			Iterator<Person> iteratorBody = personsInText.iterator();
			while (iteratorBody.hasNext()) {
				Person person = iteratorBody.next();	
				String key = person.toString();
				if (key.equals(titlePersonKey)) {
					continue;
				}
				allPeopleFound.putIfAbsent(key, person);
				
				lastNamesLogger.info(person.getLastName());

				if (titlePersonKey != null && !titlePersonKey.isEmpty()) {
					peopleLogger.info("Relacja #"+cntRelationFound.incrementAndGet() + " " + titlePersonKey + " <-> #" + cntPersonFound.incrementAndGet() + " " + key);
				} else {
					peopleLogger.info(allPeopleFound.size() + " : [" + pageTitle + "] <-> " + key);
				}
				
			}
		}

		logger.debug("=============");
	}

	
	private String extractPageTitle(final String title) {
		 final String removeStr = " – Wikipedia, wolna encyklopedia";
		 return title.replace(removeStr, "");
	}

	public static void main(String[] args) {
		
		namesDict = new NamesDictionary();
		try {
			namesDict.loadFromFile("d:/dev/workspace/people.graph/names_fulldict2.csv");
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}

		try {
			lastNameDeclRules = new DeclinationRulesSet(WikiCrawler.class.getResourceAsStream("/lastnames_declination_rules.csv"));
		} catch (IOException e1) {
			e1.printStackTrace();
			return; 
		}

	    /*
	     * crawlStorageFolder is a folder where intermediate crawl data is
	     * stored.
	     */
	    String crawlStorageFolder = "d:/dev/workspace/people.graph/crawler_data";
	
	    /*
	     * numberOfCrawlers shows the number of concurrent threads that should
	     * be initiated for crawling.
	     */
	    final int numberOfCrawlers = 4;
	
	    CrawlConfig config = new CrawlConfig();
	
	    config.setCrawlStorageFolder(crawlStorageFolder);
	
	    /*
	     * Be polite: Make sure that we don't send more than 1 request per
	     * second (1000 milliseconds between requests).
	     */
	    config.setPolitenessDelay(0);
	
	    /*
	     * You can set the maximum crawl depth here. The default value is -1 for
	     * unlimited depth
	     */
	    config.setMaxDepthOfCrawling(5);
	
	    /*
	     * You can set the maximum number of pages to crawl. The default value
	     * is -1 for unlimited number of pages
	     */
	    config.setMaxPagesToFetch(10_000);
	
	    /**
	     * Do you want crawler4j to crawl also binary data ?
	     * example: the contents of pdf, or the metadata of images etc
	     */
	    config.setIncludeBinaryContentInCrawling(false);
	
	    /*
	     * Do you need to set a proxy? If so, you can use:
	     * config.setProxyHost("proxyserver.example.com");
	     * config.setProxyPort(8080);
	     *
	     * If your proxy also needs authentication:
	     * config.setProxyUsername(username); config.getProxyPassword(password);
	     */
	
	    /*
	     * This config parameter can be used to set your crawl to be resumable
	     * (meaning that you can resume the crawl from a previously
	     * interrupted/crashed crawl). Note: if you enable resuming feature and
	     * want to start a fresh crawl, you need to delete the contents of
	     * rootFolder manually.
	     */
	    config.setResumableCrawling(false);
	
	    /*
	     * Instantiate the controller for this crawl.
	     */
	    PageFetcher pageFetcher = new PageFetcher(config);
	    RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
	    RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
	    CrawlController controller;
		try {
			controller = new CrawlController(config, pageFetcher, robotstxtServer);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return; 
		}
	
	    /*
	     * For each crawl, you need to add some seed urls. These are the first
	     * URLs that are fetched and then the crawler starts following links
	     * which are found in these pages
	     */
	//    controller.addSeed("http://www.ics.uci.edu/");
	//    controller.addSeed("http://www.ics.uci.edu/~lopes/");
	//    controller.addSeed("http://www.ics.uci.edu/~welling/");
		controller.addSeed("https://pl.wikipedia.org/wiki/Mariusz_Kazana");
		//controller.addSeed("https://pl.wikipedia.org/wiki/Departament_I_MSW");
//		controller.addSeed("https://pl.wikipedia.org/wiki/Robert_Lewandowski");
		
	    /*
	     * Start the crawl. This is a blocking operation, meaning that your code
	     * will reach the line after this only when crawling is finished.
	     */
	    controller.start(WikiCrawler.class, numberOfCrawlers);
	    
	    // show statistic
	    globalResult.entrySet().stream()
	    	.sorted(Comparator.comparing(Entry<String,Integer>::getValue).reversed())
	    	.forEach( 
	    			(p) -> {
	    				System.out.println(String.format("%s %d", p.getKey(), p.getValue()));
	    			}
	    	);
	
	}
	
	
}
