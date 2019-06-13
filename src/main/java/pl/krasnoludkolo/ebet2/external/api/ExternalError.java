package pl.krasnoludkolo.ebet2.external.api;

import pl.krasnoludkolo.ebet2.infrastructure.ResponseError;

public enum ExternalError implements ResponseError {
    NO_LEAGUE_DETAILS("No league details", 500),
    ERROR_DURING_DOWNLOADING("Error during downloading", 500),
    LEAGUE_NAME_DUPLICATION("League name duplication", 400),
    NO_EXTERNAL_CLIENT("No external client", 500);

    private String message;
    private int httpCode;

    ExternalError(String message, int httpCode) {
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
