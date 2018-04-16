package pl.krasnoludkolo.ebet2.league.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchDTO {

    private UUID uuid;
    private int round;
    private String host;
    private String guest;
    private MatchResult result;
    private UUID leagueUUID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchDTO matchDTO = (MatchDTO) o;
        return uuid == matchDTO.uuid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, round, host, guest, result);
    }
}
