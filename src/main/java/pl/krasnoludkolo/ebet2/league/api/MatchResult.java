package pl.krasnoludkolo.ebet2.league.api;

public enum MatchResult {

    NOT_SET, HOST_WON, GUEST_WON, DRAW;

    public static MatchResult fromResult(int hostGloas, int guestGoals) {
        if (hostGloas > guestGoals) {
            return MatchResult.HOST_WON;
        } else if (hostGloas < guestGoals) {
            return MatchResult.GUEST_WON;
        } else {
            return MatchResult.DRAW;
        }
    }
}
