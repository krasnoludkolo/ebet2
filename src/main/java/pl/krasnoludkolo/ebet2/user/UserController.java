package pl.krasnoludkolo.ebet2.user;

import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.krasnoludkolo.ebet2.infrastructure.error.ResponseResolver;
import pl.krasnoludkolo.ebet2.user.api.LoginUserInfo;
import pl.krasnoludkolo.ebet2.user.api.UserDetails;
import pl.krasnoludkolo.ebet2.user.api.UserError;
import pl.krasnoludkolo.ebet2.user.api.UserToken;

@RestController
@RequestMapping("api/user")
class UserController {

    private UserFacade userFacade;

    @Autowired
    UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @PostMapping("login")
    public ResponseEntity generateToken(@RequestBody LoginUserInfo loginUserInfo) {
        Either<UserError, UserToken> userDetails = userFacade.login(loginUserInfo);
        return ResponseResolver.resolve(userDetails);
    }

    @PostMapping("register")
    public ResponseEntity registerUser(@RequestBody LoginUserInfo loginUserInfo) {
        Either<UserError, UserDetails> userDetails = userFacade.registerUser(loginUserInfo);
        return ResponseResolver.resolve(userDetails);
    }
}
