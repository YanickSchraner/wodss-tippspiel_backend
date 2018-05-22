package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.dto.TournamentGroupDTO;

public class TournamentGroupDTOBuilder {
    private TournamentGroupDTO tournamentGroup;

    public TournamentGroupDTOBuilder() {
        this.tournamentGroup = new TournamentGroupDTO();
    }

    public TournamentGroupDTOBuilder withId(Long id) {
        tournamentGroup.setId(id);
        return this;
    }

    public TournamentGroupDTOBuilder withName(String name) {
        tournamentGroup.setName(name);
        return this;
    }

    public TournamentGroupDTO build() {
        return tournamentGroup;
    }
}
