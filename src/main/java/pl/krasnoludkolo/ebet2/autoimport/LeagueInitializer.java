package pl.krasnoludkolo.ebet2.autoimport;

import io.vavr.collection.List;
import pl.krasnoludkolo.ebet2.autoimport.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.autoimport.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.autoimport.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;

import java.util.UUID;

class LeagueInitializer {

    private LeagueFacade leagueFacade;

    LeagueInitializer(LeagueFacade leagueFacade) {
        this.leagueFacade = leagueFacade;
    }

    LeagueDetails initializeLeague(ExternalSourceClient client, ExternalSourceConfiguration config) {
        List<MatchInfo> matchInfos = client.downloadAllRounds(config);
        UUID leagueUUID = createLeague(config);
        matchInfos
                .forEach(matchInfo -> addMatchToLeague(matchInfo, leagueUUID));
        return new LeagueDetails(leagueUUID, config, 0);
    }

    private UUID createLeague(ExternalSourceConfiguration config) {
        String leagueName = config.getParameter("name");
        return leagueFacade.createLeague(leagueName);
    }

    private void addMatchToLeague(MatchInfo matchInfo, UUID leagueUUID) {
        NewMatchDTO newMatchDTO = createNewMatchDTO(matchInfo, leagueUUID);
        UUID uuid = leagueFacade.addMatchToLeague(newMatchDTO);
        if (matchInfo.isFinished()) {
            leagueFacade.setMatchResult(uuid, matchInfo.getResult());
        }

    }

    private NewMatchDTO createNewMatchDTO(MatchInfo matchInfo, UUID leagueUUID) {
        String host = matchInfo.getHostName();
        String guest = matchInfo.getGuestName();
        int round = matchInfo.getRound();
        return new NewMatchDTO(host, guest, round, leagueUUID);

    }


}
