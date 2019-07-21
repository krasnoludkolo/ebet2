package pl.krasnoludkolo.ebet2.infrastructure.error;

public interface ResponseError {

    String getMessage();

    int getHttpCode();

}
