package pl.krasnoludkolo.ebet2.external;

import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;

import java.util.UUID;

public class ExternalFacade {

    private LeagueInitializer leagueInitializer;
    private LeagueUpdater leagueUpdater;
    private Map<String, ExternalSourceClient> clientsMap;
    private Repository<LeagueDetails> leagueDetailsRepository;
    private AutoUpdaterScheduler autoUpdaterScheduler;

    ExternalFacade(LeagueFacade leagueFacade, LeagueUpdater leagueUpdater, List<ExternalSourceClient> clients, Repository<LeagueDetails> leagueDetailsRepository) {
        leagueInitializer = new LeagueInitializer(leagueFacade);
        this.leagueUpdater = leagueUpdater;
        clientsMap = clients.toMap(client -> Tuple.of(client.getShortcut(), client));
        this.leagueDetailsRepository = leagueDetailsRepository;
        setUpUpdateScheduler();
    }

    private void setUpUpdateScheduler() {
        autoUpdaterScheduler = new AutoUpdaterScheduler(this);
    }

    public UUID initializeLeague(ExternalSourceConfiguration config, String clientShortcut, String leagueName) {
        ExternalSourceClient client = clientsMap.get(clientShortcut).getOrElseThrow(IllegalArgumentException::new);
        LeagueDetails leagueDetails = leagueInitializer.initializeLeague(client, config, leagueName);
        leagueDetailsRepository.save(leagueDetails.getLeagueUUID(), leagueDetails);
        return leagueDetails.getLeagueUUID();
    }

    //TODO async
    public void updateLeague(UUID leagueUUID) {
        LeagueDetails leagueDetails = leagueDetailsRepository.findOne(leagueUUID).getOrElseThrow(IllegalArgumentException::new);
        ExternalSourceClient client = clientsMap.get(leagueDetails.getClientShortcut()).getOrElseThrow(IllegalArgumentException::new);
        LeagueDetails details = leagueUpdater.updateLeague(leagueDetails, client);
        leagueDetailsRepository.update(details.getLeagueUUID(), details);
    }

    public void addLeagueToAutoUpdater(UUID leagueUUID) {
        checkIfLeagueDetailsExist(leagueUUID);
        autoUpdaterScheduler.addLeagueToAutoUpdate(leagueUUID);
    }

    private void checkIfLeagueDetailsExist(UUID leagueUUID) {
        if (leagueDetailsRepository.findOne(leagueUUID).isEmpty()) {
            throw new IllegalArgumentException("No league with given uuid");
        }
    }

}
