package pl.krasnoludkolo.ebet2;

import io.vavr.collection.List;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.bet.BetFacade;
import pl.krasnoludkolo.ebet2.bet.api.BetTyp;
import pl.krasnoludkolo.ebet2.bet.api.NewBetDTO;
import pl.krasnoludkolo.ebet2.external.ExternalFacade;
import pl.krasnoludkolo.ebet2.external.api.ExternalSourceConfiguration;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.dto.LeagueDTO;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchResult;
import pl.krasnoludkolo.ebet2.points.PointsFacade;
import pl.krasnoludkolo.ebet2.points.api.UserResultDTO;
import pl.krasnoludkolo.ebet2.results.ResultFacade;
import pl.krasnoludkolo.ebet2.user.api.UserDetails;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class AutoImportAndLeagueResultsUpdate {

    private LocalDateTime nextYear = LocalDateTime.now().plus(1, ChronoUnit.YEARS);
    private ExternalFacade externalFacade;
    private BetFacade betFacade;
    private ResultFacade resultFacade;
    private PointsFacade pointsFacade;
    private LeagueFacade leagueFacade;
    private InMemorySystem system;
    private UserDetails auth;
    private UserDetails auth2;

    @Before
    public void init() {
        system = new InMemorySystem();
        externalFacade = system.externalFacade();
        betFacade = system.betFacade();
        resultFacade = system.resultFacade();
        pointsFacade = system.pointsFacade();
        leagueFacade = system.leagueFacade();
        auth = system.getSampleUserDetailList().get(0);
        auth2 = system.getSampleUserDetailList().get(1);

    }

    @Test
    public void shouldImportLeagueMakeBetAndUpdateResult() {
        system.setExternalSourceMatchList(first());
        ExternalSourceConfiguration config = ExternalSourceConfiguration.empty("Mock");
        UUID leagueUUID = system.sampleLeagueUUID();
        externalFacade.initializeLeagueConfiguration(config, leagueUUID);
        resultFacade.manuallyUpdateLeague(leagueUUID);

        LeagueDTO leagueDTO = leagueFacade.getLeagueByUUID(leagueUUID).get();
        MatchDTO matchDTO = leagueDTO.getMatchDTOS().stream().filter(m -> m.getResult() == MatchResult.NOT_SET).findFirst().get();
        UUID matchUUID = matchDTO.getUuid();

        betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.GUEST_WON, matchUUID), auth2.getToken());

        system.setExternalSourceMatchList(withResult());
        resultFacade.manuallyUpdateLeague(leagueUUID);

        UserResultDTO user1 = pointsFacade.getResultsFromLeagueToUser(leagueUUID, auth.getUserUUID()).get();
        UserResultDTO user2 = pointsFacade.getResultsFromLeagueToUser(leagueUUID, auth2.getUserUUID()).get();

        assertEquals(1, user1.getPointCounter());
        assertEquals(0, user2.getPointCounter());
    }

    private List<MatchInfo> first() {
        return List.of(new MatchInfo("a", "b", 1, false, MatchResult.NOT_SET, nextYear));
    }

    private List<MatchInfo> withResult() {
        return List.of(new MatchInfo("a", "b", 1, true, MatchResult.DRAW, nextYear));
    }

}

