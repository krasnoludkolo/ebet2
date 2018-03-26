package pl.krasnoludkolo.ebet2.autoimport;

import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import pl.krasnoludkolo.ebet2.autoimport.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.autoimport.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;

import java.util.UUID;

public class AutoImportFacade {

    private LeagueInitializer leagueInitializer;
    private LeagueUpdater leagueUpdater;
    private Map<String, ExternalSourceClient> clientsMap;
    private Repository<LeagueDetails> leagueDetailsRepository;

    AutoImportFacade(LeagueFacade leagueFacade, LeagueUpdater leagueUpdater, List<ExternalSourceClient> clients, Repository<LeagueDetails> leagueDetailsRepository) {
        leagueInitializer = new LeagueInitializer(leagueFacade);
        this.leagueUpdater = leagueUpdater;
        clientsMap = clients.toMap(client -> Tuple.of(client.getShortcut(), client));
        this.leagueDetailsRepository = leagueDetailsRepository;
    }

    public UUID initializeLeague(ExternalSourceConfiguration config, String clientShortcut) {
        ExternalSourceClient client = clientsMap.get(clientShortcut).getOrElseThrow(IllegalArgumentException::new);
        LeagueDetails leagueDetails = leagueInitializer.initializeLeague(client, config);
        leagueDetailsRepository.save(leagueDetails.getLeagueUUID(), leagueDetails);
        return leagueDetails.getLeagueUUID();
    }

    public void updateLeague(UUID leagueUUID) {
        LeagueDetails leagueDetails = leagueDetailsRepository.findOne(leagueUUID).getOrElseThrow(IllegalArgumentException::new);
        ExternalSourceClient client = clientsMap.get(leagueDetails.getClientShortcut()).getOrElseThrow(IllegalArgumentException::new);
        LeagueDetails details = leagueUpdater.updateLeague(leagueDetails, client);
        leagueDetailsRepository.update(details.getLeagueUUID(), details);
    }


}
