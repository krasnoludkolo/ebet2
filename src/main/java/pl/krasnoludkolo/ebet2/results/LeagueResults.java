package pl.krasnoludkolo.ebet2.results;

import io.vavr.Tuple;
import io.vavr.collection.*;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.results.api.LeagueResultsDTO;
import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

class LeagueResults {

    private final static Logger LOGGER = getLogger(LeagueResults.class.getName());

    private final UUID leagueUUID;

    private Map<Integer, PriorityQueue<UserResult>> roundsResults;

    static LeagueResults create(UUID leagueUUID) {
        return new LeagueResults(leagueUUID, HashMap.empty());
    }

    static LeagueResults fromEntity(List<RoundResultsEntity> roundResultsEntity) {
        UUID leagueUUID = roundResultsEntity.headOption().getOrElseThrow(IllegalStateException::new).getLeagueUUID();

        Map<Integer, PriorityQueue<UserResult>> resultsInRounds =
                roundResultsEntity
                        .toMap(entity -> Tuple.of(entity.getRound(), List.ofAll(entity.getUserResultList())))
                        .mapValues(l -> l.map(UserResult::fromEntity).toPriorityQueue());

        return new LeagueResults(leagueUUID, resultsInRounds);
    }

    private LeagueResults(UUID leagueUUID, Map<Integer, PriorityQueue<UserResult>> roundsResults) {
        this.leagueUUID = leagueUUID;
        this.roundsResults = roundsResults;
    }

    LeagueResults updateResults(MatchResult result, List<BetDTO> bets, int round) {

        List<BetDTO> correctBets = bets.filter(betDTO -> betDTO.getBetTyp().match(result));
        List<UserResult> userResultsWithIncorrectBetNotInQueue = getUserResultsWithIncorrectBetNotInQueue(bets, correctBets, round);
        List<UserResult> userResultsWithCorrectBetsNotInQueue = getUserResultsForBetsNotInQueue(correctBets, round);

        PriorityQueue<UserResult> userResultPriorityQueue = roundsResults.getOrElse(round, PriorityQueue.empty());
        PriorityQueue<UserResult> results = userResultPriorityQueue
                .enqueueAll(userResultsWithCorrectBetsNotInQueue)
                .map(userResult -> addPointsForCorrectBet(correctBets, userResult))
                .enqueueAll(userResultsWithIncorrectBetNotInQueue);

        Map<Integer, PriorityQueue<UserResult>> resultMap = roundsResults.put(round, results);

        return new LeagueResults(leagueUUID, resultMap);
    }

    private UserResult addPointsForCorrectBet(final List<BetDTO> correctBets, final UserResult userResult) {
        return correctBets
                .find(betDTO -> userResult.hasName(betDTO.getUsername()))
                .map(t -> userResult.addPoint())
                .getOrElse(userResult);
    }

    private List<UserResult> getUserResultsWithIncorrectBetNotInQueue(List<BetDTO> bets, List<BetDTO> correctBets, int round) {
        List<BetDTO> betsListWithoutCorrectBets = bets.removeAll(correctBets);
        return getUserResultsForBetsNotInQueue(betsListWithoutCorrectBets, round);
    }

    private List<UserResult> getUserResultsForBetsNotInQueue(List<BetDTO> betsList, int round) {
        return betsList.filter(betDTO -> betNotInResultQueue(betDTO, round))
                .map(BetDTO::getUsername)
                .map(this::createNewUserResult);
    }

    private boolean betNotInResultQueue(BetDTO betDTO, int round) {
        return roundsResults.get(round).getOrElse(PriorityQueue.empty())
                .find(userResult -> userResult.hasName(betDTO.getUsername()))
                .isEmpty();
    }

    private UserResult createNewUserResult(String user) {
        LOGGER.log(Level.INFO, "Create new user with name: " + user);
        return UserResult.create(user);
    }

    List<UserResult> getGeneralUserResult(String username) {
        return roundsResults
                .values()
                .map(queue -> queue.find(userResult -> userResult.hasName(username)).getOrElse(UserResult.create(username)))
                .toList();
    }

    private Predicate<UserResult> withName(String user) {
        return userResult -> userResult.hasName(user);
    }

    LeagueResultsDTO toDTO() {
        List<List<UserResultDTO>> roundsResult = getRoundsResults();
        List<UserResultDTO> generalResult = getGeneralResult();
        return new LeagueResultsDTO(generalResult, roundsResult, leagueUUID);
    }

    private List<List<UserResultDTO>> getRoundsResults() {
        return roundsResults
                .mapValues(queue -> queue.toList().map(UserResult::toDTO))
                .toList()
                .map(t -> t._2.toList());
    }

    private List<UserResultDTO> getGeneralResult() {
        Set<String> allUsers = getAllUsers();
        return allUsers.
                map(name -> {
                    int points = roundsResults
                            .values()
                            .map(round -> getUserResultFromRound(name, round))
                            .sum()
                            .intValue();
                    return new UserResultDTO(name, points);
                })
                .toList()
                .sorted()
                .reverse();
    }

    private int getUserResultFromRound(String name, PriorityQueue<UserResult> round) {
        return round.find(withName(name)).map(UserResult::getUserPointResult).getOrElse(0);
    }

    private Set<String> getAllUsers() {
        return roundsResults
                .mapValues(queue -> queue.map(e -> e.toDTO().getName()))
                .values()
                .flatMap(l -> l)
                .toSet();
    }

    List<RoundResultsEntity> toEntity() {
        return roundsResults.toList().map(t -> {
            RoundResultsEntity roundResultsEntity = new RoundResultsEntity(UUID.randomUUID(), leagueUUID, new ArrayList<>(), t._1);
            roundResultsEntity.setUserResultList(t._2.map(e -> e.toEntity(roundResultsEntity)).toJavaList());
            return roundResultsEntity;
        });
    }

    UUID getLeagueUUID() {
        return leagueUUID;
    }
}