package pl.krasnoludkolo.ebet2.external.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchResult;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class MatchInfo {


    private final String hostName;
    private final String guestName;
    private final int round;
    private final boolean finished;
    private final MatchResult result;
    private final LocalDateTime matchStartDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchInfo matchInfo = (MatchInfo) o;
        return round == matchInfo.round &&
                finished == matchInfo.finished &&
                Objects.equals(hostName, matchInfo.hostName) &&
                Objects.equals(guestName, matchInfo.guestName) &&
                result == matchInfo.result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostName, guestName, round, finished, result);
    }
}
