package ch.fhnw.wodss.tippspiel.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* ch.fhnw.wodss.tippspiel.controller.*.*(..))")
    public void logController(JoinPoint jp) {
        logger.info("Controller method called: " + jp.getSignature());
    }

    @AfterThrowing(pointcut = "execution(* ch.fhnw.wodss.tippspiel.configuration.RESTAuthenticationFilter.attemptAuthentication(..))",
            throwing = "error")
    public void logInvalidLoginDataReceived(JoinPoint jp, Throwable error) {
        logger.warn("Invalid login data format received");
    }

    @AfterThrowing(pointcut = "execution(* ch.fhnw.wodss.tippspiel.configuration.UserDetailsServiceImpl.loadUserByUsername(..))",
            throwing = "error")
    public void logUnknownUsernameLogin(JoinPoint jp, Throwable error) {
        logger.warn("Failed login with unknown username: " + jp.getArgs()[0]);
    }

    @AfterReturning(pointcut = "execution(* ch.fhnw.wodss.tippspiel.security.Argon2PasswordEncoder.matches(..))",
            returning = "validLogin")
    public void logInvalidLoginAttempt(boolean validLogin) {
        if (!validLogin) {
            logger.warn("Invalid login attempt registered!");
        }
    }

    // TODO log Mailer, API Connector and Scorer actions!


}
