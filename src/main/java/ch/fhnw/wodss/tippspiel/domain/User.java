package ch.fhnw.wodss.tippspiel.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
public class User implements UserDetails {

    @Column
    @NotNull
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<Bet> bets;

    @Column
    @NotNull
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "members")
    private List<BetGroup> betGroups;

    @Id
    @GeneratedValue
    @Column
    private Long id;

    @Column
    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @Column
    @NotNull
    @Size(min = 10, max = 1024)
    private String password;

    @Column
    @NotNull
    @Size(min = 10, max = 100)
    private String email;

    @Column
    private boolean reminders = true;

    @Column
    private boolean dailyResults = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User(String name, String password, String email, List<Bet> bets, List<BetGroup> betGroups, boolean reminders, boolean dailyResults, Set<Role> roles) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.bets = bets;
        this.betGroups = betGroups;
        this.reminders = reminders;
        this.dailyResults = dailyResults;
        this.roles = roles;
    }

    public boolean hasBet(long id) {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        HashSet<GrantedAuthority> authorities = new HashSet<>();
        if (roles != null) {
            roles.stream()
                    .map(Role::getName)
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }
        return authorities;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof User))return false;
        User otherUser = (User) other;
        return otherUser.getId().equals(this.getId());
    }

    @Override
    public String toString() {
        return getUsername();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
