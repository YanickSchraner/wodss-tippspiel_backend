package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.dto.BetDTO;

public class BetDTOBuilder {
    private BetDTO bet;

    public BetDTOBuilder() {
        this.bet = new BetDTO();
    }

    public BetDTOBuilder withId(Long id) {
        bet.setId(id);
        return this;
    }

    public BetDTOBuilder withBettedHomeTeamGoals(int homeTeamGoals) {
        bet.setBettedHomeTeamGoals(homeTeamGoals);
        return this;
    }

    public BetDTOBuilder withActualHomeTeamGoals(int homeTeamGoals) {
        bet.setActualHomeTeamGoals(homeTeamGoals);
        return this;
    }

    public BetDTOBuilder withBettedAwayTeamGoals(int awayTeamGoals) {
        bet.setBettedAwayTeamGoals(awayTeamGoals);
        return this;
    }

    public BetDTOBuilder withActualAwayTeamGoals(int awayTeamGoals) {
        bet.setActualAwayTeamGoals(awayTeamGoals);
        return this;
    }

    public BetDTOBuilder withScore(int score) {
        bet.setScore(score);
        return this;
    }

    public BetDTOBuilder withGame(Long game) {
        bet.setGameId(game);
        return this;
    }

    public BetDTOBuilder withUserId(Long user) {
        bet.setUserId(user);
        return this;
    }

    public BetDTOBuilder withUserName(String name) {
        bet.setUsername(name);
        return this;
    }

    public BetDTOBuilder withAwayTeamId(Long id) {
        bet.setAwayTeamId(id);
        return this;
    }

    public BetDTOBuilder withHomeTeamId(Long id) {
        bet.setHomeTeamId(id);
        return this;
    }

    public BetDTOBuilder withLocation(String location) {
        bet.setLocation(location);
        return this;
    }

    public BetDTOBuilder withPhase(String phase) {
        bet.setPhase(phase);
        return this;
    }

    public BetDTO build() {
        return bet;
    }
}
