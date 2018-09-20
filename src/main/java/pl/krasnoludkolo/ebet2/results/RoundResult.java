package pl.krasnoludkolo.ebet2.results;

import io.vavr.collection.List;
import io.vavr.collection.PriorityQueue;
import io.vavr.collection.Set;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

class RoundResult {

    private static final Logger LOGGER = getLogger(RoundResult.class.getName());

    private PriorityQueue<UserResult> userResults;

    static RoundResult fromEntity(RoundResultsEntity entity) {
        PriorityQueue<UserResult> results = List
                .ofAll(entity.getUserResultList())
                .map(UserResult::fromEntity)
                .toPriorityQueue();
        return new RoundResult(results);
    }

    static RoundResult create() {
        return new RoundResult(PriorityQueue.empty());
    }

    private RoundResult(PriorityQueue<UserResult> userResults) {
        this.userResults = userResults;
    }

    RoundResult updateResults(MatchResult matchResult, List<BetDTO> betList) {
        List<BetDTO> correctBets = betList.filter(betDTO -> betDTO.getBetTyp().match(matchResult));
        List<UserResult> userResultsWithIncorrectBetNotInQueue = getUserResultsWithIncorrectBetNotInQueue(betList, correctBets);
        List<UserResult> userResultsWithCorrectBetsNotInQueue = getUserResultsForBetsNotInQueue(correctBets);

        PriorityQueue<UserResult> results = userResults
                .enqueueAll(userResultsWithCorrectBetsNotInQueue)
                .map(userResult -> addPointsForCorrectBet(correctBets, userResult))
                .enqueueAll(userResultsWithIncorrectBetNotInQueue);
        return new RoundResult(results);
    }


    private UserResult addPointsForCorrectBet(final List<BetDTO> correctBets, final UserResult userResult) {
        return correctBets
                .find(betDTO -> userResult.hasName(betDTO.getUsername()))
                .map(t -> userResult.addPoint())
                .getOrElse(userResult);
    }

    private List<UserResult> getUserResultsWithIncorrectBetNotInQueue(List<BetDTO> bets, List<BetDTO> correctBets) {
        List<BetDTO> betsListWithoutCorrectBets = bets.removeAll(correctBets);
        return getUserResultsForBetsNotInQueue(betsListWithoutCorrectBets);
    }

    private List<UserResult> getUserResultsForBetsNotInQueue(List<BetDTO> betsList) {
        return betsList.filter(this::betNotInResultQueue)
                .map(BetDTO::getUsername)
                .map(this::createNewUserResult);
    }

    private boolean betNotInResultQueue(BetDTO betDTO) {
        return userResults
                .find(userResult -> userResult.hasName(betDTO.getUsername()))
                .isEmpty();
    }

    private UserResult createNewUserResult(String user) {
        LOGGER.log(Level.INFO, "Create new user with name: " + user);
        return UserResult.create(user);
    }

    UserResult getUserResult(String username) {
        return userResults
                .find(userResult -> userResult.hasName(username))
                .getOrElse(UserResult.create(username));
    }

    Set<String> getAllUsers() {
        return userResults
                .map(UserResult::toDTO)
                .map(UserResultDTO::getName)
                .toSet();
    }

    RoundResultsEntity toEntity(UUID leagueUUID, int round) {
        RoundResultsEntity roundResultsEntity = new RoundResultsEntity(leagueUUID, new ArrayList<>(), round);
        java.util.List<UserResultEntity> entities = userResults.toList().map(e -> e.toEntity(roundResultsEntity)).toJavaList();
        roundResultsEntity.setUserResultList(entities);
        return roundResultsEntity;
    }

    List<UserResultDTO> toUserResultDTOList() {
        return userResults
                .map(UserResult::toDTO)
                .toList();
    }

}
