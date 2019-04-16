package pl.krasnoludkolo.ebet2.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import io.vavr.control.Option;
import io.vavr.control.Try;
import pl.krasnoludkolo.ebet2.user.api.UserToken;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

class JWTTokenManager {

    private String secret = UUID.randomUUID().toString();

    private static final Logger LOGGER = Logger.getLogger(JWTTokenManager.class.getName());
    private static final String USERNAME_CLAIM_NAME = "username";


    UserToken generateTokenFor(String username) {
        try {
            String sign = JWT.create()
                    .withClaim(USERNAME_CLAIM_NAME, username)
                    .sign(Algorithm.HMAC256(secret));
            return new UserToken(sign);
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }


    Option<String> getUsername(String token) {
        return Option.of(token)
                .filter(this::verify)
                .map(this::decodeUsername);
    }

    private String decodeUsername(String t) {
        return JWT.decode(t).getClaim(USERNAME_CLAIM_NAME).asString();
    }

    private boolean verify(String token) {
        return Try.of(() -> {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                    .build();
            verifier.verify(token);
            return true;
        }).getOrElse(false);
    }
}
