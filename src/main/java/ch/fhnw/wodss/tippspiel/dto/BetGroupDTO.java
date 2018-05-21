package ch.fhnw.wodss.tippspiel.dto;

import ch.fhnw.wodss.tippspiel.domain.User;
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
    private int score;

    @JsonProperty("user_ids")
    private List<Long> userIds;

}
