package ch.fhnw.wodss.tippspiel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableAspectJAutoProxy
@EnableScheduling
public class TippspielApplication {

    public static void main(String[] args) {
        SpringApplication.run(TippspielApplication.class, args);

    }

}
