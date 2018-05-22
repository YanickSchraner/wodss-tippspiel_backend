package ch.fhnw.wodss.tippspiel.persistance;

import ch.fhnw.wodss.tippspiel.domain.Game;
import ch.fhnw.wodss.tippspiel.domain.TournamentTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    boolean existsGameByHomeTeamAndAwayTeamAndDateTimeEquals(TournamentTeam homeTeam, TournamentTeam awayTeam, LocalDateTime dateTime);
    List<Game> getAllByDateTimeIsBetween(LocalDateTime start, LocalDateTime end);
    Optional<Game> findFirstByHomeTeamEqualsAndAwayTeamEqualsAndDateTimeIsBetween(TournamentTeam homeTeamm, TournamentTeam awayTeam, LocalDateTime start, LocalDateTime end);
}
