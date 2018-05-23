package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.dto.UserAllBetGroupDTO;

public class UserAllBetGroupDTOBuilder {
    UserAllBetGroupDTO user;

    public UserAllBetGroupDTOBuilder() {
        user = new UserAllBetGroupDTO();
    }

    public UserAllBetGroupDTOBuilder withId(long id) {
        user.setId(id);
        return this;
    }

    public UserAllBetGroupDTOBuilder withName(String username) {
        user.setName(username);
        return this;
    }

    public UserAllBetGroupDTOBuilder withScore(int score) {
        user.setScore(score);
        return this;
    }


    public UserAllBetGroupDTO build() {
        return user;
    }
}
