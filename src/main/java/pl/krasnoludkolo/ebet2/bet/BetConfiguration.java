package pl.krasnoludkolo.ebet2.bet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;

import java.util.function.Function;

@Configuration
public class BetConfiguration {

    @Bean
    public BetFacade betFacadeBean() {
        Function<Bet, BetEntity> d2e = Bet::toEntity;
        Function<BetEntity, Bet> e2d = Bet::new;
        Repository<Bet> repository = new BetJOOQRepository(d2e, e2d);
        BetManager betManager = new BetManager(repository);
        return new BetFacade(betManager);
    }


    public BetFacade inMemoryBetFacade() {
        Repository<Bet> repository = new InMemoryRepository<>();
        BetManager betManager = new BetManager(repository);
        return new BetFacade(betManager);
    }

}
