package ch.fhnw.wodss.tippspiel.persistance;

import ch.fhnw.wodss.tippspiel.domain.TournamentTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TournamentTeamRepository extends JpaRepository<TournamentTeam, Long> {

    Optional<TournamentTeam> findTournamentTeamByNameEquals(String name);

    boolean existsTournamentTeamsByGroup_Id(Long tournamentGroupId);

    @Query("SELECT count(game) FROM Game game WHERE game.homeTeam.id = :id OR game.awayTeam.id = :id")
    boolean hasGames(@Param("id") Long id);

}
