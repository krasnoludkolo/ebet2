package pl.krasnoludkolo.ebet2.bet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.krasnoludkolo.ebet2.bet.api.BetType;

import java.util.UUID;

@AllArgsConstructor
@Getter
class NewBet {

    private final BetType betType;
    private final UUID matchUUID;
    private final UUID userUUID;

}
