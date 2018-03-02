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
public class BetGroup {

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
    @Size(min = 10, max = 300)
    private String password;

    @Column
    @Min(0)
    @Max(Integer.MAX_VALUE)
    private Integer score;

    @Column
    @ManyToMany(fetch = FetchType.EAGER)
    private List<User> members;

    public BetGroup(Long id, String name, String password, Integer score, List<User> members){
        this.id = id;
        this.name = name;
        this.password = password;
        this.score = score;
        this.members = members;
    }
}
