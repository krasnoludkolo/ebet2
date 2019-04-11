package pl.krasnoludkolo.ebet2.league.api;

import pl.krasnoludkolo.ebet2.infrastructure.ResponseError;

public enum LeagueError implements ResponseError {
    LEAGUE_NAME_DUPLICATION("League name duplication", 400),
    LEAGUE_NOT_FOUND("League not found", 404),
    MATCH_NOT_FOUND("Match not found", 404),
    WRONG_NAME_EXCEPTION("Wrong name exception", 400),
    SET_SET_MATCH("Set result to match with result", 400),
    SET_NOT_SET_RESULT("Set NOT_SET result", 400),
    EMPTY_OR_NULL_NAME("Name cannot be empty or null", 400);

    private String message;
    private int httpCode;

    LeagueError(String message, int httpCode) {
        this.message = message;
        this.httpCode = httpCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getHttpCode() {
        return httpCode;
    }
}
