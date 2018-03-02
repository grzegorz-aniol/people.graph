package people.source.webcrawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import people.api.TextResource;
import people.api.TextResourceConsumer;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

public class WikiCrawler extends WebCrawler {

    private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");

    private Logger peopleLogger = LoggerFactory.getLogger("PEOPLE");
    private Logger lastNamesLogger = LoggerFactory.getLogger("LASTNAMES");

    private static AtomicLong cntPages = new AtomicLong(0);

    private HashMap<String, Integer> localResult = new HashMap<>();

    private static HashMap<String, Integer> globalResult = new HashMap<>();


    private final TextResourceConsumer resourceConsumer;

    public WikiCrawler(TextResourceConsumer rc) {
        this.resourceConsumer = rc;
    }

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

            return true;
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

            resourceConsumer.onNewResource(TextResource.builder()
                .sourceTypeName("wiki")
                .resourceId(url)
                .resourceTitle(pageTitle)
                .text(pageBody)
                .build());


        }

        logger.debug("=============");
    }


    private String extractPageTitle(final String title) {
        final String removeStr = " – Wikipedia, wolna encyklopedia";
        return title.replace(removeStr, "");
    }

}
