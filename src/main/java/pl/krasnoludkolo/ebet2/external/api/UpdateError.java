package pl.krasnoludkolo.ebet2.external.api;

import pl.krasnoludkolo.ebet2.infrastructure.ResponseError;

public enum UpdateError implements ResponseError {
    NO_LEAGUE_DETAILS("No league details", 500),
    NO_EXTERNAL_CLIENT("No external client", 500);

    private String message;
    private int httpCode;

    UpdateError(String message, int httpCode) {
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
