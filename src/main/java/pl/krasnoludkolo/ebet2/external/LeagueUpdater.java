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
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;
import pl.krasnoludkolo.ebet2.results.ResultFacade;

import java.util.Objects;
import java.util.UUID;

class LeagueUpdater {

    private LeagueFacade leagueFacade;
    private ResultFacade resultFacade;

    LeagueUpdater(LeagueFacade leagueFacade, ResultFacade resultFacade) {
        this.leagueFacade = leagueFacade;
        this.resultFacade = resultFacade;
    }

    LeagueDetails updateLeague(UpdateInfo updateInfo) {
        LeagueDetails leagueDetails = updateInfo.getLeagueDetails();
        ExternalSourceClient client = updateInfo.getClient();
        ExternalSourceConfiguration config = LeagueDetailsCreator.toExternalSourceConfiguration(leagueDetails);
        List<MatchInfo> matchInfoList = client.downloadAllRounds(config);
        List<MatchDTO> matchesFromLeague = getAllMatchesFromLeague(leagueDetails);
        List<Tuple2<UUID, MatchResult>> newToOldMatchesMap = getNewToOldMatchesMap(matchInfoList, matchesFromLeague);
        List<NewMatchDTO> newMatchesInLeague = getNewMatchesInLeague(matchInfoList, matchesFromLeague).map(matchInfo -> mapToNewMatchDTO(matchInfo, leagueDetails.getLeagueUUID()));
        newToOldMatchesMap.forEach(this::setMatchResultsAndUpdateResults);
        newMatchesInLeague.forEach(leagueFacade::addMatchToLeague);
        return leagueDetails;
    }

    private NewMatchDTO mapToNewMatchDTO(MatchInfo matchInfo, UUID leagueUUID) {
        return new NewMatchDTO(matchInfo.getHostName(), matchInfo.getGuestName(), matchInfo.getRound(), leagueUUID, matchInfo.getMatchStartDate());
    }

    private void setMatchResultsAndUpdateResults(Tuple2<UUID, MatchResult> t) {
        leagueFacade.setMatchResult(t._1, t._2);
        resultFacade.updateLeagueResultsForMatch(t._1);
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

    private List<MatchInfo> getNewMatchesInLeague(List<MatchInfo> matchInfoList, List<MatchDTO> matchesFromLeague) {
        return matchInfoList
                .reject(matchInfo -> isInLeague(matchInfo, matchesFromLeague));
    }

    private boolean isInLeague(MatchInfo matchInfo, List<MatchDTO> matchesFromLeague) {
        return matchesFromLeague.exists(matchDTO -> isSameMatch(matchDTO, matchInfo));
    }

}
