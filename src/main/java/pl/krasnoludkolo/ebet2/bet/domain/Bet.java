package pl.krasnoludkolo.ebet2.bet.domain;

import lombok.Getter;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.bet.api.BetTyp;

import java.util.Objects;
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

    void updateBetType(BetTyp betType) {
        this.betTyp = betType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bet bet = (Bet) o;
        return Objects.equals(uuid, bet.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, matchUuid, username, betTyp);
    }
}
