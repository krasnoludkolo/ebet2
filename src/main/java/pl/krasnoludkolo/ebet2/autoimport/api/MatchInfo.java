package pl.krasnoludkolo.ebet2.autoimport.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;

@Getter
@AllArgsConstructor
public class MatchInfo {


    private final String hostName;
    private final String guestName;
    private final int round;
    private boolean finished;
    private MatchResult result;

    public MatchResult getResult() {
        return result;
    }
}
