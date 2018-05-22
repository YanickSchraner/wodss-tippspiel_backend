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
        locations.put("Rostow", "ros");
        locations.put("Samara", "sam");
        locations.put("Wolgograd", "wol");
        locations.put("Sotschi", "sot");
        locations.put("Kasan", "kas");
        locations.put("Saransk", "sar");
        locations.put("Kaliningrad", "kal");
        locations.put("Nischni", "nis");

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
    public void scrape() {
        Document doc = null;
        try {
            doc = Jsoup.connect(WIKIPEDIA_SEARCH_URL).userAgent("Mozilla/5.0").get();
            Elements groups = doc.select("table.wikitable.zebra.hintergrundfarbe5 > tbody");
            for (int countGroups = 0; countGroups < groups.size(); countGroups++) {
                Element group = groups.get(countGroups);
                for (int i = 0; i < group.children().size(); i++) {
                    Element gameDetails = group.child(i).child(0);
                    String detailsString = gameDetails.textNodes().get(0).text();
                    String loc = "";
                    if (gameDetails.childNodeSize() == 1) {
                        String[] splitted = detailsString.split(" ");
                        if (splitted.length == 9) { // Kaliningrad is in the same timezone as MESZ
                            loc = splitted[8];
                        } else {
                            loc = splitted[10];
                        }
                        loc = lookupLocationAbreviation(loc);
                    } else {
                        loc = gameDetails.child(0).ownText();
                        loc = lookupLocationAbreviation(loc);
                    }
                    int day = Integer.parseInt(detailsString.split(" ")[1].replace(".", ""));
                    String monthStr = detailsString.split(" ")[2];
                    int month = this.month.getOrDefault(monthStr, 6);
                    int year = Integer.parseInt(detailsString.split(" ")[3].replace(",", ""));
                    String time = detailsString.split(" ")[4].replace("(", "");
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
                    i++; // Move index to teams and result row
                    Element teamsAndResult = group.child(i);
                    String homeTeamName = teamsAndResult.child(0).ownText();
                    homeTeamName = lookupTeamAbreviation(homeTeamName);
                    String awayTeamName = teamsAndResult.child(2).ownText();
                    awayTeamName = lookupTeamAbreviation(awayTeamName);
                    String score = teamsAndResult.child(3).ownText();
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
                    String phaseName = "Gruppenphase";
                    String groupName = "";
                    switch (countGroups) {
                        case 0:
                            groupName = "A";
                            break;
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
                        case 8:
                            phaseName = "Achtelfinale";
                            break;
                        case 9:
                            phaseName = "Viertelfinale";
                            break;
                        case 10:
                            phaseName = "Halbfinale";
                            break;
                        case 11:
                            phaseName = "Spiel um Platz 3";
                            break;
                        default:
                            phaseName = "Gruppenphase";
                            groupName = "A";
                            break;
                    }
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String lookupLocationAbreviation(String location) {
        return locations.getOrDefault(location, "tbd");
    }

    private String lookupTeamAbreviation(String team) {
        return teams.getOrDefault(team, "tbd");
    }
}
