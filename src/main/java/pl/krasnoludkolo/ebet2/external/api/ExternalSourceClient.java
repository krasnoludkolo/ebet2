package pl.krasnoludkolo.ebet2.external.api;

import io.vavr.collection.List;

public interface ExternalSourceClient {

    List<MatchInfo> downloadRound(ExternalSourceConfiguration config, int round);

    List<MatchInfo> downloadAllRounds(ExternalSourceConfiguration config);

    String getShortcut();

}