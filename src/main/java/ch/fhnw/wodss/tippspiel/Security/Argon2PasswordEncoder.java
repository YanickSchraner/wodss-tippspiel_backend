package ch.fhnw.wodss.tippspiel.Security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Argon2PasswordEncoder implements PasswordEncoder {

    private static final Argon2 ARGON2 = Argon2Factory.create();

    // Parameters set to use as much CPU and Memory on the server and get a hashing time of 1 second.
    private static final int ITERATIONS = 3000;
    private static final int MEMORY = 524288; // About 512 MB RAM
    private static final int PARALLELISM = 2;

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
