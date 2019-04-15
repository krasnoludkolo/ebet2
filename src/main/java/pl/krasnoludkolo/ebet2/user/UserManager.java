package pl.krasnoludkolo.ebet2.user;

import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.user.api.UserError;
import pl.krasnoludkolo.ebet2.user.api.UserInfo;

import java.util.UUID;

class UserManager {

    private final Repository<UserEntity> repository;
    private final JWTTokenManager tokenManager;
    private final NewUserValidator userValidator;
    private final PasswordEncrypt passwordEncrypt;

    UserManager(Repository<UserEntity> repository, JWTTokenManager tokenManager, PasswordEncrypt passwordEncrypt) {
        this.repository = repository;
        this.tokenManager = tokenManager;
        this.userValidator = new NewUserValidator(repository);
        this.passwordEncrypt = passwordEncrypt;
    }

    synchronized Either<UserError, String> registerUser(UserInfo user) {
        return userValidator
                .validate(user)
                .map(this::createUserEntity)
                .peek(u -> repository.save(u.getUuid(), u))
                .map(UserEntity::getUsername)
                .map(tokenManager::generateTokenFor);
    }

    private UserEntity createUserEntity(UserInfo user) {
        String encryptedPassword = encryptPassword(user.getPassword());
        return new UserEntity(UUID.randomUUID(), user.getUsername(), encryptedPassword);
    }

    boolean checkPasswordFor(UserInfo user) {
        return !repository
                .findAll()
                .filter(userEntity -> checkPassword(user, userEntity))
                .isEmpty();
    }

    private boolean checkPassword(UserInfo user, UserEntity userEntity) {
        return checkPassword(user.getPassword(), userEntity.getPassword());
    }

    private String encryptPassword(String password) {
        return passwordEncrypt.encryptPassword(password);
    }

    private boolean checkPassword(String candidate, String password) {
        return passwordEncrypt.checkPassword(candidate, password);
    }



}
