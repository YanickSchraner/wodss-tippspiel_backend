package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.domain.TournamentTeam;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.TournamentTeamRepository;
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
        return repository.findAll();
    }

    public TournamentTeam getTournamentTeamById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can't find a tournament team with id: " + id));
    }

    public TournamentTeam getTournamentTeamByName(String name) {
        return repository.findTournamentTeamByNameEquals(name)
                .orElseThrow(() -> new ResourceNotFoundException("Can't find a tournament team with name: " + name));
    }

    public TournamentTeam addTournamentTeam(TournamentTeam tournamentTeam) {
        if (repository.findTournamentTeamByNameEquals(tournamentTeam.getName()).isPresent()) {
            throw new IllegalActionException("There is already a tournament team with the name: " + tournamentTeam.getName());
        }
        return null;
    }

    public TournamentTeam updateTournamentTeam(Long id, TournamentTeam tournamentTeam) {
        if (repository.existsById(id)) {
            tournamentTeam.setId(id);
            return repository.save(tournamentTeam);
        }
        throw new ResourceNotFoundException("Can't find a tournament team with id: " + id + " to update.");
    }

    public void deleteTournamentTeam(Long id) {
        if (repository.hasGames(id)) {
            throw new IllegalActionException("Can't delete a tournament team with open games.");
        }
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Can't find a tournament team with id: " + id + " to delete.");
        }
    }
}
