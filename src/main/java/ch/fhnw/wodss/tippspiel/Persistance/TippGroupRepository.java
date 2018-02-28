package ch.fhnw.wodss.tippspiel.Persistance;

import ch.fhnw.wodss.tippspiel.Domain.TippGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TippGroupRepository extends JpaRepository<TippGroup, Long> {
}
