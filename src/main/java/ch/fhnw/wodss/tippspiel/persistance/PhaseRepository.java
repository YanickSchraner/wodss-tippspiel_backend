package ch.fhnw.wodss.tippspiel.persistance;

import ch.fhnw.wodss.tippspiel.domain.Phase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhaseRepository extends JpaRepository<Phase, Long> {
    Optional<Phase> findFirstByNameEquals(String name);
}
