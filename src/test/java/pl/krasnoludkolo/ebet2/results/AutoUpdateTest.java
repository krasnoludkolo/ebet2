package pl.krasnoludkolo.ebet2.results;

import io.vavr.collection.List;
import org.junit.Before;
import org.junit.Test;
import pl.krasnoludkolo.ebet2.InMemorySystem;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.infrastructure.InMemoryRepository;
import pl.krasnoludkolo.ebet2.league.LeagueFacade;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchResult;
import pl.krasnoludkolo.ebet2.league.api.dto.NewMatchDTO;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class AutoUpdateTest {


    private LeagueFacade leagueFacade;
    private InMemorySystem system;
    private ResultFacade resultFacade;
    private LocalDateTime matchStartDate;

    @Before
    public void setUp() {
        system = new InMemorySystem();
        leagueFacade = system.leagueFacade();
        this.resultFacade = system.resultFacade();
        system.setExternalSourceMatchList(List.of(planned()));
        matchStartDate = system.now();
    }

    @Test
    public void shouldFetchMatchResultAfterMatchIsFinished() {
        //given
        UUID leagueUUID = system.sampleLeagueUUID();
        UUID matchUUID = resultFacade.registerMatch(createNewMatchDTO(leagueUUID)).get().getUuid();

        //when
        system.setExternalSourceMatchList(List.of(finished()));
        system.advanceTimeBy(3, TimeUnit.HOURS);

        //then
        MatchResult result = leagueFacade.getMatchByUUID(matchUUID).get().getResult();
        assertEquals(MatchResult.DRAW, result);
    }

    @Test
    public void shouldRetryFetchMatchResultAfterMatchIsFinished() {
        //given
        UUID leagueUUID = system.sampleLeagueUUID();
        UUID matchUUID = resultFacade.registerMatch(createNewMatchDTO(leagueUUID)).get().getUuid();

        //when
        system.advanceTimeBy(3, TimeUnit.HOURS);
        system.setExternalSourceMatchList(List.of(finished()));
        system.advanceTimeBy(10, TimeUnit.MINUTES);

        //then
        MatchResult result = leagueFacade.getMatchByUUID(matchUUID).get().getResult();
        assertEquals(MatchResult.DRAW, result);
    }

    @Test
    public void shouldFetchMatchResultAfterMatchIsFinishedAfterApplicationRestart() {
        //given
        UUID leagueUUID = system.sampleLeagueUUID();
        MatchDTO matchDTO = resultFacade.registerMatch(createNewMatchDTO(leagueUUID)).get();
        UUID matchUUID = matchDTO.getUuid();

        UpdateDetails updateDetails = UpdateDetails.firstAttempt(matchDTO);
        InMemoryRepository<UpdateDetails> repository = new InMemoryRepository<>();
        repository.save(matchUUID, updateDetails);

        //when
        system.setExternalSourceMatchList(List.of(finished()));
        system.recreateResultsModule(repository);
        system.advanceTimeBy(3, TimeUnit.HOURS);

        //then
        MatchResult result = leagueFacade.getMatchByUUID(matchUUID).get().getResult();
        assertEquals(MatchResult.DRAW, result);
    }

    @Test
    public void shouldRescheduleAndTryFetchAgainIfErrorDuringDownloading() {
        //given
        UUID leagueUUID = system.sampleLeagueUUID();
        MatchDTO matchDTO = resultFacade.registerMatch(createNewMatchDTO(leagueUUID)).get();
        UUID matchUUID = matchDTO.getUuid();

        UpdateDetails updateDetails = UpdateDetails.firstAttempt(matchDTO);
        InMemoryRepository<UpdateDetails> repository = new InMemoryRepository<>();
        repository.save(matchUUID, updateDetails);

        //when
        system.setErrorDuringDownloading(true);
        system.setExternalSourceMatchList(List.of(finished()));
        system.recreateResultsModule(repository);
        system.advanceTimeBy(2, TimeUnit.HOURS);

        //then
        MatchResult result = leagueFacade.getMatchByUUID(matchUUID).get().getResult();
        assertEquals(MatchResult.NOT_SET, result);


        system.setErrorDuringDownloading(false);
        system.advanceTimeBy(1, TimeUnit.MINUTES);

        result = leagueFacade.getMatchByUUID(matchUUID).get().getResult();
        assertEquals(MatchResult.NOT_SET, result);
    }


    private NewMatchDTO createNewMatchDTO(UUID leagueUUID) {
        return new NewMatchDTO("host", "guest", 1, leagueUUID, system.now());
    }

    private MatchInfo planned() {
        matchStartDate = system.now();
        return new MatchInfo("host", "guest", 1, false, MatchResult.NOT_SET, matchStartDate);
    }

    private MatchInfo finished() {
        return new MatchInfo("host", "guest", 1, true, MatchResult.DRAW, matchStartDate);
    }

}
