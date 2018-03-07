package ch.fhnw.wodss.tippspiel.Services;

import ch.fhnw.wodss.tippspiel.Domain.Game;
import ch.fhnw.wodss.tippspiel.Exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.Exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.Persistance.BetRepository;
import ch.fhnw.wodss.tippspiel.Persistance.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GameService {
    private final GameRepository gameRepository;
    private final BetRepository betRepository;


    @Autowired
    public GameService(GameRepository gameRepository, BetRepository betRepository) {
        this.gameRepository = gameRepository;
        this.betRepository = betRepository;
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Game getGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find a game with id " + id));
    }

    public Game addGame(Game game) {
        if (gameRepository.existsGameByHomeTeamAndAwayTeamAndDateTimeEquals(game.getHomeTeam(), game.getAwayTeam(), game.getDateTime())) {
            throw new IllegalActionException("Can't create an identical game");
        }
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
        if (betRepository.existsBetsByGame_Id(id)) {
            throw new IllegalActionException("Can't delete a game with active bets");
        }
        if (gameRepository.existsById(id)) {
            gameRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Could not find game with id " + id + " to delete.");
        }
    }

    public void setResult(Long id, int homeTeamScore, int awayTeamScore) {
        Optional<Game> game = gameRepository.findById(id);
        Calendar cal = Calendar.getInstance();
        if (game.isPresent()) {
            if (game.get().getDateTime().before(cal.getTime())) {
                throw new IllegalActionException("Can't set Score for a game which hasn't been played jet.");
            }
            game.get().setHomeTeamGoals(homeTeamScore);
            game.get().setAwayTeamGoals(awayTeamScore);
            gameRepository.save(game.get());
        } else {
            throw new ResourceNotFoundException("Could not find game with id " + id + " to update the score.");
        }
    }

}
