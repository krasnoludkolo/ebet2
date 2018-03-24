package pl.krasnoludkolo.ebet2.autoimport;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;

@Configuration
class AutoImportConfiguration {


    @Bean
    public AutoImportFacade autoImportBean(LeagueFacade leagueFacade) {
        return new AutoImportFacade(leagueFacade);
    }


}
