package pl.krasnoludkolo.ebet2.league;

import io.haste.Haste;
import io.haste.TimeSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.infrastructure.SpringDataRepositoryAdapter;

import javax.persistence.EntityManagerFactory;
import java.util.function.UnaryOperator;

@Configuration
public class LeagueConfiguration {

    @Bean
    public LeagueFacade leagueFacadeBean(LeagueRepository leagueRepository, MatchRepository matchRepository, EntityManagerFactory entityManager) {
        MatchManager matchManager = createMatchManager(matchRepository, entityManager);
        LeagueManager leagueManager = createLeagueManager(leagueRepository, matchManager, entityManager);
        TimeSource timeProvider = Haste.TimeSource.systemTimeSource();
        return new LeagueFacade(leagueManager, matchManager, timeProvider);
    }

    private LeagueManager createLeagueManager(LeagueRepository leagueRepository, MatchManager matchManager, EntityManagerFactory entityManager) {
        UnaryOperator<League> d2e = UnaryOperator.identity();
        UnaryOperator<League> e2d = UnaryOperator.identity();
        SpringDataRepositoryAdapter<League, League> adapter = new SpringDataRepositoryAdapter<>(leagueRepository, d2e, e2d, entityManager);
        return new LeagueManager(adapter, matchManager);
    }

    private MatchManager createMatchManager(MatchRepository matchRepository, EntityManagerFactory entityManager) {
        UnaryOperator<Match> d2e = m -> m;
        UnaryOperator<Match> e2d = m -> m;
        SpringDataRepositoryAdapter<Match, Match> adapter = new SpringDataRepositoryAdapter<>(matchRepository, d2e, e2d, entityManager);
        return new MatchManager(adapter);
    }

    public LeagueFacade inMemoryLeagueFacade(TimeSource timeSource) {
        Repository<Match> matchRepository = new InMemoryRepository<>();
        MatchManager matchManager = new MatchManager(matchRepository);
        Repository<League> leagueRepository = new InMemoryRepository<>();
        LeagueManager leagueManager = new LeagueManager(leagueRepository, matchManager);
        return new LeagueFacade(leagueManager, matchManager, timeSource);
    }

}
