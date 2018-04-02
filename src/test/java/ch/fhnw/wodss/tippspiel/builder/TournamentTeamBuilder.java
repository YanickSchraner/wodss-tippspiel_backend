package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.domain.TournamentGroup;
import ch.fhnw.wodss.tippspiel.domain.TournamentTeam;

public class TournamentTeamBuilder {
    private TournamentTeam tournamentTeam;

    public TournamentTeamBuilder() {
        tournamentTeam = new TournamentTeam();
    }

    public TournamentTeamBuilder withId(long id) {
        tournamentTeam.setId(id);
        return this;
    }

    public TournamentTeamBuilder withName(String name) {
        tournamentTeam.setName(name);
        return this;
    }

    public TournamentTeamBuilder withGroup(TournamentGroup tournamentGroup) {
        tournamentTeam.setGroup(tournamentGroup);
        return this;
    }

    public TournamentTeam build() {
        return tournamentTeam;
    }
}
