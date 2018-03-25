package pl.krasnoludkolo.ebet2.autoimport;

import io.vavr.collection.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;

@Configuration
class AutoImportConfiguration {


    @Bean
    public AutoImportFacade autoImportBean(LeagueFacade leagueFacade) {
        List<ExternalSourceClient> list = List.of(new FootballDataClient());
        return new AutoImportFacade(leagueFacade, list);
    }


}
