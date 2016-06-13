package people.graph;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class WikiCrawler extends WebCrawler {

	private Logger log = LoggerFactory.getLogger(WikiCrawler.class);
	
	private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");

	private HashSet<String> names = new HashSet<>();

	private static AtomicLong cntPersons = new AtomicLong(0);

	private static AtomicLong cntPages = new AtomicLong(0);

	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		// Ignore the url if it has an extension that matches our defined set of
		// image extensions.
		if (IMAGE_EXTENSIONS.matcher(href).matches()) {
			return false;
		}

		// Only accept the url if it is in the "www.ics.uci.edu" domain and
		// protocol is "http".
		return href.startsWith("https://pl.wikipedia.org/");
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
			String text = htmlParseData.getText();
//			String html = htmlParseData.getHtml();
//			Set<WebURL> links = htmlParseData.getOutgoingUrls();
			
			String mainPerson = NamesDictionary.findPerson(pageTitle);
			boolean isPersonTitle = (mainPerson != null && !mainPerson.isEmpty());

			Matcher matcher = NamesDictionary.regexpPerson2.matcher(text);
			while (matcher.find()) {
				String name = matcher.group();
				String firstName = matcher.group(1);
				// String lastName = matcher.group(2);

				if (NamesDictionary.NAMES.contains(firstName)) {
					if (!names.contains(name)) {
						cntPersons.incrementAndGet(); 
						names.add(name);
						
//						logger.info(" > #{}: {} (pages: {})", cntPersons.get(), name, cntPages.get());
						if (isPersonTitle && !mainPerson.equals(name)) {
							logger.info("{} -> {}", mainPerson, name);
						}
						if (!isPersonTitle) {
							logger.info("{} -> {}", pageTitle, name);
						}
					}

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
	    config.setMaxDepthOfCrawling(2);
	
	    /*
	     * You can set the maximum number of pages to crawl. The default value
	     * is -1 for unlimited number of pages
	     */
	    config.setMaxPagesToFetch(1000);
	
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
//		controller.addSeed("https://pl.wikipedia.org/wiki/Mariusz_Kazana");
		controller.addSeed("https://pl.wikipedia.org/wiki/Departament_I_MSW");
		
	    /*
	     * Start the crawl. This is a blocking operation, meaning that your code
	     * will reach the line after this only when crawling is finished.
	     */
	    controller.start(WikiCrawler.class, numberOfCrawlers);
	
	}
	
	
}
