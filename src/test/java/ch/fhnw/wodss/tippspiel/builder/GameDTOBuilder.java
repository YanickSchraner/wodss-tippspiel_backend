package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.dto.GameDTO;

public class GameDTOBuilder {
    private GameDTO game;

    public GameDTOBuilder() {
        this.game = new GameDTO();
    }

    public GameDTOBuilder withHomeTeamId(Long homeTeamId) {
        game.setHomeTeamId(homeTeamId);
        return this;
    }

    public GameDTOBuilder withAwayTeamId(Long awayTeamId) {
        game.setAwayTeamId(awayTeamId);
        return this;
    }

    public GameDTOBuilder withHomeTeamName(String homeTeamName) {
        game.setHomeTeamName(homeTeamName);
        return this;
    }

    public GameDTOBuilder withAwayTeamName(String awayTeamName) {
        game.setAwayTeamName(awayTeamName);
        return this;
    }

    public GameDTOBuilder withLocationName(String locationName) {
        game.setLocationName(locationName);
        return this;
    }

    public GameDTOBuilder withPhaseName(String phaseName) {
        game.setPhaseName(phaseName);
        return this;
    }

    public GameDTOBuilder withHomeTeamGoals(int homeTeamGoals) {
        game.setHomeTeamGoals(homeTeamGoals);
        return this;
    }

    public GameDTOBuilder withAwayTeamGoals(int awayTeamGoals) {
        game.setAwayTeamGoals(awayTeamGoals);
        return this;
    }

    public GameDTOBuilder withDate(String date) {
        game.setDate(date);
        return this;
    }

    public GameDTOBuilder withTime(String time) {
        game.setTime(time);
        return this;
    }

    public GameDTO build() {
        return game;
    }
}
