package pl.krasnoludkolo.ebet2.user;

import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.user.api.UserError;
import pl.krasnoludkolo.ebet2.user.api.UserInfo;

public class UserFacade {

    private final UserManager userManager;
    private final JWTTokenManager tokenManager;

    public UserFacade(UserManager userManager, JWTTokenManager tokenManager) {
        this.userManager = userManager;
        this.tokenManager = tokenManager;
    }

    public Either<UserError, String> registerUser(UserInfo userInfo) {
        return userManager.registerUser(userInfo);
    }

    public Either<UserError, String> generateToken(UserInfo userInfo) {
        boolean correct = userManager.checkPasswordFor(userInfo);
        if (correct) {
            return Either.right(tokenManager.generateTokenFor(userInfo.getUsername()));
        }
        return Either.left(UserError.WRONG_PASSWORD);
    }

    public Either<String, String> getUsername(String token) {
        return tokenManager
                .getUsername(token)
                .toEither("Wrong token");
    }
}
