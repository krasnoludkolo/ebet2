package pl.krasnoludkolo.ebet2.domain.bet.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class BetDTO {


    private final UUID uuid;
    private final BetTyp betTyp;
    private final String username;
    private final UUID matchUUID;

}
