package pl.krasnoludkolo.ebet2.bet.api;

enum BetError {
    BET_NOT_FOUND("Bet not found"), BET_ALREADY_SET("Bet already set"), MATCH_ALREADY_TAKEN("Match already taken");

    private String message;

    BetError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
