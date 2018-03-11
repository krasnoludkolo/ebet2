package pl.krasnoludkolo.ebet2.domain.bet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.domain.Repository;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;

@Configuration
public class BetConfiguration {

    @Bean
    public BetFacade inMemoryBetFacade() {
        Repository<Bet> repository = new InMemoryRepository<>();
        BetCRUDService betCRUDService = new BetCRUDService(repository);
        return new BetFacade(betCRUDService);
    }

}
