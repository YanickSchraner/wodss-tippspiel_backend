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

    public GameBuilder withId(Long id) {
        game.setId(id);
        return this;
    }

    public GameBuilder withDateTime(Date date) {
        game.setDateTime(date);
        return this;
    }

    public GameBuilder withHomeTeamGoals(int homeTeamGoals) {
        game.setHomeTeamGoals(homeTeamGoals);
        return this;
    }

    public GameBuilder withAwayTeamGoals(int awayTeamGoals) {
        game.setAwayTeamGoals(awayTeamGoals);
        return this;
    }

    public GameBuilder withHomeTeam(TournamentTeam homeTeam) {
        game.setHomeTeam(homeTeam);
        return this;
    }

    public GameBuilder withAwayTeam(TournamentTeam awayTeam) {
        game.setAwayTeam(awayTeam);
        return this;
    }

    public GameBuilder withLocation(String  name) {
        Location location = new Location();
        location.setName(name);
        game.setLocation(location);
        return this;
    }

    public GameBuilder withPhase(String name) {
        Phase phase = new Phase();
        phase.setName(name);
        game.setPhase(phase);
        return this;
    }

    public Game build() {
        return game;
    }
}