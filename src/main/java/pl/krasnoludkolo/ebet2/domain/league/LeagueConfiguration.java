package pl.krasnoludkolo.ebet2.domain.league;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.domain.Repository;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;

@Configuration
class LeagueConfiguration {

    @Bean
    LeagueFacade leagueFacade() {
        LeagueCRUDService leagueCRUDService = createLeagueCRUDService();
        MatchCRUDService matchCRUDService = createMatchCRUDService();
        return new LeagueFacade(leagueCRUDService, matchCRUDService);
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
