package pl.krasnoludkolo.ebet2.autoimport;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import pl.krasnoludkolo.ebet2.autoimport.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.autoimport.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

class LeagueUpdater {

    private LeagueFacade leagueFacade;

    LeagueUpdater(LeagueFacade leagueFacade) {
        this.leagueFacade = leagueFacade;
    }

    LeagueDetails updateLeague(LeagueDetails leagueDetails, ExternalSourceClient client) {
        List<MatchInfo> matchInfoList = client.downloadAllRounds(leagueDetails.getConfiguration());
        List<MatchDTO> matchesFromRound = List.ofAll(leagueFacade.getLeagueByUUID(leagueDetails.getLeagueUUID()).get().getMatchDTOS());
        List<Tuple2<UUID, MatchResult>> newToOldMatchesMap = getNewToOldMatchesMap(matchInfoList, matchesFromRound);
        newToOldMatchesMap.forEach(t -> leagueFacade.setMatchResult(t._1, t._2));
        return leagueDetails;
    }

    private List<Tuple2<UUID, MatchResult>> getNewToOldMatchesMap(List<MatchInfo> matchInfoList, List<MatchDTO> matchesFromRound) {
        return matchesFromRound
                .filter(matchesDTOWithNotSetResult())
                .map(toMatchUUIDAndResultTuple(matchInfoList))
                .filter(tupleWithNoNotSetResult());
    }

    private Predicate<MatchDTO> matchesDTOWithNotSetResult() {
        return matchDTO -> matchDTO.getResult() == MatchResult.NOT_SET;
    }

    private Function<MatchDTO, Tuple2<UUID, MatchResult>> toMatchUUIDAndResultTuple(List<MatchInfo> matchInfoList) {
        return matchDTO ->
                Tuple.of(matchDTO.getUuid(), findCorrespondingMatch(matchDTO, matchInfoList).getResult());
    }

    private MatchInfo findCorrespondingMatch(MatchDTO matchDTO, List<MatchInfo> matchInfoList) {
        return matchInfoList.find(correspondingMatch(matchDTO)).getOrElseThrow(IllegalStateException::new);
    }

    private Predicate<MatchInfo> correspondingMatch(MatchDTO matchDTO) {
        return matchInfo -> isSameMatch(matchDTO, matchInfo);
    }

    private Predicate<Tuple2<UUID, MatchResult>> tupleWithNoNotSetResult() {
        return t -> t._2 != MatchResult.NOT_SET;
    }

    private boolean isSameMatch(MatchDTO matchDTO, MatchInfo matchInfo) {
        return Objects.equals(matchDTO.getHost(), matchInfo.getHostName())
                && Objects.equals(matchDTO.getGuest(), matchInfo.getGuestName());
    }

}
