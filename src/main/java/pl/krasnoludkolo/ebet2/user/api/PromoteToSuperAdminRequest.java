package pl.krasnoludkolo.ebet2.user.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class PromoteToSuperAdminRequest {

    private final UUID userUUID;
    private final UUID promoterUUID;


}
