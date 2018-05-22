package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.dto.RestBetGroupDTO;

public class RestBetGroupDTOBuilder {
    private RestBetGroupDTO bet;

    public RestBetGroupDTOBuilder() {
        this.bet = new RestBetGroupDTO();
    }

    public RestBetGroupDTOBuilder withName(String name) {
        bet.setName(name);
        return this;
    }

    public RestBetGroupDTOBuilder withPassword(String password) {
        bet.setPassword(password);
        return this;
    }

    public RestBetGroupDTO build() {
        return bet;
    }
}
