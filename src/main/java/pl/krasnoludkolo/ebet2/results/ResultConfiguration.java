package pl.krasnoludkolo.ebet2.results;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.infrastructure.SpringDataRepositoryAdapter;

import java.util.function.Function;

@Configuration
public class ResultConfiguration {

    @Bean
    public ResultFacade resultFacadeBean(BetFacade betFacade, LeagueResultsRepository repository) {
        SpringDataRepositoryAdapter<LeagueResults, LeagueResultsEntity> adapter = createAdapter(repository);
        LeagueResultsManager service = new LeagueResultsManager(adapter);
        LeagueResultsUpdater leagueResultsUpdater = new LeagueResultsUpdater(adapter);
        return new ResultFacade(service, leagueResultsUpdater, betFacade);
    }

    private SpringDataRepositoryAdapter<LeagueResults, LeagueResultsEntity> createAdapter(LeagueResultsRepository repository) {
        Function<LeagueResults, LeagueResultsEntity> d2e = LeagueResults::toEntity;
        Function<LeagueResultsEntity, LeagueResults> e2d = LeagueResults::fromEntity;
        return new SpringDataRepositoryAdapter<>(repository, d2e, e2d);
    }

    public ResultFacade inMemoryResult(BetFacade betFacade) {
        Repository<LeagueResults> repository = new InMemoryRepository<>();
        LeagueResultsManager service = new LeagueResultsManager(repository);
        LeagueResultsUpdater leagueResultsUpdater = new LeagueResultsUpdater(repository);
        return new ResultFacade(service, leagueResultsUpdater, betFacade);
    }

}