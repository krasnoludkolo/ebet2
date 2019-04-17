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
    private static final String UUID_CLAIM_NAME = "uuid";


    UserToken generateTokenFor(UUID userUUID) {
        try {
            String sign = JWT.create()
                    .withClaim(UUID_CLAIM_NAME, userUUID.toString())
                    .sign(Algorithm.HMAC256(secret));
            return new UserToken(sign);
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }


    Option<UUID> getUUID(String token) {
        return Option.of(token)
                .filter(this::verify)
                .map(this::decodeUUID);
    }

    private UUID decodeUUID(String t) {
        return UUID.fromString(JWT.decode(t).getClaim(UUID_CLAIM_NAME).asString());
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
