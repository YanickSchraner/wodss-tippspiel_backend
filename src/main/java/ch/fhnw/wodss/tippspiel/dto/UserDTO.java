package ch.fhnw.wodss.tippspiel.dto;

import ch.fhnw.wodss.tippspiel.domain.Role;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@JsonAutoDetect
@NoArgsConstructor
public class UserDTO {

    @JsonProperty("id")
    private long id;
    @JsonProperty("bets")
    private List<BetDTO> bets;
    @JsonProperty("betGroups")
    private List<BetGroupDTO> betGroups;
    @JsonProperty("name")
    private String name;
    @JsonProperty("password")
    private String password;
    @JsonProperty("email")
    private String email;
    @JsonProperty("reminders")
    private Boolean reminders;
    @JsonProperty("dailyResults")
    private Boolean dailyResults;
    @JsonProperty("role")
    private Set<Role> role;

}
