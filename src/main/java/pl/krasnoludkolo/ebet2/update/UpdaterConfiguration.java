package pl.krasnoludkolo.ebet2.update;

import io.haste.Haste;
import io.haste.TimeSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.krasnoludkolo.ebet2.external.ExternalFacade;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

@Configuration
public class UpdaterConfiguration {

    @Bean
    public UpdaterFacade updaterFacade(ExternalFacade externalFacade) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        Repository<UpdateDetails> repository = createRepository();
        TimeSource timeSource = Haste.TimeSource.systemTimeSource();
        return new UpdaterFacade(executorService, repository, externalFacade, timeSource);
    }

    private Repository<UpdateDetails> createRepository() {
        Function<UpdateDetails, UpdateDetails> domainToEntityMapper = Function.identity();
        Function<UpdateDetails, UpdateDetails> entityToDomainMapper = Function.identity();
        return new JOOQUpdateDetailsRepository(domainToEntityMapper, entityToDomainMapper);
    }

    public UpdaterFacade inMemory(ScheduledExecutorService service, ExternalFacade externalFacade, TimeSource timeSource) {
        Repository<UpdateDetails> repository = new InMemoryRepository<>();
        return new UpdaterFacade(service, repository, externalFacade, timeSource);
    }

}
