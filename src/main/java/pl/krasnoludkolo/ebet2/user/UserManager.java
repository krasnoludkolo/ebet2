package pl.krasnoludkolo.ebet2.user;

import io.vavr.control.Either;
import io.vavr.control.Option;
import org.apache.commons.codec.binary.Base64;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.user.api.UserError;
import pl.krasnoludkolo.ebet2.user.api.UserInfo;

import java.util.UUID;

class UserManager {

    private final Repository<UserEntity> repository;
    private final JWTTokenManager tokenManager;

    UserManager(Repository<UserEntity> repository, JWTTokenManager tokenManager) {
        this.repository = repository;
        this.tokenManager = tokenManager;
    }

    synchronized Either<UserError, String> registerUser(UserInfo user) {
        Option<UserError> parametersErrors = validateParameters(user);
        if (!parametersErrors.isEmpty()) {
            return Either.left(parametersErrors.get());
        }
        if (userWithUsernameExists(user.getUsername())) {
            return Either.left(UserError.DUPLICATED_USERNAME);
        }
        UserEntity userEntity = createUserEntity(user);
        repository.save(userEntity.getUuid(), userEntity);
        return Either.right(tokenManager.generateTokenFor(user.getUsername()));
    }

    private UserEntity createUserEntity(UserInfo user) {
        byte[] encryptedPassword = encryptPassword(user.getPassword());
        return new UserEntity(UUID.randomUUID(), user.getUsername(), encryptedPassword);
    }

    private boolean userWithUsernameExists(String username) {
        return repository.findAll().find(userEntity -> userEntity.getUsername().equals(username)).isDefined();
    }

    private Option<UserError> validateParameters(UserInfo user) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return Option.of(UserError.EMPTY_USERNAME_OR_PASSWORD);
        }
        return Option.none();
    }

    boolean checkPasswordFor(UserInfo user) {
        return !repository
                .findAll()
                .filter(userEntity -> checkPassword(user, userEntity))
                .isEmpty();
    }

    private boolean checkPassword(UserInfo user, UserEntity userEntity) {
        return userEntity.getUsername().equals(user.getUsername()) && decryptPassword(userEntity.getPassword()).equals(user.getPassword());
    }

    private byte[] encryptPassword(String password) {
        return Base64.encodeBase64(password.getBytes());
    }

    private String decryptPassword(byte[] password) {
        byte[] bytes = Base64.decodeBase64(password);
        return new String(bytes);
    }



}
