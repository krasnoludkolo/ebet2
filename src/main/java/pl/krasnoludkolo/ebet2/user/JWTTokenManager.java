package pl.krasnoludkolo.ebet2.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import io.vavr.control.Option;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

class JWTTokenManager {

    private String secret = UUID.randomUUID().toString();

    private final static Logger LOGGER = Logger.getLogger(JWTTokenManager.class.getName());


    String generateTokenFor(String username) {
        try {
            return JWT.create()
                    .withClaim("username", username)
                    .sign(Algorithm.HMAC256(secret));
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }


    Option<String> getUsername(String token) {
        if (!verify(token)) {
            return Option.none();
        }
        return Option.of(JWT.decode(token).getClaim("username").asString());
    }

    private boolean verify(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                    .build();
            verifier.verify(token);
            return true;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage());
        } catch (JWTVerificationException e1) {
            return false;
        }
    }
}
