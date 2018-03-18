package pl.krasnoludkolo.ebet2.league;

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
    private League league;


    static Match fromEntity(MatchEntity entity) {
        return new Match(entity.getUuid(), entity.getRound(), entity.getHost(), entity.getGuest(), entity.getResult(), League.fromEntity(entity.getLeague()));
    }

    static Match fromEntity(MatchEntity entity, League league) {
        return new Match(entity.getUuid(), entity.getRound(), entity.getHost(), entity.getGuest(), entity.getResult(), league);
    }

    static Match fromDTO(NewMatchDTO dto, League league) {
        return new Match(dto.getRound(), dto.getHost(), dto.getGuest(), league);
    }

    private Match(int round, String host, String guest, League league) {
        this(UUID.randomUUID(), round, host, guest, MatchResult.NOT_SET, league);
    }

    private Match(UUID uuid, int round, String host, String guest, MatchResult result, League league) {
        this.uuid = uuid;
        this.round = round;
        this.host = host;
        this.guest = guest;
        this.result = result;
        this.league = league;
    }

    void setMatchResult(MatchResult matchResult) {
        if (matchResult.equals(MatchResult.NOT_SET)) {
            throw new IllegalArgumentException("Cannot set NOT_SET match result");
        }
        result = matchResult;
    }

    MatchDTO toDTO() {
        return new MatchDTO(uuid, round, host, guest, result, league.getUuid());
    }

    MatchEntity toEntity() {
        return new MatchEntity(uuid, round, host, guest, result, league.toEntity());
    }

    MatchEntity toEntity(LeagueEntity entity) {
        return new MatchEntity(uuid, round, host, guest, result, entity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return Objects.equals(uuid, match.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, round, host, guest, result, league);
    }
}
