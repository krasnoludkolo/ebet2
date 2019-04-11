package pl.krasnoludkolo.ebet2.bet.api;

public enum BetError {
    BET_NOT_FOUND("Bet not found", 404),
    BET_ALREADY_SET("Bet already set", 400),
    MATCH_ALREADY_STARTED("Match already started", 400),
    MATCH_NOT_FOUND("Match not found", 404),
    USER_NOT_FOUND("User not found", 400);

    private String message;
    private int code;

    BetError(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public int getHttpCode() {
        return code;
    }
}
