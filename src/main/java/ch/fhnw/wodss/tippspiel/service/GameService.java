package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.domain.Game;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.BetRepository;
import ch.fhnw.wodss.tippspiel.persistance.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
public class GameService {

    private final GameRepository gameRepository;
    private final BetRepository betRepository;

    @Autowired
    public GameService(GameRepository gameRepository, BetRepository betRepository) {
        this.gameRepository = gameRepository;
        this.betRepository = betRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Game getGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find a game with id " + id));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Game addGame(Game game) {
        if (gameRepository.existsGameByHomeTeamAndAwayTeamAndDateTimeEquals(game.getHomeTeam(), game.getAwayTeam(), game.getDateTime())) {
            throw new IllegalActionException("Can't create an identical game");
        }
        return gameRepository.save(game);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Game updateGame(Long id, Game game) {
        Optional<Game> oldGame = gameRepository.findById(id);
        if (oldGame.isPresent()) {
            game.setId(id);
            return gameRepository.save(game);
        }
        throw new ResourceNotFoundException("Could not find game with id " + id + " to update.");
    }

    @Transactional(propagation = Propagation.REQUIRED)
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

    @Transactional(propagation = Propagation.REQUIRED)
    public void setResult(Long id, int homeTeamScore, int awayTeamScore) {
        Optional<Game> game = gameRepository.findById(id);
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        if (game.isPresent()) {
            if (game.get().getDateTime().isAfter(now)) {
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
