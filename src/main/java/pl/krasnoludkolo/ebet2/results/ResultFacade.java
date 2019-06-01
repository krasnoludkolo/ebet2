package pl.krasnoludkolo.ebet2.results;

import io.haste.TimeSource;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import pl.krasnoludkolo.ebet2.external.ExternalFacade;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.infrastructure.Success;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;
import pl.krasnoludkolo.ebet2.points.PointsFacade;
import pl.krasnoludkolo.ebet2.points.api.PointsError;
import pl.krasnoludkolo.ebet2.results.api.ResultError;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

public class ResultFacade {

    private final ExternalFacade externalFacade;
    private LeagueUpdater leagueUpdater;
    private PointsFacade pointsFacade;
    private LeagueFacade leagueFacade;
    private ResultsUpdater resultsUpdater;

    ResultFacade(LeagueFacade leagueFacade, ExternalFacade externalFacade, PointsFacade pointsFacade, ScheduledExecutorService executorService, Repository<UpdateDetails> repository, TimeSource timeSource) {
        this.pointsFacade = pointsFacade;
        this.leagueFacade = leagueFacade;
        this.externalFacade = externalFacade;
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

    Option<MatchDTO> getMatchByUUID(UUID matchUUID) {
        return leagueFacade.getMatchByUUID(matchUUID).toOption();
    }

    public Either<ResultError, UUID> registerLeague(String leagueName, ExternalSourceConfiguration config) {
        return leagueFacade.createLeague(leagueName)
                .mapLeft(x -> ResultError.LEAGUE_NAME_DUPLICATION)
                .flatMap(uuid -> initializeExternalSourceConfiguration(config, uuid))
                .map(this::updateLeague);
    }

    private Either<ResultError, UUID> initializeExternalSourceConfiguration(ExternalSourceConfiguration config, UUID uuid) {
        return Try
                .ofCallable(() ->
                        externalFacade
                                .initializeLeagueConfiguration(config, uuid)
                                .mapLeft(x -> ResultError.NO_EXTERNAL_CLIENT)
                )
                .onFailure(r -> leagueFacade.removeLeague(uuid))
                .toEither(ResultError.DOWNLOAD_ERROR)
                .flatMap(i -> i);
    }

    private UUID updateLeague(UUID uuid) {
        leagueUpdater.updateLeague(uuid);
        return uuid;
    }
}
