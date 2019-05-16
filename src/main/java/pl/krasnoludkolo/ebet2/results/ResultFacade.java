package pl.krasnoludkolo.ebet2.results;

import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.external.ExternalFacade;
import pl.krasnoludkolo.ebet2.infrastructure.Success;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.points.PointsFacade;
import pl.krasnoludkolo.ebet2.points.api.PointsError;

import java.util.UUID;

public class ResultFacade {

    private LeagueUpdater leagueUpdater;
    private PointsFacade pointsFacade;

    ResultFacade(LeagueFacade leagueFacade, ExternalFacade externalFacade, PointsFacade pointsFacade) {
        this.pointsFacade = pointsFacade;
        this.leagueUpdater = new LeagueUpdater(leagueFacade, this, externalFacade);
    }

    public Either<PointsError, Success> registerMatchResult(UUID matchUUID, MatchResult result) {
        return pointsFacade.updateResultsForMatchInLeague(matchUUID, result);
    }

    public Either<PointsError, Success> manuallyUpdateLeague(UUID leagueUUID) {
        return leagueUpdater.updateLeague(leagueUUID);
    }


}
