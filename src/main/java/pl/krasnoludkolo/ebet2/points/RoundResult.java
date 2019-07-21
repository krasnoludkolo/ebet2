package pl.krasnoludkolo.ebet2.points;

import io.vavr.collection.List;
import io.vavr.collection.PriorityQueue;
import io.vavr.collection.Set;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchResult;
import pl.krasnoludkolo.ebet2.points.api.UserResultDTO;

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
        List<BetDTO> correctBets = betList.filter(betDTO -> betDTO.getBetType().match(matchResult));
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
                .find(betDTO -> userResult.hasUUID(betDTO.getUserUUID()))
                .map(t -> userResult.addPoint())
                .getOrElse(userResult);
    }

    private List<UserResult> getUserResultsWithIncorrectBetNotInQueue(List<BetDTO> bets, List<BetDTO> correctBets) {
        List<BetDTO> betsListWithoutCorrectBets = bets.removeAll(correctBets);
        return getUserResultsForBetsNotInQueue(betsListWithoutCorrectBets);
    }

    private List<UserResult> getUserResultsForBetsNotInQueue(List<BetDTO> betsList) {
        return betsList.filter(this::betNotInResultQueue)
                .map(BetDTO::getUserUUID)
                .map(this::createNewUserResult);
    }

    private boolean betNotInResultQueue(BetDTO betDTO) {
        return !userResults
                .exists(userResult -> userResult.hasUUID(betDTO.getUserUUID()));
    }

    private UserResult createNewUserResult(UUID uuid) {
        LOGGER.log(Level.INFO, "Create new user with uuid: " + uuid);
        return UserResult.create(uuid);
    }

    UserResult getUserResult(UUID uuid) {
        return userResults
                .find(userResult -> userResult.hasUUID(uuid))
                .getOrElse(UserResult.create(uuid));
    }

    Set<UUID> getAllUsers() {
        return userResults
                .map(UserResult::toDTO)
                .map(UserResultDTO::getUserUUID)
                .toSet();
    }

    RoundResultsEntity toEntity(int round) {
        RoundResultsEntity roundResultsEntity = new RoundResultsEntity(new ArrayList<>(), round);
        java.util.List<UserResultEntity> entities = userResults.toList().map(UserResult::toEntity).toJavaList();
        roundResultsEntity.setUserResultList(entities);
        return roundResultsEntity;
    }

    List<UserResultDTO> toUserResultDTOList() {
        return userResults
                .map(UserResult::toDTO)
                .toList();
    }

}
