package pl.krasnoludkolo.ebet2.league;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.league.api.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.Assert.*;

public class LeagueFacadeTest {

    private LocalDateTime nextYear = LocalDateTime.now().plus(1, ChronoUnit.YEARS);
    private LocalDateTime lastYear = LocalDateTime.now().minus(1, ChronoUnit.YEARS);

    private LeagueFacade facade;

    @Before
    public void init() {
        facade = new InMemorySystem().leagueFacade();
    }

    @Test
    public void shouldNotCreateLeagueWithNoName() {
        LeagueError leagueError = facade.createLeague("").left().get();
        assertEquals(LeagueError.WRONG_NAME_EXCEPTION, leagueError);
    }

    @Test
    public void shouldNotCreateLeagueWithNullName() {
        LeagueError leagueError = facade.createLeague(null).left().get();
        assertEquals(LeagueError.WRONG_NAME_EXCEPTION, leagueError);
    }

    @Test
    public void shouldAddEmptyLeagueAndFindByUUID() {
        //when
        UUID uuid = facade.createLeague("new").get();
        //then
        Option<LeagueDTO> dto = facade.getLeagueByUUID(uuid);
        assertEquals("new", dto.get().getName());
    }

    @Test
    public void shouldAddEmptyLeagueAndFindByName() {
        //given
        //when
        facade.createLeague("new").get();
        //then
        Option<LeagueDTO> dto = facade.findLeagueByName("new");
        assertEquals("new", dto.get().getName());
    }

    @Test
    public void shouldNotAddLeagueWithExistingName() {
        //when
        facade.createLeague("new");
        LeagueError leagueError = facade.createLeague("new").left().get();
        assertEquals(LeagueError.LEAGUE_NAME_DUPLICATION, leagueError);
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
        facade.createLeague("new").get();
        facade.createLeague("new2").get();
        //then
        List<LeagueDetailsDTO> dtos = facade.getAllLeaguesDetails();
        List<String> namesList = dtos.map(LeagueDetailsDTO::getName);
        assertTrue(namesList.contains("new"));
        assertTrue(namesList.contains("new2"));
    }

