package ch.fhnw.wodss.tippspiel.Services;

import ch.fhnw.wodss.tippspiel.Domain.Bet;
import ch.fhnw.wodss.tippspiel.Domain.Game;
import ch.fhnw.wodss.tippspiel.Persistance.BetRepository;
import ch.fhnw.wodss.tippspiel.Persistance.GameRepository;
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
        return betRepository.getOne(id);
    }

    public Bet addBet(Bet bet) {
        Game game = gameRepository.getOne(bet.getGame().getId());
        if (null == game) {
            //throw Exception
        }
        Calendar cal = Calendar.getInstance();
        // Check if date time before game start time
        if (game.getDateTime().before(cal.getTime())) {
            return betRepository.save(bet);
        } else {
            //throw Exception
        }
        return null;
    }

    public Bet updateBet(Long id, Bet bet) {
        if (!betRepository.existsById(id)) {
            //throw Exception
        }
        Game game = gameRepository.getOne(bet.getGame().getId());
        if (null == game) {
            //throw Exception
        }
        Calendar cal = Calendar.getInstance();
        // Check if date time before game start time
        if (game.getDateTime().before(cal.getTime())) {
            bet.setId(id);
            return betRepository.save(bet);
        } else {
            //throw Exception
        }
        return null;
    }

    public void deleteBet(Long id) {
        Bet bet = betRepository.getOne(id);
        if (null == bet) {
            //throw Exception
        }
        Game game = gameRepository.getOne(bet.getGame().getId());
        if (null == game) {
            //throw Exception
        }
        Calendar cal = Calendar.getInstance();
        // Check if date time before game start time
        if (game.getDateTime().before(cal.getTime())) {
            betRepository.delete(bet);
        } else {
            //throw Exception
        }
    }

}