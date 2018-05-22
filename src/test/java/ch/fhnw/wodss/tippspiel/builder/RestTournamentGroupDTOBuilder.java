package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.dto.RestTournamentGroupDTO;

public class RestTournamentGroupDTOBuilder {
    private RestTournamentGroupDTO tournamentGroup;

    public RestTournamentGroupDTOBuilder() {
        this.tournamentGroup = new RestTournamentGroupDTO();
    }

    public RestTournamentGroupDTOBuilder withName(String name) {
        tournamentGroup.setName(name);
        return this;
    }

    public RestTournamentGroupDTO build() {
        return tournamentGroup;
    }
}
