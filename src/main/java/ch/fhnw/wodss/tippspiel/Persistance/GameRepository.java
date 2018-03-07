package ch.fhnw.wodss.tippspiel.Persistance;

import ch.fhnw.wodss.tippspiel.Domain.Game;
import ch.fhnw.wodss.tippspiel.Domain.TournamentTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface GameRepository extends JpaRepository<Game, Long> {
    public boolean existsGameByHomeTeamAndAwayTeamAndDateTimeEquals(TournamentTeam homeTeam, TournamentTeam awayTeam, Date dateTime);
}
