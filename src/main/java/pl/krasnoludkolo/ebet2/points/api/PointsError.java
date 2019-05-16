package pl.krasnoludkolo.ebet2.points.api;

import pl.krasnoludkolo.ebet2.infrastructure.ResponseError;

public enum PointsError implements ResponseError {
    MATCH_NOT_FOUND("Match not found", 404),
    LEAGUE_NOT_FOUND("League not found", 404),
    SET_NOT_SET_RESULT("Cannot set NOT_SET result", 400);

    private String message;
    private int httpCode;

    PointsError(String message, int httpCode) {
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
