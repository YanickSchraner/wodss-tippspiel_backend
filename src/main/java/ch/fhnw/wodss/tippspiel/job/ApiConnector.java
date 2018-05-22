package ch.fhnw.wodss.tippspiel.job;

import ch.fhnw.wodss.tippspiel.util.WikipediaScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ApiConnector {
    private final WikipediaScraper wikipediaScraper;
    private static final Logger logger = LoggerFactory.getLogger(ApiConnector.class);



    @Autowired
    public ApiConnector(WikipediaScraper wikipediaScraper) {
        this.wikipediaScraper = wikipediaScraper;
    }

    @Scheduled(cron = "0 */30 15-23 * * *")
    public void updateGames() {
        wikipediaScraper.scrapeGroupToSemiFinal();
        wikipediaScraper.scrapeSmallFinal();
        wikipediaScraper.scrapeSmallFinal();
        logger.info("New Data scraped from wikipedia.");
    }
}
