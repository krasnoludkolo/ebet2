package pl.krasnoludkolo.ebet2.external;

import io.vavr.collection.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.external.externalClients.elkartoflicho.ElkartoflichoClient;
import pl.krasnoludkolo.ebet2.external.externalClients.footballdata.FootballDataClient;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.infrastructure.SpringDataRepositoryAdapter;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.results.ResultFacade;

@Configuration
public class ExternalConfiguration {


    @Bean
    @Autowired
    public ExternalFacade autoImportBean(LeagueFacade leagueFacade, SpringLeagueDetailsRepository repo, ResultFacade resultFacade) {
        LeagueUpdater leagueUpdater = new LeagueUpdater(leagueFacade, resultFacade);
        Repository<LeagueDetails> leagueDetailsRepository = new SpringDataRepositoryAdapter<>(repo, e -> e, e -> e);
        List<ExternalSourceClient> clients = List.of(FootballDataClient.create(), new ElkartoflichoClient());
        LeagueInitializer leagueInitializer = new LeagueInitializer(leagueFacade);
        return new ExternalFacade(leagueInitializer, leagueUpdater, clients, leagueDetailsRepository);
    }

    public ExternalFacade inMemory(LeagueFacade leagueFacade, ResultFacade resultFacade, ExternalSourceClient mockClient) {
        List<ExternalSourceClient> list = List.of(mockClient);
        LeagueUpdater leagueUpdater = new LeagueUpdater(leagueFacade, resultFacade);
        Repository<LeagueDetails> leagueDetailsRepository = new InMemoryRepository<>();
        LeagueInitializer leagueInitializer = new LeagueInitializer(leagueFacade);
        return new ExternalFacade(leagueInitializer, leagueUpdater, list, leagueDetailsRepository);
    }



}
