package ch.fhnw.wodss.tippspiel.configuration;

import lombok.Data;

@Data
public class LoginRequestPOJO {
    private String username;
    private String password;
}
