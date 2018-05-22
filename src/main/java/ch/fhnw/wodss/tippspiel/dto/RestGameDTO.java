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
    private Long homeTeamId;
    @JsonProperty("awayTeam_id")
    @NotNull
    @Min(0)
    private Long awayTeamId;
    @JsonProperty("location_id")
    @NotNull
    @Min(0)
    private Long locationId;
    @JsonProperty("phase_id")
    @NotNull
    @Min(0)
    private Long phaseId;
    @JsonProperty("date")
    @NotNull
    private String date;
    @JsonProperty("time")
    @NotNull
    private String time;

}
