package pl.krasnoludkolo.ebet2.domain.league;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.domain.Repository;
import pl.krasnoludkolo.ebet2.domain.results.ResultFacade;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;

@Configuration
public class LeagueConfiguration {

    @Bean
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
