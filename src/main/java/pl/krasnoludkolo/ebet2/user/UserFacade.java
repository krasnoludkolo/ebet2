package pl.krasnoludkolo.ebet2.user;

import io.vavr.control.Either;
import io.vavr.control.Option;

public class UserFacade {

    private final UserManager userManager;
    private final JWTTokenManager tokenManager;

    public UserFacade(UserManager userManager, JWTTokenManager tokenManager) {
        this.userManager = userManager;
        this.tokenManager = tokenManager;
    }

    public Either<String, String> registerUser(String username, String password) {
        return userManager.registerUser(username, password);
    }

    public Either<String, String> generateToken(String username, String password) {
        boolean correct = userManager.checkPasswordFor(username, password);
        if (correct) {
            return Either.right(tokenManager.generateTokenFor(username));
        }
        return Either.left("Wrong password");
    }

    public Either<String, String> getUsername(String token) {
        Option<String> username = tokenManager.getUsername(token);
        return username.isDefined() ? Either.right(username.get()) : Either.left("Wrong token");
    }
}