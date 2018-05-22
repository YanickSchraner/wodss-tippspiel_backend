package ch.fhnw.wodss.tippspiel.util;

import ch.fhnw.wodss.tippspiel.domain.*;
import ch.fhnw.wodss.tippspiel.persistance.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

@Component
public class GoogleResultScraper {

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
    public GoogleResultScraper(GameRepository gameRepository, LocationRepository locationRepository, PhaseRepository phaseRepository, TournamentTeamRepository tournamentTeamRepository, TournamentGroupRepository tournamentGroupRepository) {
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
        locations.put("Sankt Petersburg", "san");
        locations.put("Rostow am Don", "ros");
        locations.put("Samara", "sam");
        locations.put("Wolgograd", "wol");
        locations.put("Sotschi", "sot");
        locations.put("Kasan", "kas");
        locations.put("Saransk", "sar");
        locations.put("Kaliningrad", "kal");
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

    public void scrape() {
        Document doc = null;
        try {
            doc = Jsoup.connect(WIKIPEDIA_SEARCH_URL).userAgent("Mozilla/5.0").get();
            Elements groups = doc.select("table.wikitable.zebra.hintergrundfarbe5 > tbody");
            int countGroups = 0;
            for (Element group : groups) {
                countGroups++;
                for (int i = 0; i < group.childNodeSize(); i++) {
                    Element gameDetails = group.child(i).child(0);
                    String dateTime = gameDetails.textNodes().get(0).text();
                    LocalDateTime localDateTime = LocalDateTime.now();
                    int day = Integer.parseInt(dateTime.split(" ")[1].substring(0, 1));
                    String monthStr = dateTime.split(" ")[2];
                    int month = this.month.getOrDefault(monthStr, 6);
                    int year = Integer.parseInt(dateTime.split(" ")[3].replace(",",""));
                    String time = dateTime.split(" ")[5].replace("(", "");
                    int hour = Integer.parseInt(time.split(":")[0]);
                    int minute = Integer.parseInt(time.split(":")[1]);
                    localDateTime.withDayOfMonth(day)
                            .withMonth(month)
                            .withYear(year)
                            .withHour(hour)
                            .withMinute(minute);
                    String loc = gameDetails.child(0).ownText();
                    loc = lookupLocationAbreviation(loc);
                    i++; // Move index to teams and result row
                    Element teamsAndResult = group.child(i);
                    String homeTeamName = teamsAndResult.child(0).ownText();
                    homeTeamName = lookupTeamAbreviation(homeTeamName);
                    String awayTeamName = teamsAndResult.child(2).ownText();
                    awayTeamName = lookupTeamAbreviation(awayTeamName);
                    String score = teamsAndResult.child(3).ownText();
                    String home = score.split(":")[0];
                    String away = score.split(":")[1];
                    Integer homeScore;
                    Integer awayScore;
                    if (home.equals("-")) {
                        homeScore = null;
                    } else {
                        homeScore = Integer.parseInt(home);
                    }
                    if (away.equals("-")) {
                        awayScore = null;
                    } else {
                        awayScore = Integer.parseInt(home);
                    }
                    String phaseName = "";
                    String groupName = "";
                    switch (countGroups) {
                        case 1:
                            groupName = "A";
                            break;
                        case 2:
                            groupName = "B";
                            break;
                        case 3:
                            groupName = "C";
                            break;
                        case 4:
                            groupName = "D";
                            break;
                        case 5:
                            groupName = "E";
                            break;
                        case 6:
                            groupName = "F";
                            break;
                        case 7:
                            groupName = "G";
                            break;
                        case 8:
                            groupName = "H";
                            break;
                        case 9:
                            phaseName = "Achtelfinale";
                            break;
                        case 10:
                            phaseName = "Viertelfinale";
                            break;
                        case 11:
                            phaseName = "Halbfinale";
                            break;
                        case 12:
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
                    TournamentGroup tournamentGroup = tournamentGroupRepository.findByNameEquals(groupName)
                            .orElse(new TournamentGroup(groupName));
                    tournamentGroupRepository.save(tournamentGroup);
                    TournamentTeam homeTeam = tournamentTeamRepository.findTournamentTeamByNameEquals(homeTeamName)
                            .orElse(new TournamentTeam(homeTeamName, tournamentGroup));
                    tournamentTeamRepository.save(homeTeam);
                    TournamentTeam awayTeam = tournamentTeamRepository.findTournamentTeamByNameEquals(awayTeamName)
                            .orElse(new TournamentTeam(awayTeamName, tournamentGroup));
                    tournamentTeamRepository.save(awayTeam);
                    Game game = gameRepository.findFirstByHomeTeamEqualsAndAwayTeamEqualsAndDateTimeEquals(homeTeam, awayTeam, localDateTime)
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
