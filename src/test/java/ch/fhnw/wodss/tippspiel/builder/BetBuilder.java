package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.domain.Bet;
import ch.fhnw.wodss.tippspiel.domain.Game;
import ch.fhnw.wodss.tippspiel.domain.User;

public class BetBuilder {
    private Bet bet;

    public BetBuilder() {
        this.bet = new Bet();
    }

    public BetBuilder id(Long id) {
        bet.setId(id);
        return this;
    }

    public BetBuilder homeTeamGoals(int homeTeamGoals) {
        bet.setHomeTeamGoals(homeTeamGoals);
        return this;
    }

    public BetBuilder awayTeamGoals(int awayTeamGoals) {
        bet.setAwayTeamGoals(awayTeamGoals);
        return this;
    }

    public BetBuilder score(int score) {
        bet.setScore(score);
        return this;
    }

    public BetBuilder game(Game game) {
        bet.setGame(game);
        return this;
    }

    public BetBuilder user(User user) {
        bet.setUser(user);
        return this;
    }

    public Bet build() {
        return bet;
    }
}