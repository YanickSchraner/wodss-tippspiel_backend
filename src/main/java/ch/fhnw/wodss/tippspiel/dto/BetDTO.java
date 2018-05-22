package ch.fhnw.wodss.tippspiel.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonAutoDetect
@NoArgsConstructor
public class BetDTO {

    @JsonProperty("id")
    private long id;
    @JsonProperty("user_id")
    private long userId;
    @JsonProperty("game_id")
    private long gameId;
    @JsonProperty("username")
    private String username;
    @JsonProperty("awayTeamId")
    private long awayTeamId;
    @JsonProperty("homeTeamId")
    private long homeTeamId;
    @JsonProperty("bettedHomeTeamGoals")
    private int bettedHomeTeamGoals;
    @JsonProperty("bettedAwayTeamGoals")
    private int bettedAwayTeamGoals;
    @JsonProperty("actualHomeTeamGoals")
    private Integer actualHomeTeamGoals;
    @JsonProperty("actualAwayTeamGoals")
    private Integer actualAwayTeamGoals;
    @JsonProperty("score")
    private Integer score;
    @JsonProperty("location")
    private String location;
    @JsonProperty("phase")
    private String phase;

}
