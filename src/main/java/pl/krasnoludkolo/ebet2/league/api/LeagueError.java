package pl.krasnoludkolo.ebet2.league.api;

public enum LeagueError {
    LEAGUE_NAME_DUPLICATION("League name duplication", 400),
    LEAGUE_NOT_FOUND("League not found", 404),
    MATCH_NOT_FOUND("Match not found", 404),
    WRONG_NAME_EXCEPTION("Wrong name exception", 400),
    SET_SET_MATCH("Set result to match with result", 400),
    SET_NOT_SET_RESULT("Set NOT_SET result", 400);

    private String message;
    private int httpCode;

    LeagueError(String message, int httpCode) {
        this.message = message;
        this.httpCode = httpCode;
    }
}
