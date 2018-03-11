package pl.krasnoludkolo.ebet2.domain.bet;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.domain.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.domain.bet.api.BetTyp;
import pl.krasnoludkolo.ebet2.domain.bet.api.NewBetDTO;
import pl.krasnoludkolo.ebet2.domain.bet.exceptions.BetNotFound;
import pl.krasnoludkolo.ebet2.domain.bet.exceptions.DuplicateUsername;
import pl.krasnoludkolo.ebet2.domain.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.domain.league.api.NewMatchDTO;

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
        UUID matchUUID = leagueFacade.addMatchToLeague(leagueUUID, new NewMatchDTO("host", "guest", 1, leagueUUID));
        //when
        UUID betUUID = betFacade.addBetToMatch(matchUUID, new NewBetDTO(BetTyp.DRAW, "username"));
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
        UUID matchUUID = leagueFacade.addMatchToLeague(leagueUUID, new NewMatchDTO("host", "guest", 1, leagueUUID));
        //when
        betFacade.addBetToMatch(matchUUID, new NewBetDTO(BetTyp.DRAW, "username"));
        betFacade.addBetToMatch(matchUUID, new NewBetDTO(BetTyp.DRAW, "username"));
    }

    @Test
    public void shouldUpdateBetToMatch() {
        //given
        UUID leagueUUID = leagueFacade.createLeague("new");
        UUID matchUUID = leagueFacade.addMatchToLeague(leagueUUID, new NewMatchDTO("host", "guest", 1, leagueUUID));
        UUID betUUID = betFacade.addBetToMatch(matchUUID, new NewBetDTO(BetTyp.HOST_WON, "username"));
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
        UUID matchUUID = leagueFacade.addMatchToLeague(leagueUUID, new NewMatchDTO("host", "guest", 1, leagueUUID));
        UUID betUUID = betFacade.addBetToMatch(matchUUID, new NewBetDTO(BetTyp.HOST_WON, "username"));
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
        UUID matchUUID = leagueFacade.addMatchToLeague(leagueUUID, new NewMatchDTO("host", "guest", 1, leagueUUID));
        //when
        betFacade.addBetToMatch(matchUUID, new NewBetDTO(BetTyp.HOST_WON, "username1"));
        betFacade.addBetToMatch(matchUUID, new NewBetDTO(BetTyp.HOST_WON, "username2"));
        betFacade.addBetToMatch(matchUUID, new NewBetDTO(BetTyp.HOST_WON, "username3"));
        //then
        List<BetDTO> dtos = betFacade.getAllBetsForMatch(matchUUID);
        assertEquals(3, dtos.size());
    }


}