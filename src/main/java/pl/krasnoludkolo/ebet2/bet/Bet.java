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
    private UUID userUUUID;
    private BetTyp betTyp;

    static Bet of(NewBet newBet) {
        return new Bet(UUID.randomUUID(), newBet.getMatchUUID(), newBet.getUserUUID(), newBet.getBetTyp());
    }

    static Bet fromEntity(BetEntity e) {
        return new Bet(e.getUuid(), e.getMatchUuid(), e.getUserUUID(), e.getBetTyp());
    }

    private Bet(UUID uuid, UUID matchUuid, UUID userUUUID, BetTyp betTyp) {
        this.uuid = uuid;
        this.matchUuid = matchUuid;
        this.userUUUID = userUUUID;
        this.betTyp = betTyp;
    }

    void updateBetType(BetTyp betType) {
        this.betTyp = betType;
    }

    boolean hasUserUUID(UUID userUUID) {
        return this.userUUUID.equals(userUUID);
    }

    boolean isCorrespondedToMatch(UUID uuid) {
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
        return Objects.hash(uuid, matchUuid, userUUUID, betTyp);
    }

    BetDTO toDto() {
        return new BetDTO(uuid, betTyp, userUUUID, matchUuid);
    }

    BetEntity toEntity() {
        return new BetEntity(uuid, matchUuid, userUUUID, betTyp);
    }
}
