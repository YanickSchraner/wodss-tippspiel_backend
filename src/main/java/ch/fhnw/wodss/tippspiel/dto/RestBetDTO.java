package ch.fhnw.wodss.tippspiel.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@JsonAutoDetect
@NoArgsConstructor
public class RestBetDTO {

    @JsonProperty("game_id")
    @NotNull
    @Min(0)
    private long gameId;
    @JsonProperty("homeTeamGoals")
    @NotNull
    @Min(0)
    private int homeTeamGoals;
    @JsonProperty("awayTeamGoals")
    @NotNull
    @Min(0)
    private int awayTeamGoals;

}
