package pl.krasnoludkolo.ebet2.results;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.external.ExternalFacade;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.points.PointsFacade;

@Configuration
public class ResultConfiguration {

    @Bean
    public ResultFacade resultFacadeBean(LeagueFacade leagueFacade, ExternalFacade externalFacade, PointsFacade pointsFacade) {
        return new ResultFacade(leagueFacade, externalFacade, pointsFacade);
    }

    public ResultFacade inMemoryResult(LeagueFacade leagueFacade, ExternalFacade externalFacade, PointsFacade pointsFacade) {
        return new ResultFacade(leagueFacade, externalFacade, pointsFacade);
    }

}