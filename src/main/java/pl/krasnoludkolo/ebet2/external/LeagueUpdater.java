package pl.krasnoludkolo.ebet2.external;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.LeagueDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;

import java.util.Objects;
import java.util.UUID;

class LeagueUpdater {

    private LeagueFacade leagueFacade;

    LeagueUpdater(LeagueFacade leagueFacade) {
        this.leagueFacade = leagueFacade;
    }

    LeagueDetails updateLeague(LeagueDetails leagueDetails, ExternalSourceClient client) {
        ExternalSourceConfiguration config = LeagueDetailsCreator.toExternalSourceConfiguration(leagueDetails);
        List<MatchInfo> matchInfoList = client.downloadAllRounds(config);
        List<MatchDTO> matchesFromLeague = getAllMatchesFromLeague(leagueDetails);
        List<Tuple2<UUID, MatchResult>> newToOldMatchesMap = getNewToOldMatchesMap(matchInfoList, matchesFromLeague);
        newToOldMatchesMap.forEach(t -> leagueFacade.setMatchResult(t._1, t._2));
        return leagueDetails;
    }

    private List<MatchDTO> getAllMatchesFromLeague(LeagueDetails leagueDetails) {
        UUID leagueUUID = leagueDetails.getLeagueUUID();
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

    private boolean tupleWithNoNotSetResult(Tuple2<UUID, MatchResult> t) {
        return t._2 != MatchResult.NOT_SET;
    }

}
