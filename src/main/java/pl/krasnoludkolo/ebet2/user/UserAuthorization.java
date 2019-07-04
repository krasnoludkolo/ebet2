package pl.krasnoludkolo.ebet2.user;

import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.infrastructure.Success;
import pl.krasnoludkolo.ebet2.user.api.PromoteToSuperAdminRequest;
import pl.krasnoludkolo.ebet2.user.api.UserError;

import java.util.UUID;

final class UserAuthorization {

    private final Repository<UserEntity> repository;

    UserAuthorization(Repository<UserEntity> repository) {
        this.repository = repository;
    }

    Either<UserError, Success> promoteToSuperAdmin(PromoteToSuperAdminRequest request) {
        return canPromote(request)
                .map(repository::findOne)
                .flatMap(user -> user.toEither(UserError.UUID_NOT_FOUND))
                .map(this::setAdminRole)
                .map(this::updateUserEntity);

    }

    private UserEntity setAdminRole(UserEntity u) {
        return new UserEntity(u.getUuid(), u.getUsername(), u.getPassword(), GlobalRole.SUPER_ADMIN);
    }

    private Success updateUserEntity(UserEntity userEntity) {
        repository.update(userEntity.getUuid(), userEntity);
        return new Success();
    }

    private Either<UserError, UUID> canPromote(PromoteToSuperAdminRequest request) {
        return isSuperAdmin(request.getPromoterUUID())
                .flatMap(isAdmin -> isAdmin ? Either.right(request.getUserUUID()) : Either.left(UserError.NOT_REQUIRED_ROLE));
    }

    Either<UserError, Boolean> isSuperAdmin(UUID userUUID) {
        return repository
                .findOne(userUUID)
                .toEither(UserError.UUID_NOT_FOUND)
                .map(this::isSuperAdmin);
    }

    private boolean isSuperAdmin(UserEntity userEntity) {
        return userEntity.getGlobalRole().containsRole(GlobalRole.SUPER_ADMIN);
    }

}
