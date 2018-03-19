package pl.krasnoludkolo.ebet2.league;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.infrastructure.SpringDataRepositoryAdapter;
import pl.krasnoludkolo.ebet2.results.ResultFacade;

import java.util.function.Function;

@Configuration
public class LeagueConfiguration {

    @Bean
    public LeagueFacade leagueFacadeBean(ResultFacade resultFacade, LeagueRepository leagueRepository, MatchRepository matchRepository) {
        MatchManager matchManager = createMatchManager(matchRepository);
        LeagueManager leagueManager = createLeagueManager(leagueRepository, matchManager);
        return new LeagueFacade(leagueManager, matchManager, resultFacade);
    }

    private LeagueManager createLeagueManager(LeagueRepository leagueRepository, MatchManager matchManager) {
        Function<League, League> d2e = l -> l;
        Function<League, League> e2d = l -> l;
        SpringDataRepositoryAdapter<League, League> adapter = new SpringDataRepositoryAdapter<>(leagueRepository, d2e, e2d);
        return new LeagueManager(adapter, matchManager);
    }

    private MatchManager createMatchManager(MatchRepository matchRepository) {
        Function<Match, Match> d2e = m -> m;
        Function<Match, Match> e2d = m -> m;
        SpringDataRepositoryAdapter<Match, Match> adapter = new SpringDataRepositoryAdapter<>(matchRepository, d2e, e2d);
        return new MatchManager(adapter);
    }

    public LeagueFacade inMemoryLeagueFacade(ResultFacade resultFacade) {
        Repository<Match> matchRepository = new InMemoryRepository<>();
        MatchManager matchManager = new MatchManager(matchRepository);
        Repository<League> leagueRepository = new InMemoryRepository<>();
        LeagueManager leagueManager = new LeagueManager(leagueRepository, matchManager);
        return new LeagueFacade(leagueManager, matchManager, resultFacade);
    }

}