package ch.fhnw.wodss.tippspiel.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonAutoDetect
@NoArgsConstructor
public class TournamentTeamDTO {

    @JsonProperty("name")
    private String name;
    @JsonProperty("tournamentGroupName")
    private String tournamentGroupName;

}
