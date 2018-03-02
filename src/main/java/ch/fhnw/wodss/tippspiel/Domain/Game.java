package ch.fhnw.wodss.tippspiel.Domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Game {

    @Id
    @GeneratedValue
    @NotNull
    @Column
    private Long id;

    @Column
    @NotNull
    @Min(0)
    @Max(Integer.MAX_VALUE)
    private Integer homeTeamGoals;

    @Column
    @NotNull
    @Min(0)
    @Max(Integer.MAX_VALUE)
    private Integer awayTeamGoals;

    @Column
    @OneToMany(fetch = FetchType.EAGER)
    private TournamentTeam homeTeam;

    @Column
    @OneToMany(fetch = FetchType.EAGER)
    private TournamentTeam awayTeam;

    @Column
    @OneToMany(fetch = FetchType.EAGER)
    private Location location;

    @Column
    @OneToMany(fetch = FetchType.EAGER)
    private Phase phase;

    public Game(Long id, Integer homeTeamGoals, Integer awayTeamGoals, TournamentTeam homeTeam, TournamentTeam awayTeam, Location location, Phase phase) {
        this.id = id;
        this.homeTeamGoals = homeTeamGoals;
        this.awayTeamGoals = awayTeamGoals;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.location = location;
        this.phase = phase;
    }
}