package pl.krasnoludkolo.ebet2.external;

import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.UpdateError;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.infrastructure.Success;
import pl.krasnoludkolo.ebet2.league.api.LeagueError;

import java.util.UUID;

public class ExternalFacade {

    private LeagueInitializer leagueInitializer;
    private LeagueUpdater leagueUpdater;
    private Map<String, ExternalSourceClient> clientsMap;
    private Repository<LeagueDetails> leagueDetailsRepository;

    ExternalFacade(LeagueInitializer leagueInitializer, LeagueUpdater leagueUpdater, List<ExternalSourceClient> clients, Repository<LeagueDetails> leagueDetailsRepository) {
        this.leagueInitializer = leagueInitializer;
        this.leagueUpdater = leagueUpdater;
        clientsMap = clients.toMap(client -> Tuple.of(client.getShortcut(), client));
        this.leagueDetailsRepository = leagueDetailsRepository;
    }


    public Either<LeagueError, UUID> initializeLeague(ExternalSourceConfiguration config, String clientShortcut, String leagueName) {
        ExternalSourceClient client = clientsMap.get(clientShortcut).getOrElseThrow(IllegalArgumentException::new);
        Either<LeagueError, LeagueDetails> leagueDetails = leagueInitializer.initializeLeague(client, config, leagueName);
        leagueDetails.peek(details -> leagueDetailsRepository.save(details.getLeagueUUID(), details));
        return leagueDetails.map(LeagueDetails::getLeagueUUID);
    }

    //TODO async
    public Either<UpdateError, Success> updateLeague(UUID leagueUUID) {
        return leagueDetailsRepository
                .findOne(leagueUUID)
                .toEither(UpdateError.NO_LEAGUE_DETAILS)
                .flatMap(this::getExternalSourceClientForLeague)
                .map(leagueUpdater::updateLeague)
                .map(Success::new);
    }

    private Either<UpdateError, UpdateInfo> getExternalSourceClientForLeague(LeagueDetails details) {
        return Option.of(details)
                .map(LeagueDetails::getClientShortcut)
                .flatMap(clientsMap::get)
                .map(client -> new UpdateInfo(details, client))
                .toEither(UpdateError.NO_EXTERNAL_CLIENT);


    }

}
