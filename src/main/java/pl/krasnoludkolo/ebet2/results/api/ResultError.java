package pl.krasnoludkolo.ebet2.results.api;

import pl.krasnoludkolo.ebet2.infrastructure.error.ResponseError;

public enum ResultError implements ResponseError {
    LEAGUE_NAME_DUPLICATION("League name duplication", 400),
    DOWNLOAD_ERROR("Error during data import", 500),
    NO_EXTERNAL_CLIENT("No external client", 400);

    private String message;
    private int httpCode;

    ResultError(String message, int httpCode) {
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
