package pl.krasnoludkolo.ebet2.bet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;

@Configuration
public class BetConfiguration {

    @Bean
    public BetFacade betFacadeBean() {
        Repository<Bet> repository = new BetJOOQRepository();
        BetManager betManager = new BetManager(repository);
        return new BetFacade(betManager);
    }


    public BetFacade inMemoryBetFacade() {
        Repository<Bet> repository = new InMemoryRepository<>();
        BetManager betManager = new BetManager(repository);
        return new BetFacade(betManager);
    }

}
