package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.domain.Bet;
import ch.fhnw.wodss.tippspiel.domain.Game;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.dto.BetDTO;
import ch.fhnw.wodss.tippspiel.dto.RestBetDTO;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceAlreadyExistsException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotAllowedException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.BetRepository;
import ch.fhnw.wodss.tippspiel.persistance.GameRepository;
import ch.fhnw.wodss.tippspiel.persistance.UserRepository;
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
public class BetService {

    private final BetRepository betRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    @Autowired
    public BetService(BetRepository betRepository, GameRepository gameRepository, UserRepository userRepository) {
        this.betRepository = betRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public BetDTO getBetById(Long id, User user) {
        Optional<Bet> bet = betRepository.findById(id);
        long betOwner = bet.orElseThrow(() -> new ResourceNotFoundException("Could not find Bet with id: " + id))
                .getUser().getId();
        if (betOwner != user.getId())
            throw new ResourceNotAllowedException("This bet with id: " + id + ", doesn't belong to the user with id: " + user.getId());
        return convertBetToBetDTO(bet.get());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BetDTO addBet(RestBetDTO restBetDTO, User user) {
        Game game = gameRepository.findById(restBetDTO.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException("Could not find Game in Bet with id: "
                        + restBetDTO.getGameId()));
        boolean alreadyBettedByUser = betRepository.existsBetByUser_IdAndGame_Id(user.getId(), game.getId());
        if (alreadyBettedByUser)
            throw new ResourceAlreadyExistsException("A bet for this game and user already exists!");

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        // Check if date time before game start time
        if (game.getDateTime().isAfter(now)) {
            Bet bet = new Bet();
            bet.setUser(user);
            bet.setAwayTeamGoals(restBetDTO.getHomeTeamGoals());
            bet.setAwayTeamGoals(restBetDTO.getAwayTeamGoals());
            List<Bet> bets = user.getBets();
            bets.add(bet);
            user.setBets(bets);
            userRepository.save(user);
            bet = betRepository.save(bet);
            return convertBetToBetDTO(bet);
        } else {
            throw new IllegalActionException("The game has started. The bet can't be accepted.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public BetDTO updateBet(Long id, RestBetDTO restBetDTO, User user) {
        if (!betRepository.existsById(id)) {
            throw new ResourceNotFoundException("No bet was found to change");
        }
        Game game = gameRepository.findById(restBetDTO.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException("Could not find Game in Bet with id: "
                        + restBetDTO.getGameId()));

        boolean alreadyBettedByUser = betRepository.existsBetByUser_IdAndGame_Id(user.getId(), game.getId());
        if (!alreadyBettedByUser)
            throw new IllegalActionException("This is not a bet of the given user!");

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        // Check if date time before game start time
        if (game.getDateTime().isAfter(now)) {
            Bet bet = new Bet();
            bet.setId(id);
            bet.setAwayTeamGoals(restBetDTO.getHomeTeamGoals());
            bet.setAwayTeamGoals(restBetDTO.getAwayTeamGoals());
            bet = betRepository.save(bet);
            return convertBetToBetDTO(bet);
        } else {
            throw new IllegalActionException("The game has started. The bet can't be updated.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteBet(Long id, User user) {
        Bet bet = betRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find Bet with id: " + id));
        Game game = gameRepository.findById(bet.getGame().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Could not find Game in Bet with id: "
                        + bet.getGame().getId()));

        boolean alreadyBettedByUser = betRepository.existsBetByUser_IdAndGame_Id(user.getId(), game.getId());
        if (!alreadyBettedByUser)
            throw new IllegalActionException("This is not a bet of the given user!");

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        // Check if date time before game start time
        if (game.getDateTime().isAfter(now)) {
            betRepository.deleteById(id);
        } else {
            throw new IllegalActionException("The game has started. The bet can't be deleted.");
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<BetDTO> getBetsForUser(User user) {
        List<Bet> bets = betRepository.getBetsForUser(user.getId());
        List<BetDTO> betsDTO = new ArrayList<>();
        for (Bet bet : bets) {
            betsDTO.add(convertBetToBetDTO(bet));
        }
        return betsDTO;
    }

    protected BetDTO convertBetToBetDTO(Bet bet) {
        BetDTO betDTO = new BetDTO();
        betDTO.setId(bet.getId());
        betDTO.setUserId(bet.getUser().getId());
        betDTO.setGameId(bet.getGame().getId());
        betDTO.setUsername(bet.getUser().getUsername());
        betDTO.setHomeTeamId(bet.getGame().getHomeTeam().getId());
        betDTO.setAwayTeamId(bet.getGame().getAwayTeam().getId());
        betDTO.setBettedHomeTeamGoals(bet.getHomeTeamGoals());
        betDTO.setBettedAwayTeamGoals(bet.getAwayTeamGoals());
        betDTO.setActualHomeTeamGoals(bet.getGame().getHomeTeamGoals());
        betDTO.setActualAwayTeamGoals(bet.getGame().getAwayTeamGoals());
        betDTO.setScore(bet.getScore());
        betDTO.setLocation(bet.getGame().getLocation().getName());
        betDTO.setPhase(bet.getGame().getPhase().getName());
        return new BetDTO();
    }

}