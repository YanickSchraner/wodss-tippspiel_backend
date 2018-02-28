package ch.fhnw.wodss.tippspiel.Persistance;

import ch.fhnw.wodss.tippspiel.Domain.TournamentGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentGroupRepository extends JpaRepository<TournamentGroup, Long> {
}
