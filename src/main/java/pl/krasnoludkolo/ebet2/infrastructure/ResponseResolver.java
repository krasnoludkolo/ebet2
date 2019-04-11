package pl.krasnoludkolo.ebet2.infrastructure;

import io.vavr.control.Either;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ResponseResolver {

    private ResponseResolver() {
    }

    public static ResponseEntity resolve(Either<? extends ResponseError, ?> input) {
        if (input.isLeft()) {
            ResponseError error = input.getLeft();
            return new ResponseEntity<>(error.getMessage(), HttpStatus.valueOf(error.getHttpCode()));
        }
        return ResponseEntity.ok(input.get());
    }

}
