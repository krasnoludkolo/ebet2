package pl.krasnoludkolo.ebet2.league;

import io.haste.Haste;
import io.haste.TimeSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.infrastructure.SpringDataRepositoryAdapter;
import pl.krasnoludkolo.ebet2.update.UpdaterFacade;

import java.util.function.UnaryOperator;

@Configuration
public class LeagueConfiguration {

    @Bean
    public LeagueFacade leagueFacadeBean(LeagueRepository leagueRepository, MatchRepository matchRepository, UpdaterFacade updaterFacade) {
        MatchManager matchManager = createMatchManager(matchRepository);
        LeagueManager leagueManager = createLeagueManager(leagueRepository, matchManager, updaterFacade);
        TimeSource timeProvider = Haste.TimeSource.systemTimeSource();
        return new LeagueFacade(leagueManager, matchManager, timeProvider);
    }

    private LeagueManager createLeagueManager(LeagueRepository leagueRepository, MatchManager matchManager, UpdaterFacade updaterFacade) {
        UnaryOperator<League> d2e = l -> l;
        UnaryOperator<League> e2d = l -> l;
        SpringDataRepositoryAdapter<League, League> adapter = new SpringDataRepositoryAdapter<>(leagueRepository, d2e, e2d);
        return new LeagueManager(adapter, matchManager, updaterFacade);
    }

    private MatchManager createMatchManager(MatchRepository matchRepository) {
        UnaryOperator<Match> d2e = m -> m;
        UnaryOperator<Match> e2d = m -> m;
        SpringDataRepositoryAdapter<Match, Match> adapter = new SpringDataRepositoryAdapter<>(matchRepository, d2e, e2d);
        return new MatchManager(adapter);
    }

    public LeagueFacade inMemoryLeagueFacade(TimeSource timeSource, UpdaterFacade updaterFacade) {
        Repository<Match> matchRepository = new InMemoryRepository<>();
        MatchManager matchManager = new MatchManager(matchRepository);
        Repository<League> leagueRepository = new InMemoryRepository<>();
        LeagueManager leagueManager = new LeagueManager(leagueRepository, matchManager, updaterFacade);
        return new LeagueFacade(leagueManager, matchManager, timeSource);
    }

}
