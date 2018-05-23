package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.domain.Phase;

public class PhaseBuilder {
    private Phase phase;

    public PhaseBuilder() {
        this.phase = new Phase();
    }

    public PhaseBuilder withId(Long id) {
        this.phase.setId(id);
        return this;
    }

    public PhaseBuilder withName(String name) {
        this.phase.setName(name);
        return this;
    }

    public Phase build() {
        return phase;
    }
}
