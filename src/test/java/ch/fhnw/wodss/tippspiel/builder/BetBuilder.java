package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.domain.Bet;
import ch.fhnw.wodss.tippspiel.domain.Game;
import ch.fhnw.wodss.tippspiel.domain.User;

public class BetBuilder {
    private Bet bet;

    public BetBuilder(int homeTeamGoals, int awayTeamGoals, int score, Game game, User user) {
        bet = new Bet(homeTeamGoals, awayTeamGoals, score, game, user);
    }

    public BetBuilder id(Long id) {
        bet.setId(id);
        return this;
    }

    public Bet build() {
        return bet;
    }
}