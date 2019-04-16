package pl.krasnoludkolo.ebet2.bet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.user.UserFacade;

import java.util.function.Function;

@Configuration
public class BetConfiguration {

    @Bean
    public BetFacade betFacadeBean(UserFacade userFacade, LeagueFacade leagueFacade) {
        Function<Bet, BetEntity> d2e = Bet::toEntity;
        Function<BetEntity, Bet> e2d = Bet::fromEntity;
        Repository<Bet> repository = new BetJOOQRepository(d2e, e2d);
        BetManager betManager = new BetManager(repository, leagueFacade);
        return new BetFacade(betManager, userFacade);
    }

    public BetFacade inMemoryBetFacade(UserFacade userFacade, LeagueFacade leagueFacade) {
        Repository<Bet> repository = new InMemoryRepository<>();
        BetManager betManager = new BetManager(repository, leagueFacade);
        return new BetFacade(betManager, userFacade);
    }

}
