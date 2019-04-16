package pl.krasnoludkolo.ebet2.user;

import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.user.api.LoginUserInfo;
import pl.krasnoludkolo.ebet2.user.api.UserDetails;
import pl.krasnoludkolo.ebet2.user.api.UserError;

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
        String token = tokenManager.generateTokenFor(username).getToken();
        return new UserDetails(uuid, username, token);
    }

    private void saveToRepository(UserEntity u) {
        repository.save(u.getUuid(), u);
    }

    private UserEntity createUserEntity(LoginUserInfo user) {
        String encryptedPassword = encryptPassword(user.getPassword());
        return new UserEntity(UUID.randomUUID(), user.getUsername(), encryptedPassword);
    }

    boolean checkPasswordFor(LoginUserInfo user) {
        return !repository
                .findAll()
                .filter(userEntity -> checkPassword(user, userEntity))
                .isEmpty();
    }

    private boolean checkPassword(LoginUserInfo user, UserEntity userEntity) {
        return checkPassword(user.getPassword(), userEntity.getPassword());
    }

    private String encryptPassword(String password) {
        return passwordEncrypt.encryptPassword(password);
    }

    private boolean checkPassword(String candidate, String password) {
        return passwordEncrypt.checkPassword(candidate, password);
    }

    Either<UserError, String> getUsernameFromUUID(UUID uuid) {
        return repository
                .findOne(uuid)
                .map(UserEntity::getUsername)
                .toEither(UserError.UUID_NOT_FOUND);
    }

    Either<UserError, UUID> getUUIDFromToken(String token) {
        return tokenManager.getUsername(token)
                .flatMap(username -> repository.findAll().find(u -> u.getUsername().equals(username)))
                .map(UserEntity::getUuid)
                .toEither(UserError.WRONG_TOKEN);
    }
}
