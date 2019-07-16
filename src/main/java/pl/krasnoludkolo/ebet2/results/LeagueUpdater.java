package pl.krasnoludkolo.ebet2.results;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.external.ExternalFacade;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.infrastructure.Success;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.dto.LeagueDTO;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchResult;
import pl.krasnoludkolo.ebet2.league.api.dto.NewMatchDTO;
import pl.krasnoludkolo.ebet2.points.api.PointsError;

import java.util.Objects;
import java.util.UUID;

class LeagueUpdater {

    private LeagueFacade leagueFacade;
    private ResultFacade resultFacade;
    private ExternalFacade externalFacade;

    LeagueUpdater(LeagueFacade leagueFacade, ResultFacade resultFacade, ExternalFacade externalFacade) {
        this.leagueFacade = leagueFacade;
        this.resultFacade = resultFacade;
        this.externalFacade = externalFacade;
    }

    Either<PointsError, Success> updateLeague(UUID leagueUUID) {
        return externalFacade
                .downloadLeague(leagueUUID)
                .map(matchInfoList -> updateLeague(leagueUUID, matchInfoList))
                .mapLeft(x -> PointsError.LEAGUE_NOT_FOUND);
    }

    private Success updateLeague(UUID leagueUUID, List<MatchInfo> matchInfoList) {
        List<MatchDTO> matchesFromLeague = getAllMatchesFromLeague(leagueUUID);
        setResult(matchInfoList, matchesFromLeague);
        addNewMatches(leagueUUID, matchInfoList, matchesFromLeague);
        return new Success();
    }

    private void addNewMatches(UUID leagueUUID, List<MatchInfo> matchInfoList, List<MatchDTO> matchesFromLeague) {
        List<MatchInfo> newMatchesInLeague = getNewMatchesInLeague(matchInfoList, matchesFromLeague);
        List<MatchInfo> withoutResult = newMatchesInLeague.filter(matchInfo -> !matchInfo.isFinished());
        List<MatchInfo> withResult = newMatchesInLeague.filter(MatchInfo::isFinished);

        withoutResult.map(m -> mapToNewMatchDTO(m, leagueUUID)).forEach(resultFacade::registerMatch);
        withResult.map(m -> mapToMatchDTO(m, leagueUUID)).forEach(leagueFacade::addFinishedMatchToLeague);
    }

    private void setResult(List<MatchInfo> matchInfoList, List<MatchDTO> matchesFromLeague) {
        List<Tuple2<UUID, MatchResult>> newToOldMatchesMap = getNewToOldMatchesMap(matchInfoList, matchesFromLeague);
        newToOldMatchesMap.forEach(this::setMatchResultsAndUpdateResults);
    }

    private void setMatchResultsAndUpdateResults(Tuple2<UUID, MatchResult> t) {
        resultFacade.registerMatchResult(t._1, t._2);
    }

    private List<MatchDTO> getAllMatchesFromLeague(UUID leagueUUID) {
        return leagueFacade
                .getLeagueByUUID(leagueUUID)
                .map(LeagueDTO::getMatchDTOS)
                .map(List::ofAll)
                .getOrElse(List.empty());
    }

    private List<Tuple2<UUID, MatchResult>> getNewToOldMatchesMap(List<MatchInfo> matchInfoList, List<MatchDTO> matchesFromRound) {
        return matchesFromRound
                .filter(this::matchesDTOWithNotSetResult)
                .map(matchDTO -> toMatchUUIDAndResultTuple(matchInfoList, matchDTO))
                .filter(this::tupleWithNoNotSetResult);
    }

    private boolean matchesDTOWithNotSetResult(MatchDTO matchDTO) {
        return matchDTO.getResult() == MatchResult.NOT_SET;
    }

    private Tuple2<UUID, MatchResult> toMatchUUIDAndResultTuple(List<MatchInfo> matchInfoList, MatchDTO matchDTO) {
        return Tuple.of(matchDTO.getUuid(), findCorrespondingMatch(matchDTO, matchInfoList).getResult());
    }

    private boolean tupleWithNoNotSetResult(Tuple2<UUID, MatchResult> t) {
        return t._2 != MatchResult.NOT_SET;
    }


    private MatchInfo findCorrespondingMatch(MatchDTO matchDTO, List<MatchInfo> matchInfoList) {
        return matchInfoList
                .find(matchInfo -> correspondingMatch(matchDTO, matchInfo))
                .getOrElseThrow(IllegalStateException::new);
    }

    private boolean correspondingMatch(MatchDTO matchDTO, MatchInfo matchInfo) {
        return isSameMatch(matchDTO, matchInfo);
    }

    private boolean isSameMatch(MatchDTO matchDTO, MatchInfo matchInfo) {
        return Objects.equals(matchDTO.getHost(), matchInfo.getHostName())
                && Objects.equals(matchDTO.getGuest(), matchInfo.getGuestName())
                && matchDTO.getRound() == matchInfo.getRound();
    }

    private List<MatchInfo> getNewMatchesInLeague(List<MatchInfo> matchInfoList, List<MatchDTO> matchesFromLeague) {
        return matchInfoList
                .reject(matchInfo -> isInLeague(matchInfo, matchesFromLeague));
    }

    private boolean isInLeague(MatchInfo matchInfo, List<MatchDTO> matchesFromLeague) {
        return matchesFromLeague.exists(matchDTO -> isSameMatch(matchDTO, matchInfo));
    }

    private NewMatchDTO mapToNewMatchDTO(MatchInfo matchInfo, UUID leagueUUID) {
        return new NewMatchDTO(matchInfo.getHostName(), matchInfo.getGuestName(), matchInfo.getRound(), leagueUUID, matchInfo.getMatchStartDate());
    }

    private MatchDTO mapToMatchDTO(MatchInfo matchInfo, UUID leagueUUID) {
        MatchDTO matchDTO = MatchDTO.fromNewMatchDTO(mapToNewMatchDTO(matchInfo, leagueUUID));
        matchDTO.setResult(matchInfo.getResult());
        return matchDTO;
    }

}
