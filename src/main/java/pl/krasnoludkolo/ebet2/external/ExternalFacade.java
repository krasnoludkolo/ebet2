package pl.krasnoludkolo.ebet2.external;

import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
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
    public void updateLeague(UUID leagueUUID) {
        LeagueDetails leagueDetails = leagueDetailsRepository.findOne(leagueUUID).getOrElseThrow(IllegalArgumentException::new);
        ExternalSourceClient client = clientsMap.get(leagueDetails.getClientShortcut()).getOrElseThrow(IllegalArgumentException::new);
        LeagueDetails details = leagueUpdater.updateLeague(leagueDetails, client);
        leagueDetailsRepository.update(details.getLeagueUUID(), details);
    }

}
