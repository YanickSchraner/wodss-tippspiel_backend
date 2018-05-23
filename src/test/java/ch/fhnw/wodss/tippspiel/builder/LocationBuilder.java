package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.domain.Location;

public class LocationBuilder {
    private Location location;

    public LocationBuilder() {
        this.location = new Location();
    }

    public LocationBuilder withName(String name) {
        this.location.setName(name);
        return this;
    }

    public LocationBuilder withId(Long id) {
        this.location.setId(id);
        return this;
    }

    public Location build() {
        return this.location;
    }
}
