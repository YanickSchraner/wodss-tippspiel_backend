package ch.fhnw.wodss.tippspiel.util;

import ch.fhnw.wodss.tippspiel.domain.*;
import ch.fhnw.wodss.tippspiel.persistance.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

@Component
public class WikipediaScraper {

    private static final String WIKIPEDIA_SEARCH_URL = "https://de.wikipedia.org/wiki/Fu%C3%9Fball-Weltmeisterschaft_2018";
    private HashMap<String, String> teams = new HashMap<>();
    private HashMap<String, String> locations = new HashMap<>();
    private HashMap<String, Integer> month = new HashMap<>();
    private GameRepository gameRepository;
    private LocationRepository locationRepository;
    private PhaseRepository phaseRepository;
    private TournamentTeamRepository tournamentTeamRepository;
    private TournamentGroupRepository tournamentGroupRepository;

    @Autowired
    public WikipediaScraper(GameRepository gameRepository, LocationRepository locationRepository, PhaseRepository phaseRepository, TournamentTeamRepository tournamentTeamRepository, TournamentGroupRepository tournamentGroupRepository) {
        this.gameRepository = gameRepository;
        this.locationRepository = locationRepository;
        this.phaseRepository = phaseRepository;
        this.tournamentGroupRepository = tournamentGroupRepository;
        this.tournamentTeamRepository = tournamentTeamRepository;

        teams.put("Russland", "rus");
        teams.put("Saudi-Arabien", "sau");
        teams.put("Ägypten", "egy");
        teams.put("Uruguay", "uru");
        teams.put("Portugal", "por");
        teams.put("Spanien", "spa");
        teams.put("Marokko", "mor");
        teams.put("Iran", "ira");
        teams.put("Frankreich", "fra");
        teams.put("Australien", "aus");
        teams.put("Peru", "per");
        teams.put("Dänemark", "den");
        teams.put("Argentinien", "arg");
        teams.put("Island", "ice");
        teams.put("Kroatien", "cro");
        teams.put("Nigeria", "nig");
        teams.put("Brasilien", "bra");
        teams.put("Schweiz", "swi");
        teams.put("Costa Rica", "cos");
        teams.put("Serbien", "ser");
        teams.put("Deutschland", "ger");
        teams.put("Mexiko", "mex");
        teams.put("Schweden", "swe");
        teams.put("Südkorea", "kor");
        teams.put("Belgien", "bel");
        teams.put("Panama", "pan");
        teams.put("Tunesien", "tun");
        teams.put("England", "eng");
        teams.put("Polen", "pol");
        teams.put("Senegal", "sen");
        teams.put("Kolumbien", "col");
        teams.put("Japan", "jap");

        locations.put("Moskau", "mos");
        locations.put("Jekaterinburg", "jek");
        locations.put("Sankt", "san");
        locations.put("Sankt Petersburg", "san");
        locations.put("Rostow", "ros");
        locations.put("Samara", "sam");
        locations.put("Wolgograd", "wol");
        locations.put("Sotschi", "sot");
        locations.put("Kasan", "kas");
        locations.put("Saransk", "sar");
        locations.put("Kaliningrad", "kal");
        locations.put("Nischni", "nis");
        locations.put("Rostow am Don", "ros");
        locations.put("Nischni Nowgorod", "nis");

        month.put("Januar", 1);
        month.put("Februar", 2);
        month.put("März", 3);
        month.put("April", 4);
        month.put("Mai", 5);
        month.put("Juni", 6);
        month.put("Juli", 7);
        month.put("August", 8);
        month.put("September", 9);
        month.put("Oktober", 10);
        month.put("November", 11);
        month.put("Dezember", 12);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void scrapeGroupToSemiFinal() {
        Document doc = null;
        try {
            doc = Jsoup.connect(WIKIPEDIA_SEARCH_URL).userAgent("Mozilla/5.0").get();
            Elements groups = doc.select("table.wikitable.zebra.hintergrundfarbe5 > tbody");
            for (int countGroups = 0; countGroups < groups.size(); countGroups++) {
                Element group = groups.get(countGroups);
                this.parseGroupTable(group, countGroups);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void scrapeFinal() {
        Document doc = null;
        try {
            doc = Jsoup.connect(WIKIPEDIA_SEARCH_URL).userAgent("Mozilla/5.0").get();
            Elements tableBodies = doc.select("table.wikitable[width=100%] > tbody");
            Element tableBody = tableBodies.first();
            Element teams = tableBody.child(0);
            String homeTeamName = lookupTeamAbreviation(teams.child(0).ownText());
            String awayTeamName = lookupTeamAbreviation(teams.child(1).ownText());
            Element dateAndLocation = tableBody.child(1).select("tbody > tr").last();
            String date = dateAndLocation.child(0).textNodes().get(0).getWholeText();
            LocalDateTime localDateTime = this.parseDateTime(date);
            String loc = lookupLocationAbreviation(dateAndLocation.child(0).child(0).ownText());
            String phaseName = "finals";
            String groupName = "";
            Integer homeScore = null;
            Integer awayScore = null;

            Location location = locationRepository.findFirstByNameEquals(loc).orElse(new Location(loc));
            locationRepository.save(location);
            Phase phase = phaseRepository.findFirstByNameEquals(phaseName).orElse(new Phase(phaseName));
            phaseRepository.save(phase);
            if (groupName.equals("")) {
                groupName = "-";
            }
            TournamentGroup tournamentGroup = tournamentGroupRepository.findByNameEquals(groupName)
                    .orElse(new TournamentGroup(groupName));
            tournamentGroupRepository.save(tournamentGroup);
            TournamentTeam homeTeam = tournamentTeamRepository.findTournamentTeamByNameEquals(homeTeamName)
                    .orElse(new TournamentTeam(homeTeamName, tournamentGroup));
            tournamentTeamRepository.save(homeTeam);
            TournamentTeam awayTeam = tournamentTeamRepository.findTournamentTeamByNameEquals(awayTeamName)
                    .orElse(new TournamentTeam(awayTeamName, tournamentGroup));
            tournamentTeamRepository.save(awayTeam);
            Game game = gameRepository.findFirstByHomeTeamEqualsAndAwayTeamEqualsAndDateTimeIsBetween
                    (homeTeam, awayTeam, localDateTime.minusMinutes(10), localDateTime.plusMinutes(10))
                    .orElse(new Game(localDateTime, homeScore, awayScore, homeTeam, awayTeam, location, phase));
            game.setHomeTeamGoals(homeScore);
            game.setAwayTeamGoals(awayScore);
            game.setAwayTeam(awayTeam);
            game.setHomeTeam(homeTeam);
            game.setLocation(location);
            game.setPhase(phase);
            game.setDateTime(localDateTime);
            gameRepository.save(game);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void scrapeSmallFinal() {
        Document doc = null;
        try {
            doc = Jsoup.connect(WIKIPEDIA_SEARCH_URL).userAgent("Mozilla/5.0").get();
            Element table = doc.select("h3 > span#Spiel_um_Platz_3").parents().get(0).nextElementSibling();
            Element tableBody = table.child(0);
            this.parseGroupTable(tableBody, 11);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void parseGroupTable(Element group, int countGroups) {
        for (int i = 0; i < group.children().size(); i++) {
            Element gameDetails = group.child(i).child(0);
            i++; // Move index to teams and result row
            Element teamsAndResult = group.child(i);
            this.parseGameInTable(gameDetails, teamsAndResult, countGroups);
        }
    }

    private void parseGameInTable(Element gameDetails, Element teamsAndResult, int countGroups) {
        String detailsString = gameDetails.textNodes().get(0).text();
        String loc = this.parseLocation(gameDetails);
        LocalDateTime localDateTime = this.parseDateTime(detailsString);
        String groupName = this.getGroupName(countGroups);
        String phaseName = this.getPhaseName(countGroups);
        String homeTeamName = lookupTeamAbreviation(teamsAndResult.child(0).ownText());
        String awayTeamName = lookupTeamAbreviation(teamsAndResult.child(2).ownText());
        String scoreText = teamsAndResult.child(3).ownText();
        if (scoreText.equals("")) {
            scoreText = teamsAndResult.child(3).child(0).ownText();
        }
        Tuple<Integer, Integer> score = this.parseScore(scoreText);
        Integer homeScore = score.left;
        Integer awayScore = score.right;

        Location location = locationRepository.findFirstByNameEquals(loc).orElse(new Location(loc));
        locationRepository.save(location);
        Phase phase = phaseRepository.findFirstByNameEquals(phaseName).orElse(new Phase(phaseName));
        phaseRepository.save(phase);
        if (groupName.equals("")) {
            groupName = "-";
        }
        TournamentGroup tournamentGroup = tournamentGroupRepository.findByNameEquals(groupName)
                .orElse(new TournamentGroup(groupName));
        tournamentGroupRepository.save(tournamentGroup);
        TournamentTeam homeTeam = tournamentTeamRepository.findTournamentTeamByNameEquals(homeTeamName)
                .orElse(new TournamentTeam(homeTeamName, tournamentGroup));
        tournamentTeamRepository.save(homeTeam);
        TournamentTeam awayTeam = tournamentTeamRepository.findTournamentTeamByNameEquals(awayTeamName)
                .orElse(new TournamentTeam(awayTeamName, tournamentGroup));
        tournamentTeamRepository.save(awayTeam);
        Game game = gameRepository.findFirstByHomeTeamEqualsAndAwayTeamEqualsAndDateTimeIsBetween
                (homeTeam, awayTeam, localDateTime.minusMinutes(10), localDateTime.plusMinutes(10))
                .orElse(new Game(localDateTime, homeScore, awayScore, homeTeam, awayTeam, location, phase));
        game.setHomeTeamGoals(homeScore);
        game.setAwayTeamGoals(awayScore);
        game.setAwayTeam(awayTeam);
        game.setHomeTeam(homeTeam);
        game.setLocation(location);
        game.setPhase(phase);
        game.setDateTime(localDateTime);
        gameRepository.save(game);
    }

    private LocalDateTime parseDateTime(String date) {
        int day = Integer.parseInt(date.split(" ")[1].replace(".", ""));
        String monthStr = date.split(" ")[2];
        int month = this.month.getOrDefault(monthStr, 6);
        int year = Integer.parseInt(date.split(" ")[3].replace(",", ""));
        String time = date.split(" ")[4].replace("(", "");
        int hour = Integer.parseInt(time.split(":")[0]);
        int minute = Integer.parseInt(time.split(":")[1]);
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.withDayOfMonth(day)
                .withMonth(month)
                .withYear(year)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0);
        return localDateTime;
    }

    private String parseLocation(Element gameDetails) {
        String detailsString = gameDetails.textNodes().get(0).text();
        String loc;
        if (gameDetails.childNodeSize() == 1) {
            String[] splited = detailsString.split(" ");
            if (splited.length == 9) { // Kaliningrad is in the same timezone as MESZ
                loc = splited[8];
            } else {
                loc = splited[10];
            }
        } else {
            loc = gameDetails.child(0).ownText();
        }
        return lookupLocationAbreviation(loc);
    }

    private String getPhaseName(int countGroups) {
        String phaseName = "group";
        switch (countGroups) {
            case 8:
                phaseName = "ro16";
                break;
            case 9:
                phaseName = "ro8";
                break;
            case 10:
                phaseName = "semifinals";
                break;
            case 11:
                phaseName = "gameforthird";
                break;
        }
        return phaseName;
    }

    private String getGroupName(int countGroups) {
        String groupName = "A";
        switch (countGroups) {
            case 1:
                groupName = "B";
                break;
            case 2:
                groupName = "C";
                break;
            case 3:
                groupName = "D";
                break;
            case 4:
                groupName = "E";
                break;
            case 5:
                groupName = "F";
                break;
            case 6:
                groupName = "G";
                break;
            case 7:
                groupName = "H";
                break;
        }
        return groupName;
    }

    private Tuple<Integer, Integer> parseScore(String score) {
        String home = score.split(":")[0];
        String away = score.split(":")[1];
        Integer homeScore = null;
        Integer awayScore = null;
        if (!home.contains("-")) {
            homeScore = Integer.parseInt(home);
        }
        if (!away.contains("-")) {
            awayScore = Integer.parseInt(away);
        }
        return new Tuple<>(homeScore, awayScore);
    }

    private String lookupLocationAbreviation(String location) {
        return locations.getOrDefault(location, "tbd");
    }

    private String lookupTeamAbreviation(String team) {
        return teams.getOrDefault(team, "tbd");
    }
}
