package pl.krasnoludkolo.ebet2.bet.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class NewBet {

    private final BetTyp betTyp;
    private final UUID matchUUID;
    private final UUID userUUID;

}
