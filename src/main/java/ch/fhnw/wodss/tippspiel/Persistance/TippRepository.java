package ch.fhnw.wodss.tippspiel.Persistance;

import ch.fhnw.wodss.tippspiel.Domain.Tipp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TippRepository extends JpaRepository<Tipp, Long> {
}
