package ch.fhnw.wodss.tippspiel.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@Entity
public class TournamentTeam {

    @Id
    @GeneratedValue
    @NotNull
    @Column
    private Long id;

    @Column
    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @JoinColumn
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private TournamentGroup group;

    public TournamentTeam(Long id, String name, TournamentGroup group) {
        this.id = id;
        this.name = name;
        this.group = group;
    }
}