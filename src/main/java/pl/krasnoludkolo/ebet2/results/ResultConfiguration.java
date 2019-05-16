package pl.krasnoludkolo.ebet2.results;

import io.haste.BlockingScheduledExecutionService;
import io.haste.Haste;
import io.haste.TimeSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.external.ExternalFacade;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.points.PointsFacade;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.UnaryOperator;

@Configuration
public class ResultConfiguration {

    @Bean
    public ResultFacade resultFacadeBean(LeagueFacade leagueFacade, ExternalFacade externalFacade, PointsFacade pointsFacade) {

        UnaryOperator<UpdateDetails> d2e = x -> x;
        UnaryOperator<UpdateDetails> e2d = x -> x;
        Repository<UpdateDetails> repository = new JOOQUpdateDetailsRepository(d2e, e2d);


        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        TimeSource timeSource = Haste.TimeSource.systemTimeSource();
        return new ResultFacade(leagueFacade, externalFacade, pointsFacade, executorService, repository, timeSource);
    }

    public ResultFacade inMemoryResult(LeagueFacade leagueFacade, ExternalFacade externalFacade, PointsFacade pointsFacade, BlockingScheduledExecutionService executorService) {
        Repository<UpdateDetails> repository = new InMemoryRepository<>();
        return new ResultFacade(leagueFacade, externalFacade, pointsFacade, executorService, repository, executorService);
    }

}