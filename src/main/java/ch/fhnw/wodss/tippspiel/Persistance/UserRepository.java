package ch.fhnw.wodss.tippspiel.Persistance;

import ch.fhnw.wodss.tippspiel.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User getUserByName(String name);

}
