package ch.fhnw.wodss.tippspiel.Services;

import ch.fhnw.wodss.tippspiel.Domain.TournamentTeam;
import ch.fhnw.wodss.tippspiel.Persistance.TournamentTeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TournamentTeamService {

    private final TournamentTeamRepository repository;

    @Autowired
    public TournamentTeamService(TournamentTeamRepository repository) {
        this.repository = repository;
    }

    public List<TournamentTeam> getAllTournamentTeams() {
        return null;
    }

    public TournamentTeam getTournamentTeamById(Long id) {
        return null;
    }

    public TournamentTeam getTournamentTeamByName(String name) {
        return null;
    }

    public TournamentTeam addTournamentTeam(TournamentTeam tournamentTeam) {
        return null;
    }

    public TournamentTeam updateTournamentTeam(Long id, TournamentTeam tournamentTeam) {
        return null;
    }

    public void deleteTournamentTeam(Long id) {

    }
}
