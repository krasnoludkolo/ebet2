package pl.krasnoludkolo.ebet2.results;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;

@Configuration
public class ResultConfiguration {

    @Bean
    public ResultFacade resultFacadeBean(BetFacade betFacade, LeagueResultsRepository springRepository, LeagueFacade leagueFacade) {
        Repository<LeagueResults> repository = new LeagueResultSpringDataAdapter(springRepository);
        LeagueResultsManager service = new LeagueResultsManager(repository);
        LeagueResultsUpdater leagueResultsUpdater = new LeagueResultsUpdater(repository);
        return new ResultFacade(service, leagueResultsUpdater, betFacade, leagueFacade);
    }

    public ResultFacade inMemoryResult(BetFacade betFacade, LeagueFacade leagueFacade) {
        Repository<LeagueResults> repository = new InMemoryRepository<>();
        LeagueResultsManager service = new LeagueResultsManager(repository);
        LeagueResultsUpdater leagueResultsUpdater = new LeagueResultsUpdater(repository);
        return new ResultFacade(service, leagueResultsUpdater, betFacade, leagueFacade);
    }

}