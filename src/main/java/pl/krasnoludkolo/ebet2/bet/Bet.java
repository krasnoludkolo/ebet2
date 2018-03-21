package pl.krasnoludkolo.ebet2.bet;

import lombok.NoArgsConstructor;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.bet.api.BetTyp;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;
import java.util.UUID;

@Entity
@NoArgsConstructor
class Bet {

    @Id
    private UUID uuid;
    private UUID matchUuid;
    private String username;
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

    public boolean hasUsername(String username) {
        return this.username.equals(username);
    }

    UUID getUuid() {
        return uuid;
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

    public boolean isCorrespondedToMatch(UUID uuid) {
        return Objects.equals(uuid, matchUuid);
    }
}
