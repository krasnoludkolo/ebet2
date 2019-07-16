package pl.krasnoludkolo.ebet2.league;

import io.vavr.control.Either;
import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.krasnoludkolo.ebet2.infrastructure.error.ResponseResolver;
import pl.krasnoludkolo.ebet2.league.api.LeagueError;
import pl.krasnoludkolo.ebet2.league.api.dto.*;
import pl.krasnoludkolo.ebet2.league.query.AllMatchesFromRoundFromLeagueQueryList;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api")
class LeagueController {

    private LeagueFacade leagueFacade;

    @Autowired
    LeagueController(LeagueFacade leagueFacade) {
        this.leagueFacade = leagueFacade;
    }

    @PostMapping("/league")
    public HttpEntity<String> createNewLeague(@RequestBody NewLeagueDTO league) {
        UUID leagueUUID = leagueFacade.createLeague(league.getName()).get();
        return new ResponseEntity<>(leagueUUID.toString(), HttpStatus.CREATED);
    }

    @GetMapping("/leagues")
    public HttpEntity<List<LeagueDetailsDTO>> getAllLeagues() {
        List<LeagueDetailsDTO> allLeagues = leagueFacade.getAllLeaguesDetails().asJava();
        return new ResponseEntity<>(allLeagues, HttpStatus.OK);
    }

    @PostMapping("/match")
    public ResponseEntity addMatchToLeague(@RequestBody NewMatchDTO newMatchDTO) {
        Either<LeagueError, MatchDTO> uuid = leagueFacade.addMatchToLeague(newMatchDTO);
        return ResponseResolver.resolve(uuid);
    }

    @GetMapping("/match")
    public ResponseEntity getMatchByUUID(@RequestParam UUID uuid) {
        Either<LeagueError, MatchDTO> matchByUUID = leagueFacade.getMatchByUUID(uuid);
        return ResponseResolver.resolve(matchByUUID);
    }

    @GetMapping("/matches")
    public HttpEntity<List<MatchDTO>> getAllMatchesFromRound(@RequestParam UUID leagueUUID, @RequestParam int round) {
        AllMatchesFromRoundFromLeagueQueryList query = new AllMatchesFromRoundFromLeagueQueryList(leagueUUID, round);
        List<MatchDTO> matchDTOS = query.execute().asJava();
        return new ResponseEntity<>(matchDTOS, HttpStatus.OK);
    }

    @GetMapping("league/{leagueUUID}/matches")
    public HttpEntity<LeagueDTO> getAllMatchesFromLeague(@PathVariable UUID leagueUUID) {
        Option<LeagueDTO> leagueDTO = leagueFacade.getLeagueByUUID(leagueUUID);
        return ResponseResolver.resolve(leagueDTO);
    }

    @DeleteMapping("/league")
    public HttpStatus deleteLeague(@RequestParam UUID leagueUUID) {
        leagueFacade.removeLeague(leagueUUID);
        return HttpStatus.OK;
    }

    @DeleteMapping("/match")
    public HttpStatus deleteMatch(@RequestParam UUID matchUUID) {
        leagueFacade.removeMatch(matchUUID);
        return HttpStatus.OK;
    }

}
