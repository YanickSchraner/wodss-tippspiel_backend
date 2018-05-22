package ch.fhnw.wodss.tippspiel.job;

import ch.fhnw.wodss.tippspiel.util.WikipediaScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ApiConnector {
    final
    WikipediaScraper wikipediaScraper;

    @Autowired
    public ApiConnector(WikipediaScraper wikipediaScraper) {
        this.wikipediaScraper = wikipediaScraper;
    }

    @Scheduled(cron = "0 */30 17-23 * * *")
    public void updateGames() {
        wikipediaScraper.scrapeGroupToSemiFinal();
        wikipediaScraper.scrapeSmallFinal();
        wikipediaScraper.scrapeSmallFinal();
    }
}
