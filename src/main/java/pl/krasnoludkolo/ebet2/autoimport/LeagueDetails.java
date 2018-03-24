package pl.krasnoludkolo.ebet2.autoimport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
class LeagueDetails {

    @Id
    private UUID leagueUUID;
    private long externalID;
    private int lastResolvedRound;

    void incrementLastResolvedRound() {
        lastResolvedRound++;
    }

    int getNextRoundToResolveNumber() {
        return lastResolvedRound + 1;
    }

}
