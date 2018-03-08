package ch.fhnw.wodss.tippspiel.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue
    @NotNull
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
    @NotNull
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    List<Bet> bets;

    @Column
    @NotNull
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_group", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    List<BetGroup> betGroup;

    @Column
    private boolean reminders = true;

    @Column
    private boolean dailyResults = true;

    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User(Long id, String name, String password,  String email, List<Bet> bets, List<BetGroup> betGroup, boolean reminders, boolean dailyResults, Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.bets = bets;
        this.betGroup = betGroup;
        this.reminders = reminders;
        this.dailyResults = dailyResults;
        this.roles = roles;
    }
}
