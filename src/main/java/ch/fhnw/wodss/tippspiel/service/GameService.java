package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.domain.Game;
import ch.fhnw.wodss.tippspiel.dto.GameDTO;
import ch.fhnw.wodss.tippspiel.dto.RestGameDTO;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.*;
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
    private final LocationRepository locationRepository;
    private final PhaseRepository phaseRepository;

    @Autowired
    public GameService(GameRepository gameRepository, BetRepository betRepository, TournamentTeamRepository tournamentTeamRepository, LocationRepository locationRepository, PhaseRepository phaseRepository) {
        this.gameRepository = gameRepository;
        this.betRepository = betRepository;
        this.tournamentTeamRepository = tournamentTeamRepository;
        this.locationRepository = locationRepository;
        this.phaseRepository = phaseRepository;
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
        LocalDateTime localDateTime = LocalDateTime.parse(restGameDTO.getDate()+"T"+restGameDTO.getTime());
        game.setDateTime(localDateTime);
        game.setHomeTeam(tournamentTeamRepository.findById(restGameDTO.getHomeTeamId()).orElseThrow(() -> new ResourceNotFoundException("Home team with id " + restGameDTO.getHomeTeamId() + "not found!")));
        game.setAwayTeam(tournamentTeamRepository.findById(restGameDTO.getAwayTeamId()).orElseThrow(() -> new ResourceNotFoundException("Away team with id " + restGameDTO.getAwayTeamId() + "not found!")));
        game.setLocation(locationRepository.findById(restGameDTO.getLocationId()).orElseThrow(() -> new ResourceNotFoundException("Location with id " + restGameDTO.getLocationId() + "not found!")));
        game.setPhase(phaseRepository.findById(restGameDTO.getPhaseId()).orElseThrow(() -> new ResourceNotFoundException("Phase with id " + restGameDTO.getPhaseId() + "not found!")));
        if (gameRepository.existsGameByHomeTeamAndAwayTeamAndDateTimeEquals(game.getHomeTeam(), game.getAwayTeam(), game.getDateTime())) {
            throw new IllegalActionException("Can't create an identical game");
        }
        game = gameRepository.save(game);
        return convertGameToGameDTO(game);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public GameDTO updateGame(Long id, RestGameDTO restGameDTO) {
        Optional<Game> oldGame = gameRepository.findById(id);
        if (oldGame.isPresent()) {
            Game game = oldGame.get();
            game.setId(id);
            game.setDateTime(LocalDateTime.parse(restGameDTO.getDate() + "T" + restGameDTO.getTime()));
            game.setHomeTeam(tournamentTeamRepository.findById(restGameDTO.getHomeTeamId()).orElseThrow(() -> new ResourceNotFoundException("Home team with id " + restGameDTO.getHomeTeamId() + "not found!")));
            game.setAwayTeam(tournamentTeamRepository.findById(restGameDTO.getAwayTeamId()).orElseThrow(() -> new ResourceNotFoundException("Away team with id " + restGameDTO.getAwayTeamId() + "not found!")));
            game.setLocation(locationRepository.findById(restGameDTO.getLocationId()).orElseThrow(() -> new ResourceNotFoundException("Location with id " + restGameDTO.getLocationId() + "not found!")));
            game.setPhase(phaseRepository.findById(restGameDTO.getPhaseId()).orElseThrow(() -> new ResourceNotFoundException("Phase with id " + restGameDTO.getPhaseId() + "not found!")));
            gameRepository.save(game);
            return convertGameToGameDTO(game);
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
        gameDTO.setTime(game.getDateTime().toLocalTime().toString());
        gameDTO.setDate(game.getDateTime().toLocalDate().toString());
        gameDTO.setHomeTeamId(game.getHomeTeam().getId());
        gameDTO.setAwayTeamId(game.getAwayTeam().getId());
        gameDTO.setHomeTeamName(game.getHomeTeam().getName());
        gameDTO.setAwayTeamName(game.getAwayTeam().getName());
        gameDTO.setLocationName(game.getLocation().getName());
        gameDTO.setPhaseName(game.getPhase().getName());
        gameDTO.setHomeTeamGoals(game.getHomeTeamGoals());
        gameDTO.setAwayTeamGoals(game.getAwayTeamGoals());
        return gameDTO;
    }

}
