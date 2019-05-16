package pl.krasnoludkolo.ebet2.user.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserPublicDetails {

    private UUID userUUID;
    private String username;

}
