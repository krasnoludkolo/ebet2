package pl.krasnoludkolo.ebet2.league.api.dto;

import io.vavr.collection.List;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class LeagueDTO {

    private final UUID uuid;
    private final String name;
    private final java.util.List<MatchDTO> matchDTOS;
    private final boolean archived;

    public LeagueDTO(UUID uuid, String name, List<MatchDTO> matchDTOS, boolean archived) {
        this.uuid = uuid;
        this.name = name;
        this.matchDTOS = matchDTOS.asJava();
        this.archived = archived;
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
