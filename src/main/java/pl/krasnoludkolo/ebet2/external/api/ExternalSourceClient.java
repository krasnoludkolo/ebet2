package pl.krasnoludkolo.ebet2.external.api;

import io.vavr.collection.List;
import io.vavr.control.Either;

public interface ExternalSourceClient {

    Either<ExternalError, List<MatchInfo>> downloadAllRounds(ExternalSourceConfiguration config);

    String getShortcut();

}
