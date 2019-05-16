package pl.krasnoludkolo.ebet2.update;

import io.haste.TimeSource;
import pl.krasnoludkolo.ebet2.external.ExternalFacade;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;

final class ResultsUpdater {

    private final ScheduledExecutorService executorService;
    private final Repository<UpdateDetails> repository;
    private final ExternalFacade externalFacade;
    private final TimeSource timeSource;

    ResultsUpdater(ScheduledExecutorService executorService, Repository<UpdateDetails> repository, ExternalFacade externalFacade, TimeSource timeSource) {
        this.executorService = executorService;
        this.repository = repository;
        this.externalFacade = externalFacade;
        this.timeSource = timeSource;
    }

    void schedule(MatchDTO match) {
        Duration offset = Duration.between(match.getMatchStartDate(), timeSource.now());
//        executorService.schedule(()->externalFacade.(match.getLeagueUUID()),offset.getSeconds(), TimeUnit.SECONDS);
    }


}
