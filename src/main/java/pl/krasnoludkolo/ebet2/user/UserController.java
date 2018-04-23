package pl.krasnoludkolo.ebet2.user;

import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/user")
class UserController {

    private UserFacade userFacade;

    @Autowired
    UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @PostMapping("login")
    public ResponseEntity<String> generateToken(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        Either<String, String> token = userFacade.generateToken(username, password);
        return token
                .map(s -> new ResponseEntity<>(s, HttpStatus.OK))
                .getOrElse(() -> new ResponseEntity<>(token.getLeft(), HttpStatus.BAD_REQUEST));
    }

    @PostMapping("register")
    public ResponseEntity<String> registerUser(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        Either<String, String> token = userFacade.registerUser(username, password);
        return token
                .map(s -> new ResponseEntity<>(s, HttpStatus.OK))
                .getOrElse(() -> new ResponseEntity<>(token.getLeft(), HttpStatus.BAD_REQUEST));
    }
}
