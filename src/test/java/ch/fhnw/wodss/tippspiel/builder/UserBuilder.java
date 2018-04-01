package ch.fhnw.wodss.tippspiel.builder;

import ch.fhnw.wodss.tippspiel.domain.Role;
import ch.fhnw.wodss.tippspiel.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class UserBuilder {
    private String username;
    private List<String> roles = new ArrayList<>();
    private long id;

    public UserBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder withRole(String role) {
        roles.add(role);
        return this;
    }

    public UserBuilder withId(long id){
        this.id = id;
        return this;
    }

    public User build() {
        User testUser = new User();
        testUser.setName(username);
        testUser.setId(id);
        Set<Role> roles = this.roles.stream().map(roleName -> {
            Role role = new Role();
            role.setName(roleName);
            return role;
        }).collect(Collectors.toSet());
        testUser.setRoles(roles);
        return testUser;
    }
}
