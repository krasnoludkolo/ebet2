package pl.krasnoludkolo.ebet2.user;

import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.user.api.LoginUserInfo;
import pl.krasnoludkolo.ebet2.user.api.UserDetails;
import pl.krasnoludkolo.ebet2.user.api.UserError;
import pl.krasnoludkolo.ebet2.user.api.UserToken;

import java.util.UUID;

public class UserFacade {

    private final UserManager userManager;
    private final JWTTokenManager tokenManager;

    public UserFacade(UserManager userManager, JWTTokenManager tokenManager) {
        this.userManager = userManager;
        this.tokenManager = tokenManager;
    }

    public Either<UserError, UserDetails> registerUser(LoginUserInfo loginUserInfo) {
        return userManager.registerUser(loginUserInfo);
    }

    public Either<UserError, UserToken> generateToken(LoginUserInfo loginUserInfo) {
        boolean correct = userManager.checkPasswordFor(loginUserInfo);
        if (correct) {
            return Either.right(tokenManager.generateTokenFor(loginUserInfo.getUsername()));
        }
        return Either.left(UserError.WRONG_PASSWORD);
    }

    public Either<UserError, String> getUsernameFromToken(String token) {
        return tokenManager
                .getUsername(token)
                .toEither(UserError.WRONG_PASSWORD);
    }

    public Either<UserError, UUID> getUserUUIDFromToken(String token) {
        return userManager.getUUIDFromToken(token);
    }

    public Either<UserError, String> getUsernameFromUUID(UUID uuid) {
        return userManager.getUsernameFromUUID(uuid);
    }
}
