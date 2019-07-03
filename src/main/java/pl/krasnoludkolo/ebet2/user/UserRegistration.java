package pl.krasnoludkolo.ebet2.user;

import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.user.api.LoginUserInfo;
import pl.krasnoludkolo.ebet2.user.api.UserError;

import java.util.UUID;

final class UserRegistration {

    private final Repository<UserEntity> repository;
    private final NewUserValidator userValidator;
    private final PasswordEncrypt passwordEncrypt;

    UserRegistration(Repository<UserEntity> repository, PasswordEncrypt passwordEncrypt) {
        this.repository = repository;
        this.userValidator = new NewUserValidator(repository);
        this.passwordEncrypt = passwordEncrypt;
    }


    synchronized Either<UserError, UserEntity> registerUser(LoginUserInfo user) {
        return userValidator
                .validate(user)
                .map(this::createNormalUserEntity)
                .map(this::saveToRepository);
    }

    synchronized Either<UserError, UserEntity> registerSuperAdmin(LoginUserInfo user) {
        return userValidator
                .validate(user)
                .map(this::createSuperAdminUserEntity)
                .map(this::saveToRepository);
    }

    private UserEntity createNormalUserEntity(LoginUserInfo loginUserInfo) {
        return createUserEntity(loginUserInfo, GlobalRole.USER);
    }

    private UserEntity createSuperAdminUserEntity(LoginUserInfo loginUserInfo) {
        return createUserEntity(loginUserInfo, GlobalRole.SUPER_ADMIN);
    }

    private UserEntity createUserEntity(LoginUserInfo user, GlobalRole globalRole) {
        String encryptedPassword = encryptPassword(user.getPassword());
        return new UserEntity(UUID.randomUUID(), user.getUsername(), encryptedPassword, globalRole);
    }

    private UserEntity saveToRepository(UserEntity u) {
        repository.save(u.getUuid(), u);
        return u;
    }

    private String encryptPassword(String password) {
        return passwordEncrypt.encryptPassword(password);
    }

}
