package ch.fhnw.wodss.tippspiel.job;

import ch.fhnw.wodss.tippspiel.domain.Bet;
import ch.fhnw.wodss.tippspiel.domain.BetGroup;
import ch.fhnw.wodss.tippspiel.persistance.BetGroupRepository;
import ch.fhnw.wodss.tippspiel.persistance.BetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class Scorer {

    private static final Logger logger = LoggerFactory.getLogger(Scorer.class);
    private BetRepository betRepository;
    private BetGroupRepository betGroupRepository;

    @Autowired
    public Scorer(BetRepository betRepository, BetGroupRepository betGroupRepository) {
        this.betRepository = betRepository;
        this.betGroupRepository = betGroupRepository;
    }

    @Scheduled(cron = "30 */30 17-23 * * *")
    public void setBetScore() {
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0);
        LocalDateTime end = LocalDateTime.now().withHour(23).withMinute(59);
        List<Bet> todayBets = betRepository.getTodayBets(start, end);
        for (Bet bet : todayBets) {
            int score = calculateScore(bet);
            bet.setScore(score);
            betRepository.save(bet);
        }
        logger.info("Bet scores updated.");
    }

    private int calculateScore(Bet bet) {
        int actualHomeTeamGoals = bet.getGame().getHomeTeamGoals();
        int actualAwayTeamGoals = bet.getGame().getAwayTeamGoals();
        int bettedHomeTeamGoals = bet.getHomeTeamGoals();
        int bettedAwayTeamGoals = bet.getAwayTeamGoals();
        int actualDifference = actualHomeTeamGoals - actualAwayTeamGoals;
        int bettedDifference = bettedHomeTeamGoals - bettedAwayTeamGoals;
        int score = 0;
        if (actualAwayTeamGoals == bettedAwayTeamGoals && actualHomeTeamGoals == bettedHomeTeamGoals) {
            score += 5;
        }
        if (actualAwayTeamGoals == bettedAwayTeamGoals) {
            score++;
        }
        if (actualHomeTeamGoals == bettedHomeTeamGoals) {
            score++;
        }
        if (actualDifference == bettedDifference) {
            score += 3;
        }
        return score;
    }

    @Scheduled(cron = "50 */30 17-23 * * *")
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateBetGroupScore() {
        for (BetGroup betGroup : betGroupRepository.findAll()) {
            double score = betGroup.getMembers().stream().mapToInt(user -> user.getBets().stream().mapToInt(Bet::getScore).sum()).average().orElse(0);
            betGroup.setScore((int) score);
            betGroupRepository.save(betGroup);
        }
        logger.info("Group scores updated.");
    }
}
