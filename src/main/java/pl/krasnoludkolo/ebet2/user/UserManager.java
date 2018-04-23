package pl.krasnoludkolo.ebet2.user;

import io.vavr.control.Either;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;

import java.util.UUID;

class UserManager {

    private final Repository<UserEntity> repository;
    private final JWTTokenManager tokenManager;

    UserManager(Repository<UserEntity> repository, JWTTokenManager tokenManager) {
        this.repository = repository;
        this.tokenManager = tokenManager;
    }

    Either<String, String> registerUser(String username, String password) {
        Option<String> parametersErrors = validateParameters(username, password);
        if (!parametersErrors.isEmpty()) {
            return Either.left(parametersErrors.get());
        }
        if (userWithUsernameExists(username)) {
            return Either.left("Duplicate username");
        }
        UserEntity userEntity = createUserEntity(username, password);
        repository.save(userEntity.getUuid(), userEntity);
        return Either.right(tokenManager.generateTokenFor(username));
    }

    //TODO encrypt password
    private UserEntity createUserEntity(String username, String password) {
        return new UserEntity(UUID.randomUUID(), username, password);
    }

    private boolean userWithUsernameExists(String username) {
        return repository.findAll().find(userEntity -> userEntity.getUsername().equals(username)).isDefined();
    }

    private Option<String> validateParameters(String username, String password) {
        if (username == null || username.isEmpty()) {
            return Option.of("Empty username");
        } else if (password == null || password.isEmpty()) {
            return Option.of("Empty password");
        }
        return Option.none();
    }

    //TODO encrypt password
    public boolean checkPasswordFor(String username, String password) {
        return !repository
                .findAll()
                .filter(userEntity -> userEntity.getUsername().equals(username) && userEntity.getPassword().equals(password))
                .isEmpty();
    }
}
