package pl.krasnoludkolo.ebet2.autoimport;

import io.vavr.collection.List;

interface ExternalSourceClient {

    List<MatchInfo> downloadRound(ExternalSourceConfiguration config, int round);

    List<MatchInfo> downloadAllRounds(ExternalSourceConfiguration config);

    String getShortcut();

}
