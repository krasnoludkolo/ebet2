package pl.krasnoludkolo.ebet2.bet.api;

import pl.krasnoludkolo.ebet2.league.api.dto.MatchResult;

public enum BetType {

    HOST_WON(MatchResult.HOST_WON), GUEST_WON(MatchResult.GUEST_WON), DRAW(MatchResult.DRAW);

    private MatchResult matchMatchResult;

    BetType(MatchResult matchMatchResult) {
        this.matchMatchResult = matchMatchResult;
    }

    public boolean match(MatchResult matchResult) {
        return matchResult.equals(matchMatchResult);
    }
}
