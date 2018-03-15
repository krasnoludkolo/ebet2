package pl.krasnoludkolo.ebet2.league.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.results.domain.ResultFacade;

@Configuration
public class LeagueConfiguration {

    @Bean
    public LeagueFacade leagueFacadeBean(ResultFacade resultFacade) {
        return inMemoryLeagueFacade(resultFacade);
    }


    public LeagueFacade inMemoryLeagueFacade(ResultFacade resultFacade) {
        LeagueCRUDService leagueCRUDService = createLeagueCRUDService();
        MatchCRUDService matchCRUDService = createMatchCRUDService();
        return new LeagueFacade(leagueCRUDService, matchCRUDService, resultFacade);
    }

    private MatchCRUDService createMatchCRUDService() {
        Repository<Match> matchRepository = new InMemoryRepository<>();
        return new MatchCRUDService(matchRepository);
    }

    private LeagueCRUDService createLeagueCRUDService() {
        Repository<League> leagueRepository = new InMemoryRepository<>();
        return new LeagueCRUDService(leagueRepository);
    }

}
