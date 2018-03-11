package pl.krasnoludkolo.ebet2.domain.results.api;

import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class LeagueResultsDTO {

    private final List<UserResultDTO> userResultDTOS;
    private final UUID LeagueUuid;

    public LeagueResultsDTO(io.vavr.collection.List<UserResultDTO> userResultDTOS, UUID leagueUuid) {
        this.userResultDTOS = userResultDTOS.asJava();
        LeagueUuid = leagueUuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeagueResultsDTO that = (LeagueResultsDTO) o;
        return Objects.equals(userResultDTOS, that.userResultDTOS) &&
                Objects.equals(LeagueUuid, that.LeagueUuid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userResultDTOS, LeagueUuid);
    }
}
