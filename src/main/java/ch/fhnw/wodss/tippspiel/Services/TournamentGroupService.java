package ch.fhnw.wodss.tippspiel.Services;

import ch.fhnw.wodss.tippspiel.Domain.TournamentGroup;
import ch.fhnw.wodss.tippspiel.Exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.Exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.Persistance.TournamentGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TournamentGroupService {

    private final TournamentGroupRepository repository;

    @Autowired
    public TournamentGroupService(TournamentGroupRepository repository) {
        this.repository = repository;
    }

    public List<TournamentGroup> getAllTournamentGroups() {
        return repository.findAll();
    }

    public TournamentGroup getTournamentGroupById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find TournamentGroup with id: " + id));
    }

    public TournamentGroup getTournamentGroupByName(String name) {
        return repository.getTournamentGroupByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find TournamentGroup with name: " + name));
    }

    public TournamentGroup addTournamentGroup(TournamentGroup tournamentGroup) {
        return repository.save(tournamentGroup);
    }

    public TournamentGroup updateTournamentGroup(Long id, TournamentGroup tournamentGroup) {
        if (!repository.existsById(id)) {
            throw new IllegalActionException("No TournamentGroup was found to change");
        }
        tournamentGroup.setId(id);
        return repository.save(tournamentGroup);
    }

    public void deleteTournamentGroup(Long id) {
        TournamentGroup tournamentGroup = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find TournamentGroup with id: " + id));
        repository.delete(tournamentGroup);
    }
}
