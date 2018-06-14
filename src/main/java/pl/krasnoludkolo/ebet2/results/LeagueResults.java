package pl.krasnoludkolo.ebet2.results;

import io.vavr.collection.List;
import io.vavr.collection.PriorityQueue;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.results.api.LeagueResultsDTO;
import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

class LeagueResults {

    private final static Logger LOGGER = getLogger(LeagueResults.class.getName());

    private final UUID leagueUUID;
    private final PriorityQueue<UserResult> userResultPriorityQueue;

    static LeagueResults create(UUID leagueUUID) {
        return new LeagueResults(leagueUUID, PriorityQueue.empty());
    }

    static LeagueResults fromEntity(LeagueResultsEntity leagueResultsEntity) {
        UUID leagueUUID = leagueResultsEntity.getLeagueUUID();
        List<UserResult> list = List.ofAll(leagueResultsEntity.getUserResultList()).map(UserResult::fromEntity);
        return new LeagueResults(leagueUUID, PriorityQueue.ofAll(list));
    }

    private LeagueResults(UUID leagueUUID, PriorityQueue<UserResult> userResultPriorityQueue) {
        this.leagueUUID = leagueUUID;
        this.userResultPriorityQueue = userResultPriorityQueue;
    }

    LeagueResults updateResults(MatchResult result, List<BetDTO> bets) {

        List<BetDTO> correctBets = bets.filter(betDTO -> betDTO.getBetTyp().match(result));
        List<UserResult> userResultsWithIncorrectBetNotInQueue = getUserResultsWithIncorrectBetNotInQueue(bets, correctBets);
        List<UserResult> userResultsWithCorrectBetsNotInQueue = getUserResultsForBetsNotInQueue(correctBets);

        PriorityQueue<UserResult> results = userResultPriorityQueue
                .enqueueAll(userResultsWithCorrectBetsNotInQueue)
                .map(userResult -> addPointsForCorrectBet(correctBets, userResult))
                .enqueueAll(userResultsWithIncorrectBetNotInQueue);

        return new LeagueResults(leagueUUID, results);
    }

    private UserResult addPointsForCorrectBet(final List<BetDTO> correctBets, final UserResult userResult) {
        return correctBets
                .find(betDTO -> userResult.hasName(betDTO.getUsername()))
                .map(t -> userResult.addPoint())
                .getOrElse(userResult);
    }

    private List<UserResult> getUserResultsWithIncorrectBetNotInQueue(List<BetDTO> bets, List<BetDTO> correctBets) {
        return getUserResultsForBetsNotInQueue(bets.removeAll(correctBets));
    }

    private List<UserResult> getUserResultsForBetsNotInQueue(List<BetDTO> betsList) {
        return betsList.filter(this::betNotInResultQueue).map(BetDTO::getUsername).map(this::createNewUserResult);
    }

    private boolean betNotInResultQueue(BetDTO betDTO) {
        return userResultPriorityQueue.find(userResult -> userResult.hasName(betDTO.getUsername())).isEmpty();
    }

    private UserResult createNewUserResult(String user) {
        LOGGER.log(Level.INFO, "Create new user with name: " + user);
        return UserResult.create(user);
    }

    Option<UserResult> getUserResultForName(String username) {
        return userResultPriorityQueue.find(withName(username));
    }

    private Predicate<UserResult> withName(String user) {
        return userResult -> userResult.hasName(user);
    }

    LeagueResultsDTO toDTO() {
        List<UserResultDTO> userResultListDTOS = userResultPriorityQueue.toList().map(UserResult::toDTO);
        return new LeagueResultsDTO(userResultListDTOS, leagueUUID);
    }

    LeagueResultsEntity toEntity() {
        LeagueResultsEntity entity = new LeagueResultsEntity(leagueUUID, new ArrayList<>());
        List<UserResultEntity> list = userResultPriorityQueue.toList().map(r -> r.toEntity(entity));
        entity.setUserResultList(list.toJavaList());
        return entity;
    }

    UUID getLeagueUUID() {
        return leagueUUID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeagueResults that = (LeagueResults) o;
        return Objects.equals(leagueUUID, that.leagueUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leagueUUID, userResultPriorityQueue);
    }
}