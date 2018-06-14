package pl.krasnoludkolo.ebet2.bet;

import lombok.NoArgsConstructor;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.bet.api.BetTyp;

import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
class Bet {

    private UUID uuid;
    private UUID matchUuid;
    private String username;
    private BetTyp betTyp;

    Bet(BetEntity e) {
        this(e.getUuid(), e.getMatchUuid(), e.getUsername(), e.getBetTyp());
    }

    Bet(UUID matchUuid, String username, BetTyp betTyp) {
        this(UUID.randomUUID(), matchUuid, username, betTyp);
    }

    private Bet(UUID uuid, UUID matchUuid, String username, BetTyp betTyp) {
        this.uuid = uuid;
        this.matchUuid = matchUuid;
        this.username = username;
        this.betTyp = betTyp;
    }

    void updateBetType(BetTyp betType) {
        this.betTyp = betType;
    }

    public boolean hasUsername(String username) {
        return this.username.equals(username);
    }

    public boolean isCorrespondedToMatch(UUID uuid) {
        return Objects.equals(uuid, matchUuid);
    }

    UUID getUuid() {
        return uuid;
    }

    UUID getMatchUuid() {
        return matchUuid;
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

    BetDTO toDto() {
        return new BetDTO(uuid, betTyp, username, matchUuid);
    }

    BetEntity toEntity() {
        return new BetEntity(uuid, matchUuid, username, betTyp);
    }
}