    @Test
    public void shouldAddMatchToLeagueToRoundOne() {
        //given
        UUID leagueUUID = facade.createLeague("new").get();
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear);
        //when
        facade.addMatchToLeague(newMatchDTO);
        //then
        List<MatchDTO> matchesList = facade.getMatchesFromRound(leagueUUID, 1).get();
        assertTrue(matchesList.map(MatchDTO::getHost).contains("host"));
    }

    @Test
    public void shouldAddMatchToLeagueToRoundOneAndDwo() {
        //given
        UUID leagueUUID = facade.createLeague("new").get();
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear);
        NewMatchDTO newMatchDTO2 = new NewMatchDTO("host2", "guest2", 2, leagueUUID, nextYear);
        //when
        facade.addMatchToLeague(newMatchDTO);
        facade.addMatchToLeague(newMatchDTO2);
        //then
        List<MatchDTO> matchesList1 = facade.getMatchesFromRound(leagueUUID, 1).get();
        List<MatchDTO> matchesList2 = facade.getMatchesFromRound(leagueUUID, 2).get();
        assertTrue(matchesList1.map(MatchDTO::getHost).contains("host"));
        assertTrue(matchesList2.map(MatchDTO::getHost).contains("host2"));
    }

    @Test
    public void shouldAddMatchToLeague() {
        //given
        UUID leagueUUID = facade.createLeague("new").get();
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear);
        NewMatchDTO other = new NewMatchDTO("host2", "guest2", 1, leagueUUID, nextYear);
        //when
        UUID uuid = facade.addMatchToLeague(newMatchDTO).get();
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
        UUID leagueUUID = facade.createLeague("new").get();
        UUID leagueUUID2 = facade.createLeague("new2").get();
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear);
        NewMatchDTO newMatchDTO2 = new NewMatchDTO("host2", "guest2", 1, leagueUUID2, nextYear);
        //when
        facade.addMatchToLeague(newMatchDTO);
        facade.addMatchToLeague(newMatchDTO2);
        facade.addMatchToLeague(newMatchDTO);
        facade.addMatchToLeague(newMatchDTO2);
        //then
        List<MatchDTO> matchesFromRound = facade.getMatchesFromRound(leagueUUID, 1).get();
        assertEquals(2, matchesFromRound.size());
    }

    @Test
    public void shouldSetMatchResult() {
        //given
        UUID leagueUUID = facade.createLeague("new").get();
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear);
        UUID matchUUID = facade.addMatchToLeague(newMatchDTO).get();
        //when
        facade.setMatchResult(matchUUID, MatchResult.DRAW);
        //then
        Option<MatchDTO> matchByUUID = facade.getMatchByUUID(matchUUID);
        assertEquals(MatchResult.DRAW, matchByUUID.get().getResult());
    }


    @Test
    public void shouldNotBeAbleToSetNotSetResult() {
        //given
        UUID leagueUUID = facade.createLeague("new").get();
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear);
        UUID matchUUID = facade.addMatchToLeague(newMatchDTO).get();
        //when
        LeagueError leagueError = facade.setMatchResult(matchUUID, MatchResult.NOT_SET).left().get();
        assertEquals(LeagueError.SET_NOT_SET_RESULT, leagueError);
    }

    @Test
    public void shouldRemoveMatch() {
        //given
        UUID leagueUUID = facade.createLeague("new").get();
        NewMatchDTO newMatchDTO = new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear);
        UUID matchUUID = facade.addMatchToLeague(newMatchDTO).get();
        //when
        facade.removeMatch(matchUUID);
        //then
        Option<MatchDTO> match = facade.getMatchByUUID(matchUUID);
        assertTrue(match.isEmpty());
    }

    @Test
    public void shouldRemoveLeague() {
        //given
        UUID leagueUUID = facade.createLeague("new").get();
        //when
        facade.removeLeague(leagueUUID);
        //then
        Option<LeagueDTO> leagueByUUID = facade.getLeagueByUUID(leagueUUID);
        assertTrue(leagueByUUID.isEmpty());
    }

    @Test
    public void shouldGetAllMatchesFromLeague() {
        //given
        UUID tested = facade.createLeague("test").get();
        UUID other = facade.createLeague("test2").get();
        //when
        facade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, tested, nextYear));
        facade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, tested, nextYear));
        facade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, tested, nextYear));
        facade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, other, nextYear));
        //then
        int matchesInLeague = facade.getAllMatchesFromLeague(tested).size();
        assertEquals(3, matchesInLeague);

    }

    @Test
    public void shouldReturnTrueForStartedMatch() {
        //given
        UUID leagueUUID = facade.createLeague("test").get();
        UUID matchUUID = facade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, lastYear)).get();
        //when
        boolean ifMatchBegun = facade.hasMatchAlreadyBegun(matchUUID).get();
        assertTrue(ifMatchBegun);
    }

    @Test
    public void shouldReturnFalseForNotStartedMatch() {
        //given
        UUID leagueUUID = facade.createLeague("test").get();
        UUID matchUUID = facade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, nextYear)).get();
        //when
        boolean ifMatchBegun = facade.hasMatchAlreadyBegun(matchUUID).get();
        assertFalse(ifMatchBegun);
    }

    @Test
    public void shouldReturnFalseForMatchWithoutDate() {
        //given
        UUID leagueUUID = facade.createLeague("test").get();
        UUID matchUUID = facade.addMatchToLeague(new NewMatchDTO("host", "guest", 1, leagueUUID, null)).get();
        //when
        boolean ifMatchBegun = facade.hasMatchAlreadyBegun(matchUUID).get();
        assertTrue(ifMatchBegun);

    }

    @Test
    public void shouldReturnMatchNotFoundForNoExistingMatch() {
        //when
        LeagueError errorMessage = facade.hasMatchAlreadyBegun(UUID.randomUUID()).getLeft();
        assertEquals(LeagueError.MATCH_NOT_FOUND, errorMessage);
    }

    @Test
    public void shouldNotAddMatchWithNullHostName() {
        UUID leagueUUID = facade.createLeague("test").get();
        LeagueError error = facade.addMatchToLeague(new NewMatchDTO(null, "guest", 1, leagueUUID, nextYear)).getLeft();
        assertEquals(LeagueError.EMPTY_OR_NULL_NAME, error);
    }

    @Test
    public void shouldNotAddMatchWithNullGuestName() {
        UUID leagueUUID = facade.createLeague("test").get();
        LeagueError error = facade.addMatchToLeague(new NewMatchDTO("host", null, 1, leagueUUID, nextYear)).getLeft();
        assertEquals(LeagueError.EMPTY_OR_NULL_NAME, error);
    }

    @Test
    public void shouldNotAddMatchWithEmptyHostName() {
        UUID leagueUUID = facade.createLeague("test").get();
        LeagueError error = facade.addMatchToLeague(new NewMatchDTO("", "guest", 1, leagueUUID, nextYear)).getLeft();
        assertEquals(LeagueError.EMPTY_OR_NULL_NAME, error);
    }

    @Test
    public void shouldNotAddMatchWithEmptyGuestName() {
        UUID leagueUUID = facade.createLeague("test").get();
        LeagueError error = facade.addMatchToLeague(new NewMatchDTO("host", "", 1, leagueUUID, nextYear)).getLeft();
        assertEquals(LeagueError.EMPTY_OR_NULL_NAME, error);
    }

    @Test
    public void shouldArchiveLeague() {
        UUID leagueUUID = facade.createLeague("test").get();
        facade.archiveLeague(leagueUUID);
        boolean archived = facade.getLeagueByUUID(leagueUUID).get().isArchived();
        assertTrue(archived);
    }

    @Test
    public void shouldNotArchiveNoExistingLeague() {
        LeagueError leagueError = facade.archiveLeague(UUID.randomUUID()).left().get();
        assertEquals(LeagueError.LEAGUE_NOT_FOUND, leagueError);
    }


}
