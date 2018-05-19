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
public class RestGameDTO {

    @JsonProperty("homeTeam_id")
    @NotNull
    @Min(0)
    private long homeTeamId;
    @JsonProperty("awayTeam_id")
    @NotNull
    @Min(0)
    private long awayTeamId;
    @JsonProperty("location_id")
    @NotNull
    @Min(0)
    private long locationId;
    @JsonProperty("phase_id")
    @NotNull
    @Min(0)
    private long phaseId;

}
