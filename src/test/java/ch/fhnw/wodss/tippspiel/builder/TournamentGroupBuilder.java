package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.domain.TournamentGroup;

public class TournamentGroupBuilder {
    private TournamentGroup tournamentGroup;

    public TournamentGroupBuilder() {
        tournamentGroup = new TournamentGroup();
    }

    public TournamentGroupBuilder withId(long id) {
        tournamentGroup.setId(id);
        return this;
    }

    public TournamentGroupBuilder withName(String name) {
        tournamentGroup.setName(name);
        return this;
    }

    public TournamentGroup build() {
        return tournamentGroup;
    }
}
