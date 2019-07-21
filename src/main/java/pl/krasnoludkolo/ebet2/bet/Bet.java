package pl.krasnoludkolo.ebet2.bet;

import lombok.NoArgsConstructor;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.bet.api.BetType;

import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
class Bet {

    private UUID uuid;
    private UUID matchUuid;
    private UUID userUUUID;
    private BetType betType;

    static Bet of(NewBet newBet) {
        return new Bet(UUID.randomUUID(), newBet.getMatchUUID(), newBet.getUserUUID(), newBet.getBetType());
    }

    static Bet fromEntity(BetEntity e) {
        return new Bet(e.getUuid(), e.getMatchUuid(), e.getUserUUID(), e.getBetType());
    }

    private Bet(UUID uuid, UUID matchUuid, UUID userUUUID, BetType betType) {
        this.uuid = uuid;
        this.matchUuid = matchUuid;
        this.userUUUID = userUUUID;
        this.betType = betType;
    }

    void updateBetType(BetType betType) {
        this.betType = betType;
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
        return Objects.hash(uuid, matchUuid, userUUUID, betType);
    }

    BetDTO toDto() {
        return new BetDTO(uuid, betType, userUUUID, matchUuid);
    }

    BetEntity toEntity() {
        return new BetEntity(uuid, matchUuid, userUUUID, betType);
    }
}
