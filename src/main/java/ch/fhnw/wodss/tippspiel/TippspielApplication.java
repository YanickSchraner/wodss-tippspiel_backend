package ch.fhnw.wodss.tippspiel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
//@EnableScheduling
public class TippspielApplication {

	public static void main(String[] args) {
		SpringApplication.run(TippspielApplication.class, args);
	}
}
