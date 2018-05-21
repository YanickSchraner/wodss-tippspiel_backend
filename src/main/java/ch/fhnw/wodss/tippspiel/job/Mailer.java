package ch.fhnw.wodss.tippspiel.job;

import ch.fhnw.wodss.tippspiel.domain.User;
import ch.fhnw.wodss.tippspiel.persistance.UserRepository;
import ch.fhnw.wodss.tippspiel.util.GMail;
import com.google.api.services.gmail.Gmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class Mailer {
    private static final Logger logger = LoggerFactory.getLogger(Mailer.class);

    @Autowired
    UserRepository repository;
    @Value("${mailer.from}")
    private String from;

    @Scheduled(cron = "0 8 * * *")
    public void remindUsersToBet() throws GeneralSecurityException {
        List<User> usersToRemind = repository.findAllByRemindersTrue();
        Gmail service = GMail.getAuthorizedService().orElseThrow(() -> new GeneralSecurityException("Can't authenticate to gmail service."));
        String message = "Vergessen Sie nicht Ihren Tipp für die heutigen Spiele abzugeben!";
        String subject = "Tippspiel WM 2018 - Ausstehende Tippabgaben!";
        for (User user: usersToRemind) {
            try {
                MimeMessage mimeMessage = GMail.createEmail(user.getEmail(), from, subject, message);
                GMail.sendMessage(service, "me", mimeMessage);
            } catch (MessagingException | IOException e) {
                logger.error(Arrays.toString(e.getStackTrace()));
            }
        }
    }

    @Scheduled(cron = "0 23 * * *")
    public void sendDailyReport(){
        List<User> userToReport = repository.findAllByDailyResultsTrue();

    }

    private boolean checkUserHasOpenBets(User user){
        return true;
    }
}
