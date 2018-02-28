package ch.fhnw.wodss.tippspiel.Persistance;

import ch.fhnw.wodss.tippspiel.Domain.BetGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BetGroupRepository extends JpaRepository<BetGroup, Long> {
}
