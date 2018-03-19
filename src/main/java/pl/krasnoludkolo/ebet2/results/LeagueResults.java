package pl.krasnoludkolo.ebet2.results;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.results.api.LeagueResultsDTO;
import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

class LeagueResults {

    private UUID leagueUUID;
    private List<UserResult> userResultList;

    static LeagueResults create(UUID leagueUUID) {
        return new LeagueResults(leagueUUID, List.empty());
    }

    static LeagueResults fromEntity(LeagueResultsEntity leagueResultsEntity) {
        UUID leagueUUID = leagueResultsEntity.getLeagueUUID();
        List<UserResult> list = List.ofAll(leagueResultsEntity.getUserResultList()).map(UserResult::fromEntity);
        return new LeagueResults(leagueUUID, list);
    }

    private LeagueResults(UUID leagueUUID, List<UserResult> userResultList) {
        this.leagueUUID = leagueUUID;
        this.userResultList = userResultList;
    }

    void addPointToUser(String username) {
        UserResult result = userResultList
                .find(withName(username))
                .getOrElse(createNewUserResult(username));
        result.addPoint();
        userResultList = userResultList.append(result);
    }

    private Supplier<UserResult> createNewUserResult(String user) {
        return () -> UserResult.create(user);
    }

    Option<UserResult> getUserResultForName(String username) {
        return userResultList.find(withName(username));
    }

    private Predicate<UserResult> withName(String user) {
        return userResult -> userResult.hasName(user);
    }

    LeagueResultsDTO toDTO() {
        List<UserResultDTO> userResultListDTOS = userResultList.map(UserResult::toDTO);
        return new LeagueResultsDTO(userResultListDTOS, leagueUUID);
    }

    public UUID getLeagueUUID() {
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
        return Objects.hash(leagueUUID, userResultList);
    }

    public LeagueResultsEntity toEntity() {
        LeagueResultsEntity entity = new LeagueResultsEntity(leagueUUID, new ArrayList<>());
        List<UserResultEntity> list = userResultList.map(r -> r.toEntity(entity));
        entity.setUserResultList(list.toJavaList());
        return entity;
    }
}