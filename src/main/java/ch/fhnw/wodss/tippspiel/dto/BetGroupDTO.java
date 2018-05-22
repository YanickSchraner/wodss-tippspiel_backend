package ch.fhnw.wodss.tippspiel.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonAutoDetect
@Data
@NoArgsConstructor
public class BetGroupDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("score")
    private Integer score;

    @JsonProperty("user_ids")
    private List<Long> userIds;

}
