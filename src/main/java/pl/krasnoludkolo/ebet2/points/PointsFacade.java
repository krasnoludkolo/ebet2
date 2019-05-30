package pl.krasnoludkolo.ebet2.points;

import io.vavr.control.Either;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.infrastructure.Success;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.LeagueError;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.points.api.LeagueResultsDTO;
import pl.krasnoludkolo.ebet2.points.api.PointsError;
import pl.krasnoludkolo.ebet2.points.api.UserResultDTO;

import java.util.UUID;

public class PointsFacade {

    private LeagueResultsManager crudService;
    private LeagueResultsUpdater leagueResultsUpdater;
    private LeagueFacade leagueFacade;


    PointsFacade(LeagueResultsUpdater leagueResultsUpdater, LeagueResultsManager crudService, LeagueFacade leagueFacade) {
        this.crudService = crudService;
        this.leagueResultsUpdater = leagueResultsUpdater;
        this.leagueFacade = leagueFacade;
    }

    public Option<LeagueResultsDTO> getResultsForLeague(UUID leagueUUID) {
        return Option.of(crudService.getResultsForLeague(leagueUUID).toDTO());
    }

    public Option<UserResultDTO> getResultsFromLeagueToUser(UUID leagueUUID, UUID userUUID) {
        return Option.of(crudService
                .getResultsFromLeagueToUser(leagueUUID, userUUID)
                .map(UserResult::toDTO)
                .reduce((a, b) -> new UserResultDTO(a.getUserUUID(), a.getPointCounter() + b.getPointCounter())));
    }

    public Either<PointsError, Success> updateResultsForMatchInLeague(UUID matchUUID, MatchResult result) {
        return leagueFacade
                .getMatchByUUID(matchUUID)
                .flatMap(m -> leagueFacade.setMatchResult(m.getUuid(), result))
                .mapLeft(this::mapLeagueFacadeError)
                .map(m -> leagueResultsUpdater.updateResultsForMatchInLeague(m, result));
    }


    private PointsError mapLeagueFacadeError(LeagueError error) {
        return error == LeagueError.SET_NOT_SET_RESULT ? PointsError.SET_NOT_SET_RESULT : PointsError.MATCH_NOT_FOUND;
    }
}
