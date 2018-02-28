package ch.fhnw.wodss.tippspiel.Services;

import ch.fhnw.wodss.tippspiel.Persistance.GameRepository;
import ch.fhnw.wodss.tippspiel.Persistance.TournamentGroupRepository;
import ch.fhnw.wodss.tippspiel.Persistance.TournamentTeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class GameService {
    private final GameRepository gameRepository;
    private final TournamentTeamRepository tournamentTeamRepository;
    private final TournamentGroupRepository tournamentGroupRepository;


    @Autowired
    public GameService(GameRepository gameRepository, TournamentTeamRepository tournamentTeamRepository, TournamentGroupRepository tournamentGroupRepository) {
        this.gameRepository = gameRepository;
        this.tournamentTeamRepository = tournamentTeamRepository;
        this.tournamentGroupRepository = tournamentGroupRepository;
    }




}
