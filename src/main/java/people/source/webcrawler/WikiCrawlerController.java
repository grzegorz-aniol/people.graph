package people.source.webcrawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import lombok.extern.slf4j.Slf4j;
import people.api.TextResourceConsumer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Slf4j
public class WikiCrawlerController implements CrawlController.WebCrawlerFactory {

	private TextResourceConsumer resourceConsumer;
	private CrawlController controller;

	public WikiCrawlerController(TextResourceConsumer rc) {
		this.resourceConsumer = rc;
	}

	@Override
	public WebCrawler newInstance() throws Exception {
		return new WikiCrawler(resourceConsumer);
	}

	public void stop() {
		if (controller != null) {
			controller.shutdown();
			controller.waitUntilFinish();
		}
	}

	public void start() {

	    /*
	     * crawlStorageFolder is a folder where intermediate crawl data is
	     * stored.
	     */
	    String crawlStorageFolder = "./crawler_data";
	
	    /*
	     * numberOfCrawlers shows the number of concurrent threads that should
	     * be initiated for crawling.
	     */
	    final int numberOfCrawlers = 2;
	
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
		try {
			controller = new CrawlController(config, pageFetcher, robotstxtServer);
		} catch (Exception e) {
			log.error(e.getMessage());
			return; 
		}

		try (Stream<String> stream = Files.lines(Paths.get("./url_seeds.txt"))) {
			stream.forEach(url -> controller.addSeed(url));
		} catch (IOException e) {
			log.error(e.getMessage());
			return;
		}

	    controller.startNonBlocking(this::newInstance, numberOfCrawlers);
	}

	public boolean isFinished() {
		return (controller.isFinished());
	}

}
