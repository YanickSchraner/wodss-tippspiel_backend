package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.domain.Bet;
import ch.fhnw.wodss.tippspiel.domain.Game;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceAlreadyExistsException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotAllowedException;
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
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
public class BetService {

    private final BetRepository betRepository;
    private final GameRepository gameRepository;

    @Autowired
    public BetService(BetRepository betRepository, GameRepository gameRepository) {
        this.betRepository = betRepository;
        this.gameRepository = gameRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Bet getBetById(Long id, User user) {
        Optional<Bet> bet = betRepository.findById(id);
        long betOwner = bet.orElseThrow(() -> new ResourceNotFoundException("Could not find Bet with id: " + id))
                .getUser().getId();
        if (betOwner != user.getId())
            throw new ResourceNotAllowedException("This bet with id: " + id + ", doesn't belong to the user with id: " + user.getId());
        return bet.get();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Bet addBet(Bet bet, User user) {
        Game game = gameRepository.findById(bet.getGame().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Could not find Game in Bet with id: "
                        + bet.getGame().getId()));
        if (!Objects.equals(bet.getUser().getId(), user.getId()))
            throw new IllegalActionException("The user id in the given bet object doesn't match with the logged in user id");
        boolean alreadyBettedByUser = betRepository.existsBetByUser_IdAndGame_Id(user.getId(), game.getId());
        if (alreadyBettedByUser)
            throw new ResourceAlreadyExistsException("A bet for this game and user already exists!");

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        // Check if date time before game start time
        if (game.getDateTime().isAfter(now)) {
            return betRepository.save(bet);
        } else {
            throw new IllegalActionException("The game has started. The bet can't be accepted.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Bet updateBet(Long id, Bet bet, User user) {
        if (!betRepository.existsById(id)) {
            throw new ResourceNotFoundException("No bet was found to change");
        }
        Game game = gameRepository.findById(bet.getGame().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Could not find Game in Bet with id: "
                        + bet.getGame().getId()));

        boolean alreadyBettedByUser = betRepository.existsBetByUser_IdAndGame_Id(user.getId(), game.getId());
        if (!alreadyBettedByUser)
            throw new IllegalActionException("This is not a bet of the given user!");

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Paris"));
        // Check if date time before game start time
        if (game.getDateTime().isAfter(now)) {
            bet.setId(id);
            return betRepository.save(bet);
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

}