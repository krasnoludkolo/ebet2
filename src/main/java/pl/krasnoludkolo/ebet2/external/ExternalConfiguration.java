package pl.krasnoludkolo.ebet2.external;

import io.vavr.collection.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.external.externalClients.footballdata.FootballDataClient;
import pl.krasnoludkolo.ebet2.external.externalClients.mockclient.ExternalClientMock;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;

@Configuration
public class ExternalConfiguration {


    @Bean
    @Autowired
    public ExternalFacade autoImportBean(LeagueFacade leagueFacade) {
        LeagueUpdater leagueUpdater = new LeagueUpdater(leagueFacade);
        Repository<LeagueDetails> leagueDetailsRepository = new InMemoryRepository<>();
        return new ExternalFacade(leagueFacade, leagueUpdater, List.of(FootballDataClient.create()), leagueDetailsRepository);
    }

    public ExternalFacade inMemory(LeagueFacade leagueFacade) {
        List<ExternalSourceClient> list = List.of(new ExternalClientMock(ExternalClientMock.SOME_MATCHES));
        LeagueUpdater leagueUpdater = new LeagueUpdater(leagueFacade);
        Repository<LeagueDetails> leagueDetailsRepository = new InMemoryRepository<>();
        return new ExternalFacade(leagueFacade, leagueUpdater, list, leagueDetailsRepository);
    }

    public ExternalFacade inMemory(LeagueFacade leagueFacade, ExternalSourceClient mockClient) {
        List<ExternalSourceClient> list = List.of(mockClient);
        LeagueUpdater leagueUpdater = new LeagueUpdater(leagueFacade);
        Repository<LeagueDetails> leagueDetailsRepository = new InMemoryRepository<>();
        return new ExternalFacade(leagueFacade, leagueUpdater, list, leagueDetailsRepository);
    }



}
