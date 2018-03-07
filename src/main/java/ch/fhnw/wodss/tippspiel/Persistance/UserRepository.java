package ch.fhnw.wodss.tippspiel.Persistance;

import ch.fhnw.wodss.tippspiel.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByNameEquals(String name);

    Optional<User> findUserByEmailEquals(String email);

}
