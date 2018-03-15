package pl.krasnoludkolo.ebet2.league.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MatchDTO {

    private final UUID uuid;
    private final int round;
    private final String host;
    private final String guest;
    private final MatchResult result;
    private final UUID leagueUUID;

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
