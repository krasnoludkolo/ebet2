package pl.krasnoludkolo.ebet2.external;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;

import java.util.UUID;

class LeagueDetailsCreator {


    static LeagueDetails fromExternalSourceConfiguration(UUID uuid, ExternalSourceConfiguration config, String shortcut) {
        List<LeagueDetailsSetting> configuration = config.getAllSettings().map(t -> new LeagueDetailsSetting(UUID.randomUUID(), t._1, t._2));
        return new LeagueDetails(uuid, configuration.asJava(), shortcut);
    }

    static ExternalSourceConfiguration toExternalSourceConfiguration(LeagueDetails leagueDetails) {
        List<Tuple2<String, String>> config = List
                .ofAll(leagueDetails.getConfig())
                .map(details -> Tuple.of(details.getName(), details.getValue()));
        String clientShortcut = leagueDetails.getClientShortcut();
        return ExternalSourceConfiguration.fromSettingsList(config, clientShortcut);
    }

}
