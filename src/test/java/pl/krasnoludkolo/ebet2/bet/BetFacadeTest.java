package pl.krasnoludkolo.ebet2.bet;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.bet.api.*;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class BetFacadeTest {

    private BetFacade betFacade;
    private LeagueFacade leagueFacade;
    private String auth;
    private String auth2;
    private String auth3;

    @Before
    public void init() {
        InMemorySystem system = new InMemorySystem();
        betFacade = system.betFacade();
        leagueFacade = system.leagueFacade();
        auth = system.getSampleUsersApiToken().get(0);
        auth2 = system.getSampleUsersApiToken().get(1);
        auth3 = system.getSampleUsersApiToken().get(2);
    }

    @Test
    public void shouldAddBetToMatch() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID));
        //when
        UUID betUUID = betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, matchUUID), auth);
        //then
        Option<BetDTO> bet = betFacade.findBetByUUID(betUUID);
        BetDTO betDTO = bet.get();
        assertEquals(matchUUID, betDTO.getMatchUUID());
        assertEquals(BetTyp.DRAW, betDTO.getBetTyp());
        assertEquals("user1", betDTO.getUsername());
    }

    @Test(expected = BetAlreadySet.class)
    public void shouldNotAddBetToMatchWithUsedUsername() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID));
        //when
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID), auth);
    }

    @Test
    public void shouldUpdateBetToMatch() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID));
        UUID betUUID = betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID), auth);
        //when
        betFacade.updateBetToMatch(betUUID, BetTyp.DRAW, auth);
        //then
        Option<BetDTO> bet = betFacade.findBetByUUID(betUUID);
        BetDTO betDTO = bet.get();
        assertEquals(BetTyp.DRAW, betDTO.getBetTyp());
    }

    @Test(expected = BetNotFound.class)
    public void shouldNotUpdateBet() {
        //when
        betFacade.updateBetToMatch(UUID.randomUUID(), BetTyp.DRAW, auth);
    }

    @Test
    public void shouldRemoveBetToMatch() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID));
        UUID betUUID = betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID), auth);
        //when
        betFacade.removeBet(betUUID);
        //then
        Option<BetDTO> bet = betFacade.findBetByUUID(betUUID);
        assertTrue(bet.isEmpty());
    }

    @Test
    public void shouldGetAllBetsForMatch() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID));
        //when
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID), auth);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID), auth2);
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, matchUUID), auth3);
        //then
        List<BetDTO> dtos = betFacade.getAllBetsForMatch(matchUUID);
        assertEquals(3, dtos.size());
    }


}