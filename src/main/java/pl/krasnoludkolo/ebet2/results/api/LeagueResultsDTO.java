package pl.krasnoludkolo.ebet2.results.api;

import io.vavr.collection.List;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class LeagueResultsDTO {

    private final java.util.List<UserResultDTO> generalResult;
    private final java.util.List<java.util.List<UserResultDTO>> roundResults;
    private final UUID LeagueUuid;

    public LeagueResultsDTO(List<UserResultDTO> generalResult, List<List<UserResultDTO>> roundsResults, UUID leagueUuid) {
        this.generalResult = generalResult.asJava();
        this.roundResults = roundsResults.map(List::asJava).asJava();
        LeagueUuid = leagueUuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeagueResultsDTO that = (LeagueResultsDTO) o;
        return Objects.equals(generalResult, that.generalResult) &&
                Objects.equals(LeagueUuid, that.LeagueUuid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(generalResult, LeagueUuid);
    }
}
