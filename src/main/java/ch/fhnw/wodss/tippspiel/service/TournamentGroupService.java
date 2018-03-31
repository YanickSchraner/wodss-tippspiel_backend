package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.domain.TournamentGroup;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.TournamentGroupRepository;
import ch.fhnw.wodss.tippspiel.persistance.TournamentTeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
public class TournamentGroupService {

    private final TournamentGroupRepository tournamentGroupRepository;
    private final TournamentTeamRepository tournamentTeamRepository;

    @Autowired
    public TournamentGroupService(TournamentGroupRepository tournamentGroupRepository, TournamentTeamRepository tournamentTeamRepository) {
        this.tournamentGroupRepository = tournamentGroupRepository;
        this.tournamentTeamRepository = tournamentTeamRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<TournamentGroup> getAllTournamentGroups() {
        return tournamentGroupRepository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public TournamentGroup getTournamentGroupById(Long id) {
        return tournamentGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can't find a tournament group with id: " + id));
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public TournamentGroup getTournamentGroupByName(String name) {
        return tournamentGroupRepository.findByNameEquals(name)
                .orElseThrow(() -> new ResourceNotFoundException("Can't find a tournament group with name: " + name));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TournamentGroup addTournamentGroup(TournamentGroup tournamentGroup) {
        if (tournamentGroupRepository.findByNameEquals(tournamentGroup.getName()).isPresent()) {
            throw new IllegalActionException("A tournament group with name: " + tournamentGroup.getName() + " already exists.");
        }
        return tournamentGroupRepository.save(tournamentGroup);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TournamentGroup updateTournamentGroup(Long id, TournamentGroup tournamentGroup) {
        if (tournamentGroupRepository.existsById(id)) {
            tournamentGroup.setId(id);
            return tournamentGroupRepository.save(tournamentGroup);
        }
        throw new ResourceNotFoundException("Can't find a tournament group with id: " + id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteTournamentGroup(Long id) {
        if (tournamentTeamRepository.existsTournamentTeamsByGroup_Id(id)) {
            throw new IllegalActionException("Can't delete a tournament group with tournament team members");
        }
        if (tournamentGroupRepository.existsById(id)) {
            tournamentGroupRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Can't find a tournament group with id: " + id);
        }

    }
}
