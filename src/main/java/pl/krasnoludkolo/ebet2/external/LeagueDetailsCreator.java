package pl.krasnoludkolo.ebet2.external;

import io.vavr.Tuple;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;

import java.util.List;
import java.util.Map;
import java.util.UUID;

class LeagueDetailsCreator {


    static LeagueDetails fromExternalSourceConfiguration(UUID uuid, ExternalSourceConfiguration config, String shortcut) {
        List<LeagueDetailsSetting> configuration = config.getAllSettings().map(t -> new LeagueDetailsSetting(UUID.randomUUID(), t._1, t._2)).asJava();
        return new LeagueDetails(uuid, configuration, shortcut);
    }

    static ExternalSourceConfiguration toExternalSourceConfiguration(LeagueDetails leagueDetails) {
        Map<String, String> config = io.vavr.collection.List
                .ofAll(leagueDetails.getConfig())
                .toMap(details -> Tuple.of(details.getName(), details.getValue()))
                .toJavaMap();
        return new ExternalSourceConfiguration(config);
    }

}
