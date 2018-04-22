package pl.krasnoludkolo.ebet2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Ebet2Application {

    public static void main(String[] args) {
        SpringApplication.run(Ebet2Application.class, args);
    }

}
