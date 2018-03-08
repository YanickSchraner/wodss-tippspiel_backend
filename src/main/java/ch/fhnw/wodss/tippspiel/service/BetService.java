package ch.fhnw.wodss.tippspiel.service;

import ch.fhnw.wodss.tippspiel.domain.Bet;
import ch.fhnw.wodss.tippspiel.domain.Game;
import ch.fhnw.wodss.tippspiel.exception.IllegalActionException;
import ch.fhnw.wodss.tippspiel.exception.ResourceNotFoundException;
import ch.fhnw.wodss.tippspiel.persistance.BetRepository;
import ch.fhnw.wodss.tippspiel.persistance.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;

@Service
@Transactional
public class BetService {

    private final BetRepository betRepository;
    private final GameRepository gameRepository;

    @Autowired
    public BetService(BetRepository betRepository, GameRepository gameRepository) {
        this.betRepository = betRepository;
        this.gameRepository = gameRepository;
    }

    public Bet getBetById(Long id) {
        return betRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Could not find Bet with id: " + id));
    }

    public Bet addBet(Bet bet) {
        Game game = gameRepository.findById(bet.getGame().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Could not find Game in Bet with id: "
                        + bet.getGame().getId()));
        Calendar cal = Calendar.getInstance();
        // Check if date time before game start time
        if (game.getDateTime().before(cal.getTime())) {
            return betRepository.save(bet);
        } else {
            throw new IllegalActionException("The game has started. The bet can't be accepted.");
        }
    }

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