package pl.krasnoludkolo.ebet2.results.domain;

import io.vavr.collection.List;
import lombok.Getter;
import pl.krasnoludkolo.ebet2.results.api.LeagueResultsDTO;
import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;

import java.util.Objects;
import java.util.UUID;

@Getter
class LeagueResults {

    private UUID leagueUUID;
    private List<UserResult> userResultList;

    LeagueResults(UUID leagueUUID) {
        this.leagueUUID = leagueUUID;
        this.userResultList = List.empty();
    }

    void addPointToUser(String user) {
        UserResult result = userResultList
                .filter(userResult -> userResult.getName().equals(user))
                .peekOption().getOrElse(() -> new UserResult(user));
        result.addPoint();
        userResultList = userResultList.append(result);
    }

    public LeagueResultsDTO toDTO() {
        List<UserResultDTO> userResultListDTOS = userResultList.map(UserResult::toDTO);
        return new LeagueResultsDTO(userResultListDTOS, leagueUUID);
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
}