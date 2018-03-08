package ch.fhnw.wodss.tippspiel.persistance;

import ch.fhnw.wodss.tippspiel.domain.Game;
import ch.fhnw.wodss.tippspiel.domain.TournamentTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface GameRepository extends JpaRepository<Game, Long> {
    boolean existsGameByHomeTeamAndAwayTeamAndDateTimeEquals(TournamentTeam homeTeam, TournamentTeam awayTeam, Date dateTime);
}
