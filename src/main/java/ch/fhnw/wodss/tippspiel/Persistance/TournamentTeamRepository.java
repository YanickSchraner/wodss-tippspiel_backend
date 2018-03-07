package ch.fhnw.wodss.tippspiel.Persistance;

import ch.fhnw.wodss.tippspiel.Domain.TournamentTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TournamentTeamRepository extends JpaRepository<TournamentTeam, Long> {

    Optional<TournamentTeam> getTournamentTeamByName(String name);

    boolean existsTournamentTeamsByGroup_Id(Long tournamentGroupId);

}
