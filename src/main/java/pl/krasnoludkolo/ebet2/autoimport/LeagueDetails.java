package pl.krasnoludkolo.ebet2.autoimport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
class LeagueDetails {

    @Id
    private UUID leagueUUID;
    private ExternalSourceConfiguration configuration;
    private int lastResolvedRound;

    void incrementLastResolvedRound() {
        lastResolvedRound++;
    }

    int getNextRoundToResolveNumber() {
        return lastResolvedRound + 1;
    }

}
