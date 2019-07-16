package pl.krasnoludkolo.ebet2.points;

import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchResult;
import pl.krasnoludkolo.ebet2.points.api.LeagueResultsDTO;
import pl.krasnoludkolo.ebet2.points.api.UserResultDTO;

import java.util.UUID;

class LeagueResults {

    private final UUID leagueUUID;

    private Map<Integer, RoundResult> roundsResults;


    static LeagueResults create(UUID leagueUUID) {
        return new LeagueResults(leagueUUID, HashMap.empty());
    }

    static LeagueResults fromEntity(LeagueResultEntity leagueResultEntity) {
        UUID leagueUUID = leagueResultEntity.getLeagueUUID();
        Map<Integer, RoundResult> rounds =
                List.ofAll(leagueResultEntity.getRoundResultsEntityList())
                        .toMap(x -> Tuple.of(x.getRound(), x))
                        .mapValues(RoundResult::fromEntity);
        return new LeagueResults(leagueUUID, rounds);
    }

    private LeagueResults(UUID leagueUUID, Map<Integer, RoundResult> roundsResults) {
        this.leagueUUID = leagueUUID;
        this.roundsResults = roundsResults;
    }

    LeagueResults updateResults(MatchResult result, List<BetDTO> bets, int round) {
        RoundResult roundResult = roundsResults
                .getOrElse(round, RoundResult.create())
                .updateResults(result, bets);
        Map<Integer, RoundResult> resultMap = roundsResults.put(round, roundResult);
        return new LeagueResults(leagueUUID, resultMap);
    }


    List<UserResult> getGeneralUserResult(UUID uuid) {
        return roundsResults
                .values()
                .map(roundResult -> roundResult.getUserResult(uuid))
                .toList();
    }

    LeagueResultsDTO toDTO() {
        List<List<UserResultDTO>> roundsResult = getRoundsResults();
        List<UserResultDTO> generalResult = getGeneralResult();
        return new LeagueResultsDTO(generalResult, roundsResult, leagueUUID);
    }

    private List<List<UserResultDTO>> getRoundsResults() {
        return roundsResults
                .values()
                .map(RoundResult::toUserResultDTOList)
                .toList();
    }

    private List<UserResultDTO> getGeneralResult() {
        Set<UUID> allUsers = getAllUsers();
        return allUsers.
                map(uuid -> {
                    int points = roundsResults
                            .values()
                            .map(round -> round.getUserResult(uuid).getUserPointResult())
                            .sum()
                            .intValue();
                    return new UserResultDTO(uuid, points);
                })
                .toList()
                .sorted()
                .reverse();
    }

    private Set<UUID> getAllUsers() {
        return roundsResults
                .values()
                .flatMap(RoundResult::getAllUsers)
                .toSet();
    }

    LeagueResultEntity toEntity() {
        List<RoundResultsEntity> rounds = roundsResults.toList().map(t -> t._2.toEntity(t._1));
        return new LeagueResultEntity(leagueUUID, rounds.toJavaList());
    }

    UUID getLeagueUUID() {
        return leagueUUID;
    }
}