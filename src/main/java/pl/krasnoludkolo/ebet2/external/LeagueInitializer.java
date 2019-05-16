package pl.krasnoludkolo.ebet2.external;

import pl.krasnoludkolo.ebet2.external.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;

import java.util.UUID;

class LeagueInitializer {

    static LeagueDetails initializeLeague(ExternalSourceClient client, ExternalSourceConfiguration config, UUID leagueUUID) {
        String shortcut = client.getShortcut();
        return LeagueDetailsCreator.fromExternalSourceConfiguration(leagueUUID, config, shortcut);
    }

}
