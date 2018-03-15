package pl.krasnoludkolo.ebet2.results.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.bet.domain.BetFacade;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;

@Configuration
public class ResultConfiguration {

    @Bean
    public ResultFacade resultFacadeBean(BetFacade betFacade) {
        return inMemoryResultController(betFacade);
    }

    public ResultFacade inMemoryResultController(BetFacade betFacade) {
        Repository<LeagueResults> repository = new InMemoryRepository<>();
        LeagueResultsCRUDService service = new LeagueResultsCRUDService(repository);
        LeagueResultsUpdater leagueResultsUpdater = new LeagueResultsUpdater(repository);
        return new ResultFacade(service, leagueResultsUpdater, betFacade);
    }

}