package pl.krasnoludkolo.ebet2.league.api;

import io.vavr.collection.List;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class LeagueDTO {

    private final UUID uuid;
    private final String name;
    private final java.util.List<MatchDTO> matchDTOS;

    public LeagueDTO(UUID uuid, String name, List<MatchDTO> matchDTOS) {
        this.uuid = uuid;
        this.name = name;
        this.matchDTOS = matchDTOS.asJava();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeagueDTO leagueDTO = (LeagueDTO) o;
        return Objects.equals(uuid, leagueDTO.uuid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid, name, matchDTOS);
    }
}
