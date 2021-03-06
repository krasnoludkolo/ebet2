package pl.krasnoludkolo.ebet2.user.api;

import pl.krasnoludkolo.ebet2.infrastructure.error.ResponseError;

public enum UserError implements ResponseError {
    EMPTY_USERNAME_OR_PASSWORD("Empty username or password", 400),
    WRONG_PASSWORD("Wrong password", 400),
    UUID_NOT_FOUND("UUID not found", 404),
    USERNAME_NOT_FOUND("Username not found", 404),
    WRONG_TOKEN("Wrong token", 400),
    DUPLICATED_USERNAME("Duplicated username", 400),
    NOT_REQUIRED_ROLE("Not required role", 403);

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
