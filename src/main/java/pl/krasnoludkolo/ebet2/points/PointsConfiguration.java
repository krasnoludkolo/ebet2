package pl.krasnoludkolo.ebet2.points;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.infrastructure.SpringDataRepositoryAdapter;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;

import javax.persistence.EntityManagerFactory;
import java.util.function.Function;

@Configuration
public class PointsConfiguration {


    @Bean
    public PointsFacade pointsFacade(BetFacade betFacade, LeagueResultsRepository springRepository, LeagueFacade leagueFacade, EntityManagerFactory entityManager) {
        Repository<LeagueResults> repository = createRepository(springRepository, entityManager);
        LeagueResultsManager service = new LeagueResultsManager(repository);
        LeagueResultsUpdater leagueResultsUpdater = new LeagueResultsUpdater(service, betFacade);
        LeagueResultsManager crudService = new LeagueResultsManager(repository);
        return new PointsFacade(leagueResultsUpdater, crudService, leagueFacade);
    }


    private SpringDataRepositoryAdapter<LeagueResults, LeagueResultEntity> createRepository(LeagueResultsRepository springRepository, EntityManagerFactory entityManager) {
        Function<LeagueResults, LeagueResultEntity> d2e = LeagueResults::toEntity;
        Function<LeagueResultEntity, LeagueResults> e2d = LeagueResults::fromEntity;
        return new SpringDataRepositoryAdapter<>(springRepository, d2e, e2d, entityManager);
    }

    public PointsFacade inMemoryPointsFacade(BetFacade betFacade, LeagueFacade leagueFacade) {
        Repository<LeagueResults> repository = new InMemoryRepository<>();
        LeagueResultsManager service = new LeagueResultsManager(repository);
        LeagueResultsUpdater leagueResultsUpdater = new LeagueResultsUpdater(service, betFacade);
        LeagueResultsManager crudService = new LeagueResultsManager(repository);
        return new PointsFacade(leagueResultsUpdater, crudService, leagueFacade);
    }
}
