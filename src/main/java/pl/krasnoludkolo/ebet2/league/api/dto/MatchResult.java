package pl.krasnoludkolo.ebet2.league.api.dto;

public enum MatchResult {

    NOT_SET, HOST_WON, GUEST_WON, DRAW;

    public static MatchResult fromResult(int hostGoals, int guestGoals) {
        if (hostGoals > guestGoals) {
            return MatchResult.HOST_WON;
        } else if (hostGoals < guestGoals) {
            return MatchResult.GUEST_WON;
        } else {
            return MatchResult.DRAW;
        }
    }
}
