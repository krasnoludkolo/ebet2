package pl.krasnoludkolo.ebet2.league;

import io.vavr.control.Either;
import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.krasnoludkolo.ebet2.infrastructure.ResponseResolver;
import pl.krasnoludkolo.ebet2.league.api.*;
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
    public HttpEntity<String> createNewLeague(@RequestBody String name) {
        UUID leagueUUID = leagueFacade.createLeague(name).get();
        return new ResponseEntity<>(leagueUUID.toString(), HttpStatus.CREATED);
    }

    @GetMapping("/leagues")
    public HttpEntity<List<LeagueDetailsDTO>> getAllLeagues() {
        List<LeagueDetailsDTO> allLeagues = leagueFacade.getAllLeaguesDetails().asJava();
        return new ResponseEntity<>(allLeagues, HttpStatus.OK);
    }

    @PostMapping("/match")
    public ResponseEntity addMatchToLeague(@RequestBody NewMatchDTO newMatchDTO) {
        Either<LeagueError, UUID> uuid = leagueFacade.addMatchToLeague(newMatchDTO);
        return ResponseResolver.resolve(uuid);
    }

    @GetMapping("/match")
    public HttpEntity<MatchDTO> getMatchByUUID(@RequestParam UUID uuid) {
        Option<MatchDTO> matchDTOS = leagueFacade.getMatchByUUID(uuid);
        return ResponseResolver.resolve(matchDTOS);
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

    @PostMapping("/match/{uuid}/result")
    public HttpStatus setMatchResult(@RequestBody MatchResult matchResult, @PathVariable UUID uuid) {
        leagueFacade.setMatchResult(uuid, matchResult);
        return HttpStatus.OK;
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
