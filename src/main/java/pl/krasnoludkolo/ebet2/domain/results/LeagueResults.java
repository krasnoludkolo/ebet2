package pl.krasnoludkolo.ebet2.domain.results;

import io.vavr.collection.List;
import lombok.Getter;
import pl.krasnoludkolo.ebet2.domain.results.api.LeagueResultsDTO;
import pl.krasnoludkolo.ebet2.domain.results.api.UserResultDTO;

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
}