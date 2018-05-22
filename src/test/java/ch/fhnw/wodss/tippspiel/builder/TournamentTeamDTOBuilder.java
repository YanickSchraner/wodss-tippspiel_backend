package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.dto.TournamentTeamDTO;

public class TournamentTeamDTOBuilder {
    private TournamentTeamDTO tournamentTeam;

    public TournamentTeamDTOBuilder() {
        this.tournamentTeam = new TournamentTeamDTO();
    }

    public TournamentTeamDTOBuilder withName(String name) {
        tournamentTeam.setName(name);
        return this;
    }

    public TournamentTeamDTOBuilder withTournamentGroupName(String tournamentGroupName) {
        tournamentTeam.setTournamentGroupName(tournamentGroupName);
        return this;
    }

    public TournamentTeamDTO build() {
        return tournamentTeam;
    }
}
