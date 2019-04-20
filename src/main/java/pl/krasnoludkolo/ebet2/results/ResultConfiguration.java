package pl.krasnoludkolo.ebet2.results;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.infrastructure.SpringDataRepositoryAdapter;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;

import java.util.function.Function;

@Configuration
public class ResultConfiguration {

    @Bean
    public ResultFacade resultFacadeBean(BetFacade betFacade, LeagueResultsRepository springRepository, LeagueFacade leagueFacade) {
        Repository<LeagueResults> repository = createRepository(springRepository);
        LeagueResultsManager service = new LeagueResultsManager(repository);
        LeagueResultsUpdater leagueResultsUpdater = new LeagueResultsUpdater(repository);
        return new ResultFacade(service, leagueResultsUpdater, betFacade, leagueFacade);
    }

    private SpringDataRepositoryAdapter<LeagueResults, LeagueResultEntity> createRepository(LeagueResultsRepository springRepository) {
        Function<LeagueResults, LeagueResultEntity> d2e = LeagueResults::toEntity;
        Function<LeagueResultEntity, LeagueResults> e2d = LeagueResults::fromEntity;
        return new SpringDataRepositoryAdapter<>(springRepository, d2e, e2d);
    }

    public ResultFacade inMemoryResult(BetFacade betFacade, LeagueFacade leagueFacade) {
        Repository<LeagueResults> repository = new InMemoryRepository<>();
        LeagueResultsManager service = new LeagueResultsManager(repository);
        LeagueResultsUpdater leagueResultsUpdater = new LeagueResultsUpdater(repository);
        return new ResultFacade(service, leagueResultsUpdater, betFacade, leagueFacade);
    }

}