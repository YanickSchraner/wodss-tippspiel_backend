package ch.fhnw.wodss.tippspiel.Services;

import ch.fhnw.wodss.tippspiel.Domain.Bet;
import ch.fhnw.wodss.tippspiel.Persistance.BetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class BetService {

    private final BetRepository repository;

    @Autowired
    public BetService(BetRepository repository) {
        this.repository = repository;
    }

    public Bet getBetById(Long id) {
        return null;
    }

    public Bet addBet(Bet bet) {
        return null;
    }

    public Bet updateBet(Long id, Bet bet) {
        return null;
    }

    public void deleteBet(Long id) {

    }
}
