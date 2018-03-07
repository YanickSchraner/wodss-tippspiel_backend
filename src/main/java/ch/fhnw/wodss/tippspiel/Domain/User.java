package ch.fhnw.wodss.tippspiel.Domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

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
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "Bet")
    List<Bet> bets;

    @Column
    @NotNull
    @ManyToMany(fetch = FetchType.EAGER)
    List<BetGroup> betGroup;

    @Column
    private boolean reminders = true;

    @Column
    private boolean dailyResults = true;

    public User(Long id, String name, String password,  String email, List<Bet> bets, List<BetGroup> betGroup, boolean reminders, boolean dailyResults) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.bets = bets;
        this.betGroup = betGroup;
        this.reminders = reminders;
        this.dailyResults = dailyResults;
    }
}
