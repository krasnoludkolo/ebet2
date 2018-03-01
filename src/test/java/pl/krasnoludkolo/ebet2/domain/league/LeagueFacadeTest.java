package pl.krasnoludkolo.ebet2.domain.league;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.domain.league.api.*;

import java.util.UUID;

import static org.junit.Assert.*;

public class LeagueFacadeTest {

    @Test
    public void shouldAddEmptyLeagueAndFindByUUID() {
        //given
        LeagueFacade facade = new LeagueConfiguration().leagueFacade();
        //when
        UUID uuid = facade.createLeague("new");
        //then
        Option<LeagueDTO> dto = facade.getLeagueByUUID(uuid);
        if (dto.isEmpty()) {
            fail("No league");
        } else {
            assertEquals("new", dto.get().getName());
        }
    }

    @Test
    public void shouldAddEmptyLeagueAndFindByName() {
        //given
        LeagueFacade facade = new LeagueConfiguration().leagueFacade();
        //when
        facade.createLeague("new");
        //then
        Option<LeagueDTO> dto = facade.findLeagueByName("new");
        if (dto.isEmpty()) {
            fail("No league");
        } else {
            assertEquals("new", dto.get().getName());
        }
    }

    @Test(expected = LeagueNameDuplicationException.class)
    public void shouldNotAddLeagueWithExistingName() {
        //given
        LeagueFacade facade = new LeagueConfiguration().leagueFacade();
        //when
        facade.createLeague("new");
        facade.createLeague("new");
    }

    @Test
    public void shouldReturnEmptyOptionIfNoLeague() {
        //given
        LeagueFacade facade = new LeagueConfiguration().leagueFacade();
        //then
        Option<LeagueDTO> byName = facade.findLeagueByName("");
        assertTrue(byName.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListIfNoLeague() {
        //given
        LeagueFacade facade = new LeagueConfiguration().leagueFacade();
        //then
        List<LeagueDTO> allLeagues = facade.getAllLeagues();
        assertTrue(allLeagues.isEmpty());
    }

    @Test
    public void shouldAddDwoEmptyLeaguesAndGetThem() {
        //given
        LeagueFacade facade = new LeagueConfiguration().leagueFacade();
        //when
        facade.createLeague("new");
        facade.createLeague("new2");
        //then
        List<LeagueDTO> dtos = facade.getAllLeagues();
        List<String> namesList = dtos.map(LeagueDTO::getName);
        assertTrue(namesList.contains("new"));
        assertTrue(namesList.contains("new2"));
    }

    @Test
    public void shouldAddMatchToLeagueToRoundOne() {
        //given
        LeagueFacade facade = new LeagueConfiguration().leagueFacade();
        UUID leagueUUID = facade.createLeague("new");
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1);
        //when
        facade.addMatchToLeague(leagueUUID, newMatchDTO);
        //then
        List<MatchDTO> matchesList = facade.getMatchesFromRound(leagueUUID, 1);
        assertTrue(matchesList.map(MatchDTO::getHost).contains("host"));
    }

    @Test
    public void shouldAddMatchToLeagueToRoundOneAndDwo() {
        //given
        LeagueFacade facade = new LeagueConfiguration().leagueFacade();
        UUID leagueUUID = facade.createLeague("new");
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1);
        NewMatchDTO newMatchDTO2 = new NewMatchDTO("host2", "guest2", 2);
        //when
        facade.addMatchToLeague(leagueUUID, newMatchDTO);
        facade.addMatchToLeague(leagueUUID, newMatchDTO2);
        //then
        List<MatchDTO> matchesList1 = facade.getMatchesFromRound(leagueUUID, 1);
        List<MatchDTO> matchesList2 = facade.getMatchesFromRound(leagueUUID, 2);
        assertTrue(matchesList1.map(MatchDTO::getHost).contains("host"));
        assertTrue(matchesList2.map(MatchDTO::getHost).contains("host2"));
    }

    @Test
    public void shouldAddMatchToLeague() {
        //given
        LeagueFacade facade = new LeagueConfiguration().leagueFacade();
        UUID leagueUUID = facade.createLeague("new");
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1);
        NewMatchDTO other = new NewMatchDTO("host2", "guest2", 1);
        //when
        UUID uuid = facade.addMatchToLeague(leagueUUID, newMatchDTO);
        facade.addMatchToLeague(leagueUUID, other);
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
        LeagueFacade facade = new LeagueConfiguration().leagueFacade();
        UUID leagueUUID = facade.createLeague("new");
        UUID leagueUUID2 = facade.createLeague("new2");
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1);
        NewMatchDTO newMatchDTO2 = new NewMatchDTO("host2", "guest2", 1);
        //when
        facade.addMatchToLeague(leagueUUID, newMatchDTO);
        facade.addMatchToLeague(leagueUUID, newMatchDTO2);
        facade.addMatchToLeague(leagueUUID2, newMatchDTO);
        facade.addMatchToLeague(leagueUUID2, newMatchDTO2);
        //then
        List<MatchDTO> matchesFromRound = facade.getMatchesFromRound(leagueUUID, 1);
        assertEquals(2, matchesFromRound.size());
    }

    @Test
    public void shouldSetMatchResult() {
        //given
        LeagueFacade facade = new LeagueConfiguration().leagueFacade();
        UUID leagueUUID = facade.createLeague("new");
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1);
        UUID matchUUID = facade.addMatchToLeague(leagueUUID, newMatchDTO);
        //when
        facade.setMatchResult(matchUUID, MatchResult.DRAW);
        //then
        Option<MatchDTO> matchByUUID = facade.getMatchByUUID(matchUUID);
        if (matchByUUID.isEmpty()) {
            fail("No match");
        } else {
            assertEquals(MatchResult.DRAW, matchByUUID.get().getResult());
        }
    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldNotBeAbleToSetNotSetResult() {
        //given
        LeagueFacade facade = new LeagueConfiguration().leagueFacade();
        UUID leagueUUID = facade.createLeague("new");
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1);
        UUID matchUUID = facade.addMatchToLeague(leagueUUID, newMatchDTO);
        //when
        facade.setMatchResult(matchUUID, MatchResult.NOT_SET);
    }
}
