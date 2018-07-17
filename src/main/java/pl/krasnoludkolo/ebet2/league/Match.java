package pl.krasnoludkolo.ebet2.league;

import lombok.NoArgsConstructor;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@NoArgsConstructor
class Match {

    @Id
    private UUID uuid;
    private int round;
    private String host;
    private String guest;
    private MatchResult result;
    @ManyToOne
    private League league;
    private LocalDateTime matchStartDate;


    static Match fromDTO(NewMatchDTO dto, League league) {
        return new Match(dto.getRound(), dto.getHost(), dto.getGuest(), league, dto.getMatchStartDate());
    }

    private Match(int round, String host, String guest, League league, LocalDateTime startDate) {
        this(UUID.randomUUID(), round, host, guest, MatchResult.NOT_SET, league, startDate);
    }

    private Match(UUID uuid, int round, String host, String guest, MatchResult result, League league, LocalDateTime startDate) {
        this.uuid = uuid;
        this.round = round;
        this.host = host;
        this.guest = guest;
        this.result = result;
        this.league = league;
        this.matchStartDate = startDate;
    }

    void setMatchResult(MatchResult matchResult) {
        if (matchResult.equals(MatchResult.NOT_SET)) {
            throw new IllegalArgumentException("Cannot set NOT_SET match result");
        }
        result = matchResult;
    }

    boolean hasRoundNumber(int round) {
        return this.round == round;
    }

    boolean hasAlreadyBegun(LocalDateTime now) {
        if (matchHasNoDate()) {
            return true;
        }
        return now.isAfter(matchStartDate);
    }

    private boolean matchHasNoDate() {
        return matchStartDate == null;
    }

    MatchDTO toDTO() {
        return new MatchDTO(uuid, matchStartDate, round, host, guest, result, league.getUuid());
    }

    UUID getUuid() {
        return uuid;
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
