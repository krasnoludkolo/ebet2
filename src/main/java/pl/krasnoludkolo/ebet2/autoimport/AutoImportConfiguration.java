package pl.krasnoludkolo.ebet2.autoimport;

import io.vavr.collection.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.autoimport.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.autoimport.footballdata.FootballDataClient;
import pl.krasnoludkolo.ebet2.autoimport.mockclient.ExternalClientMock;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;

@Configuration
public class AutoImportConfiguration {


    @Bean
    @Autowired
    public AutoImportFacade autoImportBean(LeagueFacade leagueFacade) {
        LeagueUpdater leagueUpdater = new LeagueUpdater(leagueFacade);
        Repository<LeagueDetails> leagueDetailsRepository = new InMemoryRepository<>();
        return new AutoImportFacade(leagueFacade, leagueUpdater, List.of(FootballDataClient.create()), leagueDetailsRepository);
    }

    public AutoImportFacade inMemory(LeagueFacade leagueFacade) {
        List<ExternalSourceClient> list = List.of(new ExternalClientMock(ExternalClientMock.SOME_MATCHES));
        LeagueUpdater leagueUpdater = new LeagueUpdater(leagueFacade);
        Repository<LeagueDetails> leagueDetailsRepository = new InMemoryRepository<>();
        return new AutoImportFacade(leagueFacade, leagueUpdater, list, leagueDetailsRepository);
    }

    public AutoImportFacade inMemory(LeagueFacade leagueFacade, ExternalSourceClient mockClient) {
        List<ExternalSourceClient> list = List.of(mockClient);
        LeagueUpdater leagueUpdater = new LeagueUpdater(leagueFacade);
        Repository<LeagueDetails> leagueDetailsRepository = new InMemoryRepository<>();
        return new AutoImportFacade(leagueFacade, leagueUpdater, list, leagueDetailsRepository);
    }



}
