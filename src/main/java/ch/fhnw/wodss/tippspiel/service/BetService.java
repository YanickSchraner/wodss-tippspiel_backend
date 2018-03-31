package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.domain.Bet;
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

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

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
    public Bet getBetById(Long id) {
        return betRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find Bet with id: " + id));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Bet addBet(Bet bet) {
        Game game = gameRepository.findById(bet.getGame().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Could not find Game in Bet with id: "
                        + bet.getGame().getId()));
        AtomicReference<Calendar> cal = new AtomicReference<>(Calendar.getInstance());
        // Check if date time before game start time
        if (game.getDateTime().before(cal.get().getTime())) {
            return betRepository.save(bet);
        } else {
            throw new IllegalActionException("The game has started. The bet can't be accepted.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Bet updateBet(Long id, Bet bet) {
        if (!betRepository.existsById(id)) {
            throw new IllegalActionException("No bet was found to change");
        }
        Game game = gameRepository.findById(bet.getGame().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Could not find Game in Bet with id: "
                        + bet.getGame().getId()));
        Calendar cal = Calendar.getInstance();
        // Check if date time before game start time
        if (game.getDateTime().before(cal.getTime())) {
            bet.setId(id);
            return betRepository.save(bet);
        } else {
            throw new IllegalActionException("The game has started. The bet can't be updated.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteBet(Long id) {
        Bet bet = betRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find Bet with id: " + id));
        Game game = gameRepository.findById(bet.getGame().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Could not find Game in Bet with id: "
                        + bet.getGame().getId()));
        Calendar cal = Calendar.getInstance();
        // Check if date time before game start time
        if (game.getDateTime().before(cal.getTime())) {
            betRepository.deleteById(id);
        } else {
            throw new IllegalActionException("The game has started. The bet can't be deleted.");
        }
    }

}