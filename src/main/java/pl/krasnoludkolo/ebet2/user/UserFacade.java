package pl.krasnoludkolo.ebet2.user;

import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.infrastructure.Success;
import pl.krasnoludkolo.ebet2.user.api.LoginUserInfo;
import pl.krasnoludkolo.ebet2.user.api.PromoteToSuperAdminRequest;
import pl.krasnoludkolo.ebet2.user.api.UserDetails;
import pl.krasnoludkolo.ebet2.user.api.UserError;

import java.util.UUID;

public class UserFacade {

    private final UserAuthentication userAuthentication;
    private final UserAuthorization userAuthorization;
    private final JWTTokenManager tokenManager;
    private final UserRegistration userRegistration;

    public UserFacade(UserAuthentication userAuthentication, UserAuthorization userAuthorization, JWTTokenManager tokenManager, UserRegistration userRegistration) {
        this.userAuthentication = userAuthentication;
        this.userAuthorization = userAuthorization;
        this.tokenManager = tokenManager;
        this.userRegistration = userRegistration;
    }

    public Either<UserError, UserDetails> registerUser(LoginUserInfo loginUserInfo) {
        return userRegistration
                .registerUser(loginUserInfo)
                .map(tokenManager::generateUserDetails);
    }

    public Either<UserError, UserDetails> registerSuperAdmin(LoginUserInfo loginUserInfo) {
        return userRegistration
                .registerSuperAdmin(loginUserInfo)
                .map(tokenManager::generateUserDetails);
    }

    public Either<UserError, UserDetails> login(LoginUserInfo loginUserInfo) {
        return userAuthentication
                .login(loginUserInfo)
                .map(tokenManager::generateUserDetails);
    }

    public Either<UserError, UUID> getUserUUIDFromToken(String token) {
        return tokenManager.getUUIDFromToken(token);
    }

    public Either<UserError, String> getUsernameFromUUID(UUID uuid) {
        return userAuthentication.getUsernameFromUUID(uuid);
    }

    public Either<UserError, Success> promoteToSuperAdmin(PromoteToSuperAdminRequest request) {
        return userAuthorization.promoteToSuperAdmin(request);
    }

    public Either<UserError, Boolean> isSuperAdmin(UUID userUUID) {
        return userAuthorization.isSuperAdmin(userUUID);
    }

}
