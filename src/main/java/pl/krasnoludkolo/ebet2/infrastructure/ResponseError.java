package pl.krasnoludkolo.ebet2.infrastructure;

public interface ResponseError {

    String getMessage();

    int getHttpCode();

}
