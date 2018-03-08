package ch.fhnw.wodss.tippspiel.persistance;

import ch.fhnw.wodss.tippspiel.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByNameEquals(String name);

    Optional<User> findUserByEmailEquals(String email);

}
