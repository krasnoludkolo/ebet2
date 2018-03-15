package pl.krasnoludkolo.ebet2.bet.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class NewBetDTO {

    private final BetTyp betTyp;
    private final String username;
    private final UUID matchUUID;

}
