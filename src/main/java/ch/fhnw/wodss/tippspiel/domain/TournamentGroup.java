package ch.fhnw.wodss.tippspiel.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@Entity
public class TournamentGroup {

    @Id
    @GeneratedValue
    @Column
    private Long id;

    @Column
    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    public TournamentGroup(String name) {
        this.name = name;
    }
}