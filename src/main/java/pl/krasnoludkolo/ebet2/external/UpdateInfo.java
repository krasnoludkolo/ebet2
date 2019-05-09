package pl.krasnoludkolo.ebet2.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceClient;

@Getter
@AllArgsConstructor
final class UpdateInfo {
    private LeagueDetails leagueDetails;
    private ExternalSourceClient client;
}
