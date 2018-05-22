package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.domain.Role;
import ch.fhnw.wodss.tippspiel.dto.BetDTO;
import ch.fhnw.wodss.tippspiel.dto.BetGroupDTO;
import ch.fhnw.wodss.tippspiel.dto.UserDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDTOBuilder {
    Set<String> roles = new HashSet<>();
    List<BetDTO> bets = new ArrayList<>();
    List<BetGroupDTO> betGroups = new ArrayList<>();
    UserDTO user;

    public UserDTOBuilder(){
        user = new UserDTO();
    }

    public UserDTOBuilder withId(long id){
        user.setId(id);
        return this;
    }

    public UserDTOBuilder withName(String username) {
        user.setName(username);
        return this;
    }

    public UserDTOBuilder withRole(String role) {
        roles.add(role);
        return this;
    }

    public UserDTOBuilder withPassword(String password) {
        user.setPassword(password);
        return this;
    }

    public UserDTOBuilder withEmail(String email){
        user.setEmail(email);
        return this;
    }

    public UserDTOBuilder withBet(BetDTO bet){
        bets.add(bet);
        return this;
    }

    public UserDTOBuilder withBetGroup(BetGroupDTO betGroup){
        betGroups.add(betGroup);
        return this;
    }

    public UserDTOBuilder withReminders(boolean reminders){
        user.setReminders(reminders);
        return this;
    }

    public UserDTOBuilder withDailyResults(boolean dailyResults){
        user.setDailyResults(dailyResults);
        return this;
    }

    public UserDTO build() {
        Set<Role> roles = this.roles.stream().map(roleName -> {
            Role role = new Role();
            role.setName(roleName);
            return role;
        }).collect(Collectors.toSet());
        user.setRole(roles);
        user.setBetGroups(betGroups);
        user.setBets(bets);
        return user;
    }
}
