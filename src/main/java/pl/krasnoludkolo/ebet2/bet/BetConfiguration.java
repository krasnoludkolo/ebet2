package pl.krasnoludkolo.ebet2.bet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.infrastructure.SpringDataRepositoryAdapter;

import java.util.UUID;
import java.util.function.Function;

@Configuration
public class BetConfiguration {

    @Bean
    public BetFacade betFacadeBean(CrudRepository<Bet, UUID> betRepository) {
        Function<Bet, Bet> d2e = b -> b;
        Function<Bet, Bet> e2d = b -> b;
        SpringDataRepositoryAdapter<Bet, Bet> adapter = new SpringDataRepositoryAdapter<>(betRepository, d2e, e2d);
        BetManager betManager = new BetManager(adapter);
        return new BetFacade(betManager);
    }


    public BetFacade inMemoryBetFacade() {
        Repository<Bet> repository = new InMemoryRepository<>();
        BetManager betManager = new BetManager(repository);
        return new BetFacade(betManager);
    }

}
