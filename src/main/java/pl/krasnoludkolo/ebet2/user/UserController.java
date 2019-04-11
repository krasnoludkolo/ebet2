package pl.krasnoludkolo.ebet2.user;

import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.krasnoludkolo.ebet2.infrastructure.ResponseResolver;
import pl.krasnoludkolo.ebet2.user.api.UserError;
import pl.krasnoludkolo.ebet2.user.api.UserInfo;

@RestController
@RequestMapping("api/user")
class UserController {

    private UserFacade userFacade;

    @Autowired
    UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @PostMapping("login")
    public ResponseEntity generateToken(@RequestBody UserInfo userInfo) {
        Either<UserError, String> token = userFacade.generateToken(userInfo);
        return ResponseResolver.resolve(token);
    }

    @PostMapping("register")
    public ResponseEntity registerUser(@RequestBody UserInfo userInfo) {
        Either<UserError, String> token = userFacade.registerUser(userInfo);
        return ResponseResolver.resolve(token);
    }
}
