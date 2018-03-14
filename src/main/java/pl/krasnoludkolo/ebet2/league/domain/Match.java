package pl.krasnoludkolo.ebet2.league.domain;

import lombok.Getter;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;

import java.util.Objects;
import java.util.UUID;

@Getter
class Match {

    private UUID uuid;
    private int round;
    private String host;
    private String guest;
    private MatchResult result;
    private UUID leagueUUID;


    Match(NewMatchDTO dto) {
        this(dto.getRound(), dto.getHost(), dto.getGuest(), dto.getLeagueUUID());
    }

    private Match(int round, String host, String guest, UUID leagueUUID) {
        this(round, host, guest, MatchResult.NOT_SET, leagueUUID);
    }

    private Match(int round, String host, String guest, MatchResult result, UUID leagueUUID) {
        this.round = round;
        this.host = host;
        this.guest = guest;
        this.result = result;
        this.uuid = UUID.randomUUID();
        this.leagueUUID = leagueUUID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return round == match.round &&
                Objects.equals(host, match.host) &&
                Objects.equals(guest, match.guest) &&
                result == match.result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(round, host, guest, result);
    }

    MatchDTO toDTO() {
        return new MatchDTO(uuid, round, host, guest, result, leagueUUID);
    }

    public void setMatchResult(MatchResult matchResult) {
        result = matchResult;
    }
}
