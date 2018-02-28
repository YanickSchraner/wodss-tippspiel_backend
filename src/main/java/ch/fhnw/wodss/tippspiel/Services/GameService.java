package ch.fhnw.wodss.tippspiel.Services;

import ch.fhnw.wodss.tippspiel.Domain.Game;
import ch.fhnw.wodss.tippspiel.Persistance.GameRepository;
import ch.fhnw.wodss.tippspiel.Persistance.TournamentGroupRepository;
import ch.fhnw.wodss.tippspiel.Persistance.TournamentTeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

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

    public List<Game> getAllGames() {
        return null;
    }

    public Game getGameById(Long id) {
        return null;
    }


    public Game addGame(Game game) {
        return game;
    }

    public Game updateGame(Long id, Game game) {
        return null;
    }

    public void deleteGame(Long id) {

    }

    public void setResult(Long id, int homeTeamScore, int awayTeamScore) {

    }

}
