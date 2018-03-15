package pl.krasnoludkolo.ebet2.bet.domain;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.bet.api.*;
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;
import pl.krasnoludkolo.ebet2.league.domain.LeagueFacade;

import java.util.UUID;

import static org.junit.Assert.*;


public class BetFacadeTest {

    private BetFacade betFacade;
    private LeagueFacade leagueFacade;

    @Before
    public void init() {
        InMemorySystem system = new InMemorySystem();
        betFacade = system.betFacade();
        leagueFacade = system.leagueFacade();
    }

    @Test
    public void shouldAddBetToMatch() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID));
        //when
        UUID betUUID = betFacade.addBetToMatch(new NewBetDTO(BetTyp.DRAW, "username", matchUUID));
        //then
        Option<BetDTO> bet = betFacade.findBetByUUID(betUUID);
        if (bet.isEmpty()) {
            fail("No bet");
        }

        BetDTO betDTO = bet.get();
        assertEquals(matchUUID, betDTO.getMatchUUID());
        assertEquals(BetTyp.DRAW, betDTO.getBetTyp());
        assertEquals("username", betDTO.getUsername());
    }

    @Test(expected = DuplicateUsername.class)
    public void shouldNotAddBetToMatchWithUsedUsername() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID));
        //when
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, "username", matchUUID));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, "username", matchUUID));
    }

    @Test
    public void shouldUpdateBetToMatch() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID));
        UUID betUUID = betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, "username", matchUUID));
        //when
        betFacade.updateBetToMatch(betUUID, BetTyp.DRAW);
        //then
        Option<BetDTO> bet = betFacade.findBetByUUID(betUUID);
        if (bet.isEmpty()) {
            fail("No bet");
        }
        BetDTO betDTO = bet.get();
        assertEquals(BetTyp.DRAW, betDTO.getBetTyp());
    }

    @Test(expected = BetNotFound.class)
    public void shouldNotUpdateBet() {
        //when
        betFacade.updateBetToMatch(UUID.randomUUID(), BetTyp.DRAW);
    }

    @Test
    public void shouldRemoveBetToMatch() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID));
        UUID betUUID = betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, "username", matchUUID));
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
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, "username", matchUUID));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, "username2", matchUUID));
        betFacade.addBetToMatch(new NewBetDTO(BetTyp.HOST_WON, "username3", matchUUID));
        //then
        List<BetDTO> dtos = betFacade.getAllBetsForMatch(matchUUID);
        assertEquals(3, dtos.size());
    }


}