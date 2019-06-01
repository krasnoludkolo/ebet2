package pl.krasnoludkolo.ebet2.league.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchDTO {

    private UUID uuid;
    private LocalDateTime matchStartDate;
    private int round;
    private String host;
    private String guest;
    private MatchResult result;
    private UUID leagueUUID;

    public static MatchDTO fromNewMatchDTO(NewMatchDTO newMatchDTO) {
        return new MatchDTO(
                UUID.randomUUID(),
                newMatchDTO.getMatchStartDate(),
                newMatchDTO.getRound(),
                newMatchDTO.getHost(),
                newMatchDTO.getGuest(),
                MatchResult.NOT_SET,
                newMatchDTO.getLeagueUUID()
        );
    }

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
