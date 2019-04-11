package pl.krasnoludkolo.ebet2.bet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.krasnoludkolo.ebet2.bet.api.BetTyp;

import java.util.UUID;

@AllArgsConstructor
@Getter
final class NewBet {

    private final BetTyp betTyp;
    private final UUID matchUUID;
    private final String username;

}
