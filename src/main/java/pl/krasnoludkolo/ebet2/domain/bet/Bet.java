package pl.krasnoludkolo.ebet2.domain.bet;

import lombok.Getter;
import pl.krasnoludkolo.ebet2.domain.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.domain.bet.api.BetTyp;

import java.util.UUID;

@Getter
class Bet {

    private final UUID uuid;
    private final UUID matchUuid;
    private final String username;
    private BetTyp betTyp;

    Bet(UUID matchUuid, String username, BetTyp betTyp) {
        this.matchUuid = matchUuid;
        this.username = username;
        this.betTyp = betTyp;
        this.uuid = UUID.randomUUID();
    }


    BetDTO toDto() {
        return new BetDTO(uuid, betTyp, username, matchUuid);
    }

    public void updateBetType(BetTyp betType) {
        this.betTyp = betType;
    }
}
