package pl.krasnoludkolo.ebet2.user;

import io.vavr.collection.Stream;
import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.user.api.UserError;
import pl.krasnoludkolo.ebet2.user.api.UserInfo;

final class NewUserValidator {

    private Repository<UserEntity> repository;

    NewUserValidator(Repository<UserEntity> repository) {
        this.repository = repository;
    }

    Either<UserError, UserInfo> validate(UserInfo user) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (isNullOrEmpty(username, password)) {
            return Either.left(UserError.EMPTY_USERNAME_OR_PASSWORD);
        }
        return userWithUsernameExists(username) ? Either.left(UserError.DUPLICATED_USERNAME) : Either.right(user);
    }

    private boolean isNullOrEmpty(String... args) {
        return Stream.of(args)
                .map(this::isNullOrEmpty)
                .fold(false, (x, y) -> x || y);
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    private boolean userWithUsernameExists(String username) {
        return repository.findAll().find(userEntity -> userEntity.getUsername().equals(username)).isDefined();
    }

}
