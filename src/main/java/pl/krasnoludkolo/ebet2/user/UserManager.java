package pl.krasnoludkolo.ebet2.user;

import io.vavr.control.Either;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.user.api.LoginUserInfo;
import pl.krasnoludkolo.ebet2.user.api.UserDetails;
import pl.krasnoludkolo.ebet2.user.api.UserError;
import pl.krasnoludkolo.ebet2.user.api.UserToken;

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

    synchronized Either<UserError, UserDetails> registerUser(LoginUserInfo user) {
        return userValidator
                .validate(user)
                .map(this::createUserEntity)
                .peek(this::saveToRepository)
                .map(this::generateUserDetails);
    }

    private UserDetails generateUserDetails(UserEntity userEntity) {
        UUID uuid = userEntity.getUuid();
        String username = userEntity.getUsername();
        String token = tokenManager.generateTokenFor(uuid).getToken();
        return new UserDetails(uuid, username, token);
    }

    private void saveToRepository(UserEntity u) {
        repository.save(u.getUuid(), u);
    }

    private UserEntity createUserEntity(LoginUserInfo user) {
        String encryptedPassword = encryptPassword(user.getPassword());
        return new UserEntity(UUID.randomUUID(), user.getUsername(), encryptedPassword);
    }

    private String encryptPassword(String password) {
        return passwordEncrypt.encryptPassword(password);
    }

    Either<UserError, UserToken> login(LoginUserInfo loginUserInfo) {
        String username = loginUserInfo.getUsername();
        String candidatePassword = loginUserInfo.getPassword();
        return findUserByUsername(username)
                .toEither(UserError.USERNAME_NOT_FOUND)
                .flatMap(user -> checkPasswordFor(candidatePassword, user))
                .map(UserEntity::getUuid)
                .map(tokenManager::generateTokenFor);
    }

    private Option<UserEntity> findUserByUsername(String username) {
        return repository
                .findAll()
                .find(userEntity -> userEntity.getUsername().equals(username));
    }

    private Either<UserError, UserEntity> checkPasswordFor(String candidate, UserEntity user) {
        String password = user.getPassword();
        return Option.of(user)
                .filter(u -> passwordEncrypt.checkPassword(candidate, password))
                .toEither(UserError.WRONG_PASSWORD);
    }

    Either<UserError, String> getUsernameFromUUID(UUID uuid) {
        return repository
                .findOne(uuid)
                .map(UserEntity::getUsername)
                .toEither(UserError.UUID_NOT_FOUND);
    }

    Either<UserError, UUID> getUUIDFromToken(String token) {
        return tokenManager.getUUID(token).toEither(UserError.WRONG_TOKEN);
    }
}
