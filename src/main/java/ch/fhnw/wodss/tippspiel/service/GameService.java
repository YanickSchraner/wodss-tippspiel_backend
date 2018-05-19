package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.domain.Game;
import ch.fhnw.wodss.tippspiel.dto.GameDTO;
import ch.fhnw.wodss.tippspiel.dto.RestGameDTO;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.BetRepository;
import ch.fhnw.wodss.tippspiel.persistance.GameRepository;
import ch.fhnw.wodss.tippspiel.persistance.TournamentTeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
public class GameService {

    private final GameRepository gameRepository;
    private final BetRepository betRepository;
    private final TournamentTeamRepository tournamentTeamRepository;

    @Autowired
    public GameService(GameRepository gameRepository, BetRepository betRepository, TournamentTeamRepository tournamentTeamRepository) {
        this.gameRepository = gameRepository;
        this.betRepository = betRepository;
        this.tournamentTeamRepository = tournamentTeamRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<GameDTO> getAllGames() {
        List<Game> games = gameRepository.findAll();
        List<GameDTO> gameDTOS = new ArrayList<>();
        for (Game game : games) {
            gameDTOS.add(convertGameToGameDTO(game));
        }
        return gameDTOS;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public GameDTO getGameById(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find a game with id " + id));
        return convertGameToGameDTO(game);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public GameDTO addGame(RestGameDTO restGameDTO) {
        Game game = new Game();
        game.getHomeTeam().setId(restGameDTO.getHomeTeamId());
        game.getAwayTeam().setId(restGameDTO.getAwayTeamId());
        game.getLocation().setId(restGameDTO.getLocationId());
        game.getPhase().setId(restGameDTO.getPhaseId());
        //TODO
        //game.setHomeTeam(tournamentTeamRepository.findById(restGameDTO.getHomeTeamId()));
        //game.setAwayTeam(tournamentTeamRepository.findById(restGameDTO.getAwayTeamId()));
        //game.setLocation(tournamentTeamRepository.findById(restGameDTO.getLocationId()));
        //game.setPhase(tournamentTeamRepository.findById(restGameDTO.getPhaseId()));
        if (gameRepository.existsGameByHomeTeamAndAwayTeamAndDateTimeEquals(game.getHomeTeam(), game.getAwayTeam(), game.getDateTime())) {
            throw new IllegalActionException("Can't create an identical game");
        }
        game = gameRepository.save(game);
        return convertGameToGameDTO(game);
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

    private GameDTO convertGameToGameDTO(Game game) {
        GameDTO gameDTO = new GameDTO();
        gameDTO.setHomeTeamId(game.getHomeTeam().getId());
        gameDTO.setAwayTeamId(game.getAwayTeam().getId());
        gameDTO.setHomeTeamName(game.getHomeTeam().getName());
        gameDTO.setAwayTeamName(game.getAwayTeam().getName());
        gameDTO.setLocationName(game.getLocation().getName());
        gameDTO.setPhaseName(game.getPhase().getName());
        gameDTO.setHomeTeamGoals(game.getHomeTeamGoals());
        gameDTO.setAwayTeamGoals(game.getAwayTeamGoals());
        return new GameDTO();
    }

}
