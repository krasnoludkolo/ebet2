package pl.krasnoludkolo.ebet2.domain.bet.api;

import pl.krasnoludkolo.ebet2.domain.league.api.MatchResult;

public enum BetTyp {

    HOST_WON(MatchResult.HOST_WON), GUEST_WON(MatchResult.GUEST_WON), DRAW(MatchResult.DRAW);

    private MatchResult matchMatchResult;

    BetTyp(MatchResult matchMatchResult) {
        this.matchMatchResult = matchMatchResult;
    }

    public boolean match(MatchResult matchResult) {
        return matchResult.equals(matchMatchResult);
    }
}
