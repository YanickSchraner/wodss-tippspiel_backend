package ch.fhnw.wodss.tippspiel.configuration;

import ch.fhnw.wodss.tippspiel.security.Argon2PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    @Autowired
    private Argon2PasswordEncoder argon2PasswordEncoder;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return argon2PasswordEncoder;
    }
}
