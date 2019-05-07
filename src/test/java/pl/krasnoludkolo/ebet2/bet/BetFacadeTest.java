package pl.krasnoludkolo.ebet2.bet;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.bet.api.BetError;
import pl.krasnoludkolo.ebet2.bet.api.BetTyp;
import pl.krasnoludkolo.ebet2.bet.api.NewBetDTO;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;
import pl.krasnoludkolo.ebet2.user.api.UserDetails;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class BetFacadeTest {

    private LocalDateTime nextYear = LocalDateTime.now().plus(1, ChronoUnit.YEARS);
    private LocalDateTime lastYear = LocalDateTime.now().minus(1, ChronoUnit.YEARS);

    private BetFacade betFacade;
    private LeagueFacade leagueFacade;
    private UserDetails auth;
    private UserDetails auth2;
    private UserDetails auth3;
    private InMemorySystem system;

    @Before
    public void init() {
        system = new InMemorySystem();
        betFacade = system.betFacade();
        leagueFacade = system.leagueFacade();
        auth = system.getSampleUserDetailList().get(0);
        auth2 = system.getSampleUserDetailList().get(1);
        auth3 = system.getSampleUserDetailList().get(2);
    }

    @Test
    public void shouldAddBetToFutureMatch() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        //when
        UUID betUUID = betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth.getToken()).get();
        //then
        Option<BetDTO> bet = betFacade.findBetByUUID(betUUID);
        BetDTO betDTO = bet.get();
        assertEquals(matchUUID, betDTO.getMatchUUID());
        assertEquals(BetTyp.DRAW, betDTO.getBetTyp());
        assertEquals(auth.getUserUUID(), betDTO.getUserUUID());
    }

    @Test
    public void shouldNotAddBetAfterMatchStarts() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, lastYear)).get();
        //when
        BetError error = betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth.getToken()).getLeft();
        //then
        assertEquals(BetError.MATCH_ALREADY_STARTED, error);
    }

    @Test
    public void shouldNotUpdateBetAfterMatchStarts() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        UUID betUUID = betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth.getToken()).get();
        //when
        system.advanceTimeBy(2 * 365, TimeUnit.DAYS);
        BetError error = betFacade.updateBetToMatch(betUUID, BetTyp.DRAW, auth.getToken()).getLeft();
        //then
        assertEquals(BetError.MATCH_ALREADY_STARTED, error);
    }

    @Test
    public void shouldNotAddBetTwiceToSameMatch() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        //when
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID), auth.getToken());
        BetError error = betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID), auth.getToken()).getLeft();
        assertEquals(BetError.BET_ALREADY_SET, error);
    }

    @Test
    public void shouldUpdateBetToMatch() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        UUID betUUID = betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID), auth.getToken()).get();
        //when
        betFacade.updateBetToMatch(betUUID, BetTyp.DRAW, auth.getToken());
        //then
        Option<BetDTO> bet = betFacade.findBetByUUID(betUUID);
        BetDTO betDTO = bet.get();
        assertEquals(BetTyp.DRAW, betDTO.getBetTyp());
    }

    @Test
    public void shouldNotUpdateNoExistingBet() {
        //when
        UUID betUUID = UUID.randomUUID();
        BetError error = betFacade.updateBetToMatch(betUUID, BetTyp.DRAW, auth.getToken()).getLeft();
        assertEquals(BetError.BET_NOT_FOUND, error);
    }

    @Test
    public void shouldRemoveBetToMatch() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        UUID betUUID = betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID), auth.getToken()).get();
        //when
        betFacade.removeBet(betUUID);
        //then
        Option<BetDTO> bet = betFacade.findBetByUUID(betUUID);
        assertTrue(bet.isEmpty());
    }

    @Test
    public void shouldGetAllBetsForMatch() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new").get();
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        //when
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID), auth.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID), auth2.getToken());
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID), auth3.getToken());
        //then
        List<BetDTO> dtos = betFacade.getAllBetsForMatch(matchUUID);
        assertEquals(3, dtos.size());
    }

}