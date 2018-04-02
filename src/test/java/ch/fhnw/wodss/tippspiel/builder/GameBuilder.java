package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.domain.Game;
import ch.fhnw.wodss.tippspiel.domain.Location;
import ch.fhnw.wodss.tippspiel.domain.Phase;
import ch.fhnw.wodss.tippspiel.domain.TournamentTeam;

import java.util.Date;

public class GameBuilder {
    private Game game;

    public GameBuilder() {
        game = new Game();
    }

    public GameBuilder id(Long id) {
        game.setId(id);
        return this;
    }

    public GameBuilder dateTime(Date date) {
        game.setDateTime(date);
        return this;
    }

    public GameBuilder homeTeamGoals(int homeTeamGoals) {
        game.setHomeTeamGoals(homeTeamGoals);
        return this;
    }

    public GameBuilder awayTeamGoals(int awayTeamGoals) {
        game.setAwayTeamGoals(awayTeamGoals);
        return this;
    }

    public GameBuilder homeTeam(TournamentTeam homeTeam) {
        game.setHomeTeam(homeTeam);
        return this;
    }

    public GameBuilder awayTeam(TournamentTeam awayTeam) {
        game.setAwayTeam(awayTeam);
        return this;
    }

    public GameBuilder location(Location location) {
        game.setLocation(location);
        return this;
    }

    public GameBuilder phase(Phase phase) {
        game.setPhase(phase);
        return this;
    }

    public Game build() {
        return game;
    }
}