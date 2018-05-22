package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.dto.BetGroupDTO;

import java.util.List;

public class BetGroupDTOBuilder {
    private BetGroupDTO bet;

    public BetGroupDTOBuilder() {
        this.bet = new BetGroupDTO();
    }

    public BetGroupDTOBuilder withId(Long id) {
        bet.setId(id);
        return this;
    }

    public BetGroupDTOBuilder withName(String name) {
        bet.setName(name);
        return this;
    }

    public BetGroupDTOBuilder withScore(int score) {
        bet.setScore(score);
        return this;
    }

    public BetGroupDTOBuilder withUserIds(List<Long> userIds) {
        bet.setUserIds(userIds);
        return this;
    }

    public BetGroupDTO build() {
        return bet;
    }

}
