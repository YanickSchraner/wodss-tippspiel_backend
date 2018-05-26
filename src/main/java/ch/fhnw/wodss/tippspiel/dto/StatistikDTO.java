package ch.fhnw.wodss.tippspiel.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonAutoDetect
@NoArgsConstructor
public class StatistikDTO {
    @JsonProperty("started")
    private Boolean gameStarted;

    @JsonProperty("homeWin")
    private Integer homeWin;

    @JsonProperty("draw")
    private Integer draw;

    @JsonProperty("homeLose")
    private Integer homeLose;
}
