package pl.krasnoludkolo.ebet2.bet.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;

@Configuration
public class BetConfiguration {

    @Bean
    public BetFacade betFacadeBean() {
        return inMemoryBetFacade();
    }


    public BetFacade inMemoryBetFacade() {
        Repository<Bet> repository = new InMemoryRepository<>();
        BetCRUDService betCRUDService = new BetCRUDService(repository);
        return new BetFacade(betCRUDService);
    }

}
