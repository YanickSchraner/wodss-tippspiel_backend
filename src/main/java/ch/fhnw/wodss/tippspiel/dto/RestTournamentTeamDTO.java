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
public class RestTournamentTeamDTO {

    @JsonProperty("name")
    @NotNull
    private String name;
    @JsonProperty("tournamentGroup_id")
    @NotNull
    @Min(0)
    private Long tournamentGroupId;

}
