package pl.krasnoludkolo.ebet2.external;

import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.external.api.ExternalError;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;

import java.util.UUID;

public class ExternalFacade {

    private Map<String, ExternalSourceClient> clientsMap;
    private Repository<LeagueDetails> leagueDetailsRepository;

    ExternalFacade(List<ExternalSourceClient> clients, Repository<LeagueDetails> leagueDetailsRepository) {
        clientsMap = clients.toMap(client -> Tuple.of(client.getShortcut(), client));
        this.leagueDetailsRepository = leagueDetailsRepository;
    }


    public Either<ExternalError, UUID> initializeLeagueConfiguration(ExternalSourceConfiguration config, UUID leagueUUID) {
        return clientsMap
                .get(config.getClientShortcut())
                .toEither(ExternalError.NO_EXTERNAL_CLIENT)
                .map(client -> LeagueInitializer.initializeLeague(client, config, leagueUUID))
                .map(this::saveToRepository)
                .map(LeagueDetails::getLeagueUUID);
    }

    private LeagueDetails saveToRepository(LeagueDetails details) {
        leagueDetailsRepository.save(details.getLeagueUUID(), details);
        return details;
    }

    public Either<ExternalError, List<MatchInfo>> downloadLeague(UUID leagueUUID) {
        return leagueDetailsRepository
                .findOne(leagueUUID)
                .toEither(ExternalError.NO_LEAGUE_DETAILS)
                .flatMap(this::downloadLeague);
    }

    private Either<ExternalError, List<MatchInfo>> downloadLeague(LeagueDetails details) {
        ExternalSourceConfiguration configuration = LeagueDetailsCreator.toExternalSourceConfiguration(details);
        return clientsMap
                .get(details.getClientShortcut())
                .toEither(ExternalError.NO_EXTERNAL_CLIENT)
                .flatMap(client -> client.downloadAllRounds(configuration));
    }

}
