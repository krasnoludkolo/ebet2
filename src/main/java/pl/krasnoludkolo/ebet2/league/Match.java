package pl.krasnoludkolo.ebet2.league;

import io.vavr.control.Either;
import lombok.NoArgsConstructor;
import pl.krasnoludkolo.ebet2.league.api.LeagueError;
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

    Either<LeagueError, Match> setMatchResult(MatchResult matchResult) {
        if (matchResult.equals(MatchResult.NOT_SET)) {
            return Either.left(LeagueError.SET_NOT_SET_RESULT);
        }
        result = matchResult;
        return Either.right(this);
    }

    boolean hasRoundNumber(int round) {
        return this.round == round;
    }

    boolean hasAlreadyBegun(LocalDateTime now) {
        return matchHasNoDate() || now.isAfter(matchStartDate);
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
