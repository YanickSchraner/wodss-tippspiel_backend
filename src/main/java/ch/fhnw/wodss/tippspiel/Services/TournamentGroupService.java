package ch.fhnw.wodss.tippspiel.Services;

import ch.fhnw.wodss.tippspiel.Domain.TournamentGroup;
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
        return null;
    }

    public TournamentGroup getTournamentGroupById(Long id) {
        return null;
    }

    public TournamentGroup getTournamentGroupByName(String name) {
        return null;
    }

    public TournamentGroup addTournamentGroup(TournamentGroup tournamentGroup) {
        return null;
    }

    public TournamentGroup updateTournamentGroup(Long id, TournamentGroup tournamentGroup) {
        return null;
    }

    public void deleteTournamentGroup(Long id) {

    }
}
