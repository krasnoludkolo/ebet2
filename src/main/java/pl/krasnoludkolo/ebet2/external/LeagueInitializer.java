package pl.krasnoludkolo.ebet2.external;

import io.vavr.collection.List;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;

import java.time.LocalDateTime;
import java.util.UUID;

class LeagueInitializer {

    private LeagueFacade leagueFacade;

    LeagueInitializer(LeagueFacade leagueFacade) {
        this.leagueFacade = leagueFacade;
    }

    LeagueDetails initializeLeague(ExternalSourceClient client, ExternalSourceConfiguration config, String leagueName) {
        List<MatchInfo> matchInfos = client.downloadAllRounds(config);
        UUID leagueUUID = initializeLeagueInLeagueModule(matchInfos, leagueName);
        String shortcut = client.getShortcut();
        return LeagueDetailsCreator.fromExternalSourceConfiguration(leagueUUID, config, shortcut);
    }

    private UUID initializeLeagueInLeagueModule(List<MatchInfo> matchInfos, String leagueName) {
        UUID leagueUUID = createLeague(leagueName);
        matchInfos.forEach(matchInfo -> addMatchToLeague(matchInfo, leagueUUID));
        return leagueUUID;
    }

    private UUID createLeague(String leagueName) {
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
        LocalDateTime startDate = matchInfo.getMatchStartDate();
        return new NewMatchDTO(host, guest, round, leagueUUID, startDate);
    }


}
