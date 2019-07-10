package pl.krasnoludkolo.ebet2.external;

import io.vavr.collection.List;
import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.external.api.ExternalError;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchResult;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ExternalClientMock implements ExternalSourceClient {

    private static LocalDateTime nextYear = LocalDateTime.now().plus(1, ChronoUnit.YEARS);

    private boolean error = false;

    public static final List<MatchInfo> SOME_MATCHES = List.of(

            new MatchInfo("a", "b", 1, true, MatchResult.HOST_WON, nextYear),
            new MatchInfo("c", "d", 1, true, MatchResult.GUEST_WON, nextYear),
            new MatchInfo("a", "c", 2, false, MatchResult.NOT_SET, nextYear),
            new MatchInfo("b", "d", 2, false, MatchResult.NOT_SET, nextYear),
            new MatchInfo("a", "d", 3, false, MatchResult.NOT_SET, nextYear),
            new MatchInfo("c", "b", 3, false, MatchResult.NOT_SET, nextYear)
    );

    private List<MatchInfo> matchList;

    public ExternalClientMock(List<MatchInfo> matchList) {
        this.matchList = matchList;
    }

    @Override
    public Either<ExternalError, List<MatchInfo>> downloadAllRounds(ExternalSourceConfiguration config) {
        return error ? Either.left(ExternalError.ERROR_DURING_DOWNLOADING) : Either.right(matchList);
    }

    @Override
    public String getShortcut() {
        return "Mock";
    }

    public List<MatchInfo> getMatchList() {
        return matchList;
    }

    public void setMatchList(List<MatchInfo> matchList) {
        this.matchList = matchList;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
