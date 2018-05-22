package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.dto.RestBetDTO;

public class RestBetDTOBuilder {
    private RestBetDTO bet;

    public RestBetDTOBuilder() {
        this.bet = new RestBetDTO();
    }

    public RestBetDTOBuilder withGameId(Long id) {
        bet.setGameId(id);
        return this;
    }

    public RestBetDTOBuilder withHomeTeamGoals(int homeTeamGoals) {
        bet.setHomeTeamGoals(homeTeamGoals);
        return this;
    }

    public RestBetDTOBuilder withAwayTeamGoals(int awayTeamGoals) {
        bet.setAwayTeamGoals(awayTeamGoals);
        return this;
    }

    public RestBetDTO build() {
        return bet;
    }

}
