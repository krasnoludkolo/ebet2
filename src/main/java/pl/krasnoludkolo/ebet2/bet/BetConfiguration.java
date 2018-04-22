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
    public BetFacade betFacadeBean(CrudRepository<BetEntity, UUID> repo) {
        Function<Bet, BetEntity> d2e = Bet::toEntity;
        Function<BetEntity, Bet> e2d = Bet::new;
        Repository<Bet> repository = new SpringDataRepositoryAdapter<>(repo, d2e, e2d);
        BetManager betManager = new BetManager(repository);
        return new BetFacade(betManager);
    }

    public BetFacade inMemoryBetFacade() {
        Repository<Bet> repository = new InMemoryRepository<>();
        BetManager betManager = new BetManager(repository);
        return new BetFacade(betManager);
    }

}
