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
public class RestUserDTO {

    @JsonProperty("name")
    @NotNull
    @Size(min = 1, max = 100)
    private String name;
    @JsonProperty("email")
    @NotNull
    @Size(min = 10, max = 100)
    private String email;
    @JsonProperty("password")
    private String password;
    @JsonProperty("newPassword")
    private String newPassword;
    @JsonProperty("reminders")
    private boolean reminders;
    @JsonProperty("dailyResults")
    private boolean dailyResults;

}
