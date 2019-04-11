package pl.krasnoludkolo.ebet2.user.api;

import pl.krasnoludkolo.ebet2.infrastructure.ResponseError;

public enum UserError implements ResponseError {
    EMPTY_USERNAME_OR_PASSWORD("Empty username or password", 400),
    WRONG_PASSWORD("Wrong password", 400),
    DUPLICATED_USERNAME("Duplicated username", 400);

    private int httpCode;
    private String message;

    UserError(String message, int httpCode) {
        this.httpCode = httpCode;
        this.message = message;
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
