package pl.krasnoludkolo.ebet2.infrastructure.error;

import io.vavr.control.Either;
import io.vavr.control.Option;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ResponseResolver {

    private ResponseResolver() {
    }

    public static ResponseEntity resolve(Either<? extends ResponseError, ?> input) {
        return input
                .map(ResponseEntity::ok)
                .getOrElseGet(ResponseResolver::createErrorResponse);
    }

    private static ResponseEntity createErrorResponse(ResponseError error) {
        ErrorResponse response = new ErrorResponse(error.getMessage());
        int httpCode = error.getHttpCode();
        return new ResponseEntity<>(response, HttpStatus.valueOf(httpCode));
    }

    public static <T> ResponseEntity<T> resolve(Option<T> input) {
        return input
                .map(x -> new ResponseEntity<>(x, HttpStatus.OK))
                .getOrElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
