package ch.fhnw.wodss.tippspiel.Persistance;

import ch.fhnw.wodss.tippspiel.Domain.TournamentGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TournamentGroupRepository extends JpaRepository<TournamentGroup, Long> {
    Optional<TournamentGroup> findByNameEquals(String name);
}
