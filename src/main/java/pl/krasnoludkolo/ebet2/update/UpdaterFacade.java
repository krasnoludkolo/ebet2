package pl.krasnoludkolo.ebet2.update;

import io.haste.TimeSource;
import pl.krasnoludkolo.ebet2.external.ExternalFacade;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;

import java.util.concurrent.ScheduledExecutorService;

public final class UpdaterFacade {

    private ResultsUpdater resultsUpdater;


    public UpdaterFacade(ScheduledExecutorService executorService, Repository<UpdateDetails> repository, ExternalFacade externalFacade, TimeSource timeSource) {
        this.resultsUpdater = new ResultsUpdater(executorService, repository, externalFacade, timeSource);
    }

    public MatchDTO scheduleUpdate(MatchDTO matchDTO) {
        resultsUpdater.schedule(matchDTO);
        return matchDTO;
    }
}
