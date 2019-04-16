package pl.krasnoludkolo.ebet2.user.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserDetails {

    private UUID userUUID;
    private String username;
    private String token;

}
