package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.dto.RestTournamentTeamDTO;

public class RestTournamentTeamDTOBuilder {
    private RestTournamentTeamDTO tournamentTeam;

    public RestTournamentTeamDTOBuilder() {
        this.tournamentTeam = new RestTournamentTeamDTO();
    }

    public RestTournamentTeamDTOBuilder withName(String name) {
        tournamentTeam.setName(name);
        return this;
    }

    public RestTournamentTeamDTOBuilder withTournamentGroupId(long tournamentGroupId) {
        tournamentTeam.setTournamentGroupId(tournamentGroupId);
        return this;
    }

    public RestTournamentTeamDTO build() {
        return tournamentTeam;
    }

}
