package pl.krasnoludkolo.ebet2.user;

import io.vavr.control.Either;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.user.api.LoginUserInfo;
import pl.krasnoludkolo.ebet2.user.api.UserError;

import java.util.UUID;

class UserAuthentication {

    private final Repository<UserEntity> repository;
    private final PasswordEncrypt passwordEncrypt;

    UserAuthentication(Repository<UserEntity> repository, PasswordEncrypt passwordEncrypt) {
        this.repository = repository;
        this.passwordEncrypt = passwordEncrypt;
    }


    Either<UserError, UserEntity> login(LoginUserInfo loginUserInfo) {
        String username = loginUserInfo.getUsername();
        String candidatePassword = loginUserInfo.getPassword();
        return findUserByUsername(username)
                .flatMap(user -> checkPasswordFor(candidatePassword, user));
    }


    private Either<UserError, UserEntity> findUserByUsername(String username) {
        return repository
                .findAll()
                .find(userEntity -> userEntity.getUsername().equals(username))
                .toEither(UserError.USERNAME_NOT_FOUND);
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

}
