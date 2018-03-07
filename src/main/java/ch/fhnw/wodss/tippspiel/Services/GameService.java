package ch.fhnw.wodss.tippspiel.Services;

import ch.fhnw.wodss.tippspiel.Domain.Game;
import ch.fhnw.wodss.tippspiel.Exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.Persistance.GameRepository;
import ch.fhnw.wodss.tippspiel.Persistance.TournamentGroupRepository;
import ch.fhnw.wodss.tippspiel.Persistance.TournamentTeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

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
        return gameRepository.findAll();
    }

    public Game getGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find a game with id " + id));
    }

    public Game addGame(Game game) {
        return gameRepository.save(game);
    }

    public Game updateGame(Long id, Game game) {
        Optional<Game> oldGame = gameRepository.findById(id);
        if (oldGame.isPresent()) {
            game.setId(id);
            return gameRepository.save(game);
        }
        throw new ResourceNotFoundException("Could not find game with id " + id + " to update.");
    }

    public void deleteGame(Long id) {
        if (gameRepository.existsById(id)) {
            gameRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Could not find game with id " + id + " to delete.");
        }
    }

    public void setResult(Long id, int homeTeamScore, int awayTeamScore) {
        Optional<Game> game = gameRepository.findById(id);
        if (game.isPresent()) {
            game.get().setHomeTeamGoals(homeTeamScore);
            game.get().setAwayTeamGoals(awayTeamScore);
            gameRepository.save(game.get());
        } else {
            throw new ResourceNotFoundException("Could not find game with id " + id + " to update the score.");
        }
    }

}
