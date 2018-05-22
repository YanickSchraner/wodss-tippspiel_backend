package ch.fhnw.wodss.tippspiel.job;

import ch.fhnw.wodss.tippspiel.domain.Game;
import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.persistance.BetRepository;
import ch.fhnw.wodss.tippspiel.persistance.GameRepository;
import ch.fhnw.wodss.tippspiel.persistance.UserRepository;
import ch.fhnw.wodss.tippspiel.util.GMail;
import com.google.api.services.gmail.Gmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class Mailer {
    private static final Logger logger = LoggerFactory.getLogger(Mailer.class);

    private UserRepository userRepository;
    private GameRepository gameRepository;
    private BetRepository betRepository;

    @Value("${mailer.from}")
    private String from;

    @Autowired
    public Mailer(UserRepository userRepository, GameRepository gameRepository, BetRepository betRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.betRepository = betRepository;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void remindUsersToBet() throws GeneralSecurityException {
        List<User> usersToRemind = userRepository.findAllByRemindersTrue();
        Gmail service = GMail.getAuthorizedService().orElseThrow(() -> new GeneralSecurityException("Can't authenticate to gmail service."));
        String message = "Vergiss nicht deinen Tipp für die heutigen Spiele abzugeben!";
        String subject = "Tippspiel WM 2018 - Ausstehende Tippabgaben!";
        for (User user : usersToRemind) {
            try {
                if (checkUserHasOpenBets(user)) {
                    MimeMessage mimeMessage = GMail.createEmail(user.getEmail(), from, subject, message);
                    GMail.sendMessage(service, "me", mimeMessage);
                }
            } catch (MessagingException | IOException e) {
                logger.error(Arrays.toString(e.getStackTrace()));
            }
        }
        logger.info("Reminders sent");
    }

    @Scheduled(cron = "0 0 23 * * *")
    public void sendDailyReport() throws GeneralSecurityException {
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0);
        LocalDateTime end = LocalDateTime.now().withHour(23).withMinute(59);
        Gmail service = GMail.getAuthorizedService().orElseThrow(() -> new GeneralSecurityException("Can't authenticate to gmail service."));
        StringBuilder message = new StringBuilder();
        message.append("Du hast heute ");
        String subject = "Tippspiel WM 2018 - Tagesrapport";
        List<User> usersToReport = userRepository.findAllByDailyResultsTrue();
        for (User userToReport : usersToReport) {
            int score = betRepository.getTodayScore(userToReport, start, end);
            message.append(score);
            message.append(" Punkte erzielt!\n");
            message.append("Aktuell hast du ");
            message.append(betRepository.getUserScore(userToReport));
            message.append(" Punke.\n Herzliche Gratulation!");
            try {
                MimeMessage mimeMessage = GMail.createEmail(userToReport.getEmail(), from, subject, message.toString());
                GMail.sendMessage(service, "me", mimeMessage);
            } catch (MessagingException | IOException e) {
                logger.error(Arrays.toString(e.getStackTrace()));
            }
        }
        logger.info("Daily report sent.");
    }

    private boolean checkUserHasOpenBets(User user) {
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0);
        LocalDateTime end = LocalDateTime.now().withHour(23).withMinute(59);
        List<Game> todayGames = gameRepository.getAllByDateTimeIsBetween(start, end);
        int bettedGames = 0;
        for (Game game : todayGames) {
            bettedGames += user.getBets().stream().filter(bet -> bet.getGame().equals(game)).count();
        }
        return bettedGames < todayGames.size();
    }
}
