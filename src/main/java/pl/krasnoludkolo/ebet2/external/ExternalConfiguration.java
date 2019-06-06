package pl.krasnoludkolo.ebet2.external;

import io.vavr.collection.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.external.clients.elkartoflicho.ElkartoflichoClient;
import pl.krasnoludkolo.ebet2.external.clients.footballdata.FootballDataClient;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.infrastructure.SpringDataRepositoryAdapter;

import javax.persistence.EntityManagerFactory;

@Configuration
public class ExternalConfiguration {


    @Bean
    @Autowired
    public ExternalFacade autoImportBean(SpringLeagueDetailsRepository repo, EntityManagerFactory entityManager) {
        Repository<LeagueDetails> leagueDetailsRepository = new SpringDataRepositoryAdapter<>(repo, e -> e, e -> e, entityManager);
        List<ExternalSourceClient> clients = List.of(FootballDataClient.create(), new ElkartoflichoClient());
        return new ExternalFacade(clients, leagueDetailsRepository);
    }

    public ExternalFacade inMemory(ExternalSourceClient mockClient) {
        List<ExternalSourceClient> list = List.of(mockClient);
        Repository<LeagueDetails> leagueDetailsRepository = new InMemoryRepository<>();
        return new ExternalFacade(list, leagueDetailsRepository);
    }



}
