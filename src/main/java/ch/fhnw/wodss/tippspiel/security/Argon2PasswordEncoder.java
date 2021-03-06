package ch.fhnw.wodss.tippspiel.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Argon2PasswordEncoder implements PasswordEncoder {

    private static final Argon2 ARGON2 = Argon2Factory.create();

    @Value("${security.argon2.iterations}")
    private int ITERATIONS;
    @Value("${security.argon2.memory}")
    private int MEMORY;
    @Value("${security.argon2.parallelism}")
    private int PARALLELISM;

    @Override
    public String encode(CharSequence rawPassword) {
        final String hash = ARGON2.hash(ITERATIONS, MEMORY, PARALLELISM, rawPassword.toString());
        return hash;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return ARGON2.verify(encodedPassword, rawPassword.toString());
    }
}
