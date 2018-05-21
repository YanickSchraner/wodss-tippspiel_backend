package ch.fhnw.wodss.tippspiel.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@JsonAutoDetect
@NoArgsConstructor
public class RestBetGroupDTO {

    @JsonProperty("name")
    @NotNull
    private String name;

    @JsonProperty("password")
    @Size(min = 10, max = 1024)
    private String password;

}
