package pl.krasnoludkolo.ebet2.results;

import io.vavr.control.Either;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.external.ExternalFacade;
import pl.krasnoludkolo.ebet2.infrastructure.Success;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.LeagueError;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.results.api.LeagueResultsDTO;
import pl.krasnoludkolo.ebet2.results.api.ResultError;
import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;

import java.util.UUID;

public class ResultFacade {

    private LeagueResultsManager crudService;
    private LeagueResultsUpdater leagueResultsUpdater;
    private LeagueFacade leagueFacade;
    private LeagueUpdater leagueUpdater;

    ResultFacade(LeagueResultsManager service, LeagueResultsUpdater leagueResultsUpdater, LeagueFacade leagueFacade, ExternalFacade externalFacade) {
        this.crudService = service;
        this.leagueResultsUpdater = leagueResultsUpdater;
        this.leagueFacade = leagueFacade;
        this.leagueUpdater = new LeagueUpdater(leagueFacade, this, externalFacade);
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

    public Either<ResultError, Success> registerMatchResult(UUID matchUUID, MatchResult result) {
        return leagueFacade
                .getMatchByUUID(matchUUID)
                .flatMap(m -> leagueFacade.setMatchResult(m.getUuid(), result))
                .mapLeft(this::mapLeagueFacadeError)
                .map(m -> leagueResultsUpdater.updateResultsForMatchInLeague(m, result));
    }

    public Either<ResultError, Success> manuallyUpdateLeague(UUID leagueUUID) {
        return leagueUpdater.updateLeague(leagueUUID);
    }

    private ResultError mapLeagueFacadeError(LeagueError error) {
        return error == LeagueError.SET_NOT_SET_RESULT ? ResultError.SET_NOT_SET_RESULT : ResultError.MATCH_NOT_FOUND;
    }

}
