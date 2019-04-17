package pl.krasnoludkolo.ebet2.user;

import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.user.api.LoginUserInfo;
import pl.krasnoludkolo.ebet2.user.api.UserDetails;
import pl.krasnoludkolo.ebet2.user.api.UserError;
import pl.krasnoludkolo.ebet2.user.api.UserToken;

import java.util.UUID;

public class UserFacade {

    private final UserManager userManager;

    public UserFacade(UserManager userManager) {
        this.userManager = userManager;
    }

    public Either<UserError, UserDetails> registerUser(LoginUserInfo loginUserInfo) {
        return userManager.registerUser(loginUserInfo);
    }

    public Either<UserError, UserToken> login(LoginUserInfo loginUserInfo) {
        return userManager.login(loginUserInfo);
    }

    public Either<UserError, UUID> getUserUUIDFromToken(String token) {
        return userManager.getUUIDFromToken(token);
    }

    public Either<UserError, String> getUsernameFromUUID(UUID uuid) {
        return userManager.getUsernameFromUUID(uuid);
    }
}
