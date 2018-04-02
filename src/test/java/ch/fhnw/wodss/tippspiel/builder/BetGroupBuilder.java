package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.domain.BetGroup;
import ch.fhnw.wodss.tippspiel.domain.User;

import java.util.ArrayList;
import java.util.List;

public class BetGroupBuilder {
    private BetGroup betGroup;
    private List<User> users = new ArrayList<>();

    public BetGroupBuilder() {
        betGroup = new BetGroup();
    }

    public BetGroupBuilder withId(long id) {
        betGroup.setId(id);
        return this;
    }

    public BetGroupBuilder withName(String name) {
        betGroup.setName(name);
        return this;
    }

    public BetGroupBuilder withMember(User user) {
        users.add(user);
        return this;
    }

    public BetGroupBuilder withPassword(String password) {
        betGroup.setPassword(password);
        return this;
    }

    public BetGroupBuilder withScore(int score) {
        betGroup.setScore(score);
        return this;
    }

    public BetGroup build() {
        betGroup.setMembers(users);
        return betGroup;
    }
}
