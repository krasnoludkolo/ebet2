package pl.krasnoludkolo.ebet2.results;

import io.haste.TimeSource;
import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.external.ExternalFacade;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.infrastructure.Success;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;
import pl.krasnoludkolo.ebet2.points.PointsFacade;
import pl.krasnoludkolo.ebet2.points.api.PointsError;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

public class ResultFacade {

    private LeagueUpdater leagueUpdater;
    private PointsFacade pointsFacade;
    private LeagueFacade leagueFacade;
    private ResultsUpdater resultsUpdater;

    ResultFacade(LeagueFacade leagueFacade, ExternalFacade externalFacade, PointsFacade pointsFacade, ScheduledExecutorService executorService, Repository<UpdateDetails> repository, TimeSource timeSource) {
        this.pointsFacade = pointsFacade;
        this.leagueFacade = leagueFacade;
        this.leagueUpdater = new LeagueUpdater(leagueFacade, this, externalFacade);
        this.resultsUpdater = new ResultsUpdater(executorService, repository, externalFacade, timeSource, this);
    }

    public Either<PointsError, MatchDTO> registerMatch(NewMatchDTO match) {
        return leagueFacade
                .addMatchToLeague(match)
                .mapLeft(x -> PointsError.LEAGUE_NOT_FOUND)
                .map(resultsUpdater::schedule);
    }

    public Either<PointsError, Success> registerMatchResult(UUID matchUUID, MatchResult result) {
        return pointsFacade.updateResultsForMatchInLeague(matchUUID, result);
    }

    public Either<PointsError, Success> manuallyUpdateLeague(UUID leagueUUID) {
        return leagueUpdater.updateLeague(leagueUUID);
    }


}
