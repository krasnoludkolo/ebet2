package pl.krasnoludkolo.ebet2.external;

import io.vavr.collection.List;
import io.vavr.control.Either;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceClient;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.LeagueError;
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;

import java.time.LocalDateTime;
import java.util.UUID;

class LeagueInitializer {

    private LeagueFacade leagueFacade;

    LeagueInitializer(LeagueFacade leagueFacade) {
        this.leagueFacade = leagueFacade;
    }

    Either<LeagueError, LeagueDetails> initializeLeague(ExternalSourceClient client, ExternalSourceConfiguration config, String leagueName) {
        List<MatchInfo> matchInfos = client.downloadAllRounds(config);
        Either<LeagueError, UUID> leagueUUID = initializeLeagueInLeagueModule(matchInfos, leagueName);
        String shortcut = client.getShortcut();
        return leagueUUID.map(uuid -> LeagueDetailsCreator.fromExternalSourceConfiguration(uuid, config, shortcut));
    }

    private Either<LeagueError, UUID> initializeLeagueInLeagueModule(List<MatchInfo> matchInfos, String leagueName) {
        Either<LeagueError, UUID> leagueUUID = createLeague(leagueName);
        leagueUUID.peek(uuid -> matchInfos.forEach(matchInfo -> addMatchToLeague(matchInfo, uuid)));
        return leagueUUID;
    }

    private Either<LeagueError, UUID> createLeague(String leagueName) {
        return leagueFacade.createLeague(leagueName);
    }

    private void addMatchToLeague(MatchInfo matchInfo, UUID leagueUUID) {
        NewMatchDTO newMatchDTO = createNewMatchDTO(matchInfo, leagueUUID);
        Either<String, UUID> uuidEither = leagueFacade.addMatchToLeague(newMatchDTO);
        if (matchInfo.isFinished()) {
            uuidEither.peek(uuid -> leagueFacade.setMatchResult(uuidEither.get(), matchInfo.getResult()));
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
