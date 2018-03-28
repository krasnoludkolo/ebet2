package pl.krasnoludkolo.ebet2.league.domain;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.*;

import java.util.UUID;

import static org.junit.Assert.*;

public class LeagueFacadeTest {

    private LeagueFacade facade;

    @Before
    public void init() {
        facade = new InMemorySystem().leagueFacade();
    }


    @Test
    public void shouldAddEmptyLeagueAndFindByUUID() {
        //when
        UUID uuid = facade.createLeague("new");
        //then
        Option<LeagueDTO> dto = facade.getLeagueByUUID(uuid);
        assertEquals("new", dto.get().getName());
    }

    @Test
    public void shouldAddEmptyLeagueAndFindByName() {
        //given
        //when
        facade.createLeague("new");
        //then
        Option<LeagueDTO> dto = facade.findLeagueByName("new");
        assertEquals("new", dto.get().getName());
    }

    @Test(expected = LeagueNameDuplicationException.class)
    public void shouldNotAddLeagueWithExistingName() {
        //when
        facade.createLeague("new");
        facade.createLeague("new");
    }

    @Test
    public void shouldReturnEmptyOptionIfNoLeague() {
        //then
        Option<LeagueDTO> byName = facade.findLeagueByName("");
        assertTrue(byName.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListIfNoLeague() {
        //then
        List<LeagueDetailsDTO> allLeagues = facade.getAllLeaguesDetails();
        assertTrue(allLeagues.isEmpty());
    }

    @Test
    public void shouldAddDwoEmptyLeaguesAndGetThem() {
        //when
        facade.createLeague("new");
        facade.createLeague("new2");
        //then
        List<LeagueDetailsDTO> dtos = facade.getAllLeaguesDetails();
        List<String> namesList = dtos.map(LeagueDetailsDTO::getName);
        assertTrue(namesList.contains("new"));
        assertTrue(namesList.contains("new2"));
    }

    @Test
    public void shouldAddMatchToLeagueToRoundOne() {
        //given
        UUID leagueUUID = facade.createLeague("new");
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1, leagueUUID);
        //when
        facade.addMatchToLeague(newMatchDTO);
        //then
        List<MatchDTO> matchesList = facade.getMatchesFromRound(leagueUUID, 1);
        assertTrue(matchesList.map(MatchDTO::getHost).contains("host"));
    }

    @Test
    public void shouldAddMatchToLeagueToRoundOneAndDwo() {
        //given
        UUID leagueUUID = facade.createLeague("new");
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1, leagueUUID);
        NewMatchDTO newMatchDTO2 = new NewMatchDTO("host2", "guest2", 2, leagueUUID);
        //when
        facade.addMatchToLeague(newMatchDTO);
        facade.addMatchToLeague(newMatchDTO2);
        //then
        List<MatchDTO> matchesList1 = facade.getMatchesFromRound(leagueUUID, 1);
        List<MatchDTO> matchesList2 = facade.getMatchesFromRound(leagueUUID, 2);
        assertTrue(matchesList1.map(MatchDTO::getHost).contains("host"));
        assertTrue(matchesList2.map(MatchDTO::getHost).contains("host2"));
    }

    @Test
    public void shouldAddMatchToLeague() {
        //given
        UUID leagueUUID = facade.createLeague("new");
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1, leagueUUID);
        NewMatchDTO other = new NewMatchDTO("host2", "guest2", 1, leagueUUID);
        //when
        UUID uuid = facade.addMatchToLeague(newMatchDTO);
        facade.addMatchToLeague(other);
        //then
        Option<MatchDTO> dto = facade.getMatchByUUID(uuid);
        if (dto.isEmpty()) {
            fail("No league");
        } else {
            assertEquals("host", dto.get().getHost());
        }
    }

    @Test
    public void shouldAddDwoMatchesToDwoLeaguesAndGetOnlyFromOne() {
        //given
        UUID leagueUUID = facade.createLeague("new");
        UUID leagueUUID2 = facade.createLeague("new2");
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1, leagueUUID);
        NewMatchDTO newMatchDTO2 = new NewMatchDTO("host2", "guest2", 1, leagueUUID2);
        //when
        facade.addMatchToLeague(newMatchDTO);
        facade.addMatchToLeague(newMatchDTO2);
        facade.addMatchToLeague(newMatchDTO);
        facade.addMatchToLeague(newMatchDTO2);
        //then
        List<MatchDTO> matchesFromRound = facade.getMatchesFromRound(leagueUUID, 1);
        assertEquals(2, matchesFromRound.size());
    }

    @Test
    public void shouldSetMatchResult() {
        //given
        UUID leagueUUID = facade.createLeague("new");
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1, leagueUUID);
        UUID matchUUID = facade.addMatchToLeague(newMatchDTO);
        //when
        facade.setMatchResult(matchUUID, MatchResult.DRAW);
        //then
        Option<MatchDTO> matchByUUID = facade.getMatchByUUID(matchUUID);
        assertEquals(MatchResult.DRAW, matchByUUID.get().getResult());
    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldNotBeAbleToSetNotSetResult() {
        //given
        UUID leagueUUID = facade.createLeague("new");
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1, leagueUUID);
        UUID matchUUID = facade.addMatchToLeague(newMatchDTO);
        //when
        facade.setMatchResult(matchUUID, MatchResult.NOT_SET);
    }

    @Test()
    public void shouldRemoveMatch() {
        //given
        UUID leagueUUID = facade.createLeague("new");
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1, leagueUUID);
        UUID matchUUID = facade.addMatchToLeague(newMatchDTO);
        //when
        facade.removeMatch(matchUUID);
        //then
        Option<MatchDTO> match = facade.getMatchByUUID(matchUUID);
        assertTrue(match.isEmpty());
    }

    @Test()
    public void shouldRemoveLeague() {
        //given
        UUID leagueUUID = facade.createLeague("new");
        //when
        facade.removeLeague(leagueUUID);
        //then
        Option<LeagueDTO> leagueByUUID = facade.getLeagueByUUID(leagueUUID);
        assertTrue(leagueByUUID.isEmpty());
    }

}
