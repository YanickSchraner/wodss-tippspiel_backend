package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.dto.BetDTO;
import ch.fhnw.wodss.tippspiel.dto.BetGroupDTO;
import ch.fhnw.wodss.tippspiel.dto.UserRankingDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserRankingDTOBuilder {
    Set<String> roles = new HashSet<>();
    List<BetDTO> bets = new ArrayList<>();
    List<BetGroupDTO> betGroups = new ArrayList<>();
    UserRankingDTO user;

    public UserRankingDTOBuilder() {
        user = new UserRankingDTO();
    }

    public UserRankingDTOBuilder withId(long id) {
        user.setId(id);
        return this;
    }

    public UserRankingDTOBuilder withName(String username) {
        user.setName(username);
        return this;
    }


    public UserRankingDTOBuilder withScore(int score) {
        user.setScore(score);
        return this;
    }


    public UserRankingDTO build() {
        return user;
    }

}
