package pl.krasnoludkolo.ebet2.autoimport;

import io.vavr.collection.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.autoimport.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.autoimport.footballdata.FootballDataClient;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;

@Configuration
public class AutoImportConfiguration {


    @Bean
    @Autowired
    public AutoImportFacade autoImportBean(LeagueFacade leagueFacade) {
        return new AutoImportFacade(leagueFacade, List.of(FootballDataClient.create()));
    }

    public AutoImportFacade inMemory(LeagueFacade leagueFacade) {
        List<ExternalSourceClient> list = List.of(new ExternalClientMock());
        return new AutoImportFacade(leagueFacade, list);
    }



}
