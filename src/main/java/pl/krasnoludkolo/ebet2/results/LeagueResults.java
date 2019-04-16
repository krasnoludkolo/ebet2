package pl.krasnoludkolo.ebet2.results;

import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.results.api.LeagueResultsDTO;
import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;

import java.util.UUID;

class LeagueResults {

    private final UUID leagueUUID;

    private Map<Integer, RoundResult> roundsResults;


    static LeagueResults create(UUID leagueUUID) {
        return new LeagueResults(leagueUUID, HashMap.empty());
    }

    static LeagueResults fromEntity(List<RoundResultsEntity> roundResultsEntity) {
        UUID leagueUUID = roundResultsEntity.headOption().getOrElseThrow(IllegalStateException::new).getLeagueUUID();

        Map<Integer, RoundResult> resultsInRounds =
                roundResultsEntity
                        .toMap(entity -> Tuple.of(entity.getRound(), entity))
                        .mapValues(RoundResult::fromEntity);

        return new LeagueResults(leagueUUID, resultsInRounds);
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

    List<RoundResultsEntity> toEntity() {
        return roundsResults.toList().map(t -> t._2.toEntity(leagueUUID, t._1));
    }

    UUID getLeagueUUID() {
        return leagueUUID;
    }
}