package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.domain.Game;
import ch.fhnw.wodss.tippspiel.domain.Location;
import ch.fhnw.wodss.tippspiel.domain.Phase;
import ch.fhnw.wodss.tippspiel.domain.TournamentTeam;

import java.util.Date;

public class GameBuilder {
    private Game game;

    public GameBuilder(Date dateTime, Integer homeTeamGoals, Integer awayTeamGoals, TournamentTeam homeTeam, TournamentTeam awayTeam, Location location, Phase phase) {
        game = new Game(dateTime, homeTeamGoals, awayTeamGoals, homeTeam, awayTeam, location, phase);
    }

    public GameBuilder id(Long id) {
        game.setId(id);
        return this;
    }

    public Game build() {
        return game;
    }
}