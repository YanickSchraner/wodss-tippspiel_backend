package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.domain.Bet;
import ch.fhnw.wodss.tippspiel.domain.BetGroup;
import ch.fhnw.wodss.tippspiel.domain.Role;
import ch.fhnw.wodss.tippspiel.domain.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class UserBuilder {
    Set<String> roles = new HashSet<>();
    List<Bet> bets = new ArrayList<>();
    List<BetGroup> betGroups = new ArrayList<>();
    User user;

    public UserBuilder(){
        user = new User();
    }

    public UserBuilder withId(long id){
        user.setId(id);
        return this;
    }

    public UserBuilder withName(String username) {
        user.setName(username);
        return this;
    }

    public UserBuilder withRole(String role) {
        roles.add(role);
        return this;
    }

    public UserBuilder withPassword(String password) {
        user.setPassword(password);
        return this;
    }

    public UserBuilder withEmail(String email){
        user.setEmail(email);
        return this;
    }

    public UserBuilder withBet(Bet bet){
        bets.add(bet);
        return this;
    }

    public UserBuilder withBetGroup(BetGroup betGroup){
        betGroups.add(betGroup);
        return this;
    }

    public UserBuilder withReminders(boolean reminders){
        user.setReminders(reminders);
        return this;
    }

    public UserBuilder withDailyResults(boolean dailyResults){
        user.setDailyResults(dailyResults);
        return this;
    }

    public User build() {
        Set<Role> roles = this.roles.stream().map(roleName -> {
            Role role = new Role();
            role.setName(roleName);
            return role;
        }).collect(Collectors.toSet());
        user.setRoles(roles);
        user.setBetGroup(betGroups);
        user.setBets(bets);
        return user;
    }
}
