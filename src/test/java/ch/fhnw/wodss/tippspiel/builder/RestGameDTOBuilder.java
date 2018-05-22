package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.dto.RestGameDTO;

public class RestGameDTOBuilder {
    private RestGameDTO game;

    public RestGameDTOBuilder() {
        this.game = new RestGameDTO();
    }

    public RestGameDTOBuilder withHomeTeamId(Long homeTeamId) {
        game.setHomeTeamId(homeTeamId);
        return this;
    }

    public RestGameDTOBuilder withAwayTeamId(Long awayTeamId) {
        game.setAwayTeamId(awayTeamId);
        return this;
    }

    public RestGameDTOBuilder withLocationId(Long locationId) {
        game.setLocationId(locationId);
        return this;
    }

    public RestGameDTOBuilder withPhaseId(Long phaseId) {
        game.setPhaseId(phaseId);
        return this;
    }

    public RestGameDTOBuilder withDate(String date) {
        game.setDate(date);
        return this;
    }

    public RestGameDTOBuilder withTime(String time) {
        game.setTime(time);
        return this;
    }

    public RestGameDTO build() {
        return game;
    }

}
