package pl.krasnoludkolo.ebet2.league;

import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.krasnoludkolo.ebet2.league.api.LeagueDTO;
import pl.krasnoludkolo.ebet2.league.api.LeagueDetailsDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;

import java.util.List;
import java.util.UUID;

@RestController
class LeagueController {

    private LeagueFacade leagueFacade;

    @Autowired
    LeagueController(LeagueFacade leagueFacade) {
        this.leagueFacade = leagueFacade;
    }

    @PostMapping("/league")
    public HttpEntity<String> createNewLeague(@RequestBody String name) {
        UUID leagueUUID = leagueFacade.createLeague(name);
        return new ResponseEntity<>(leagueUUID.toString(), HttpStatus.CREATED);
    }

    @GetMapping("/leagues")
    public HttpEntity<List<LeagueDetailsDTO>> getAllLeagues() {
        List<LeagueDetailsDTO> allLeagues = leagueFacade.getAllLeaguesDetails().asJava();
        return new ResponseEntity<>(allLeagues, HttpStatus.OK);
    }

    @PostMapping("/match")
    public HttpEntity<UUID> addMatchToLeague(@RequestBody NewMatchDTO newMatchDTO) {
        UUID uuid = leagueFacade.addMatchToLeague(newMatchDTO);
        return new ResponseEntity<>(uuid, HttpStatus.CREATED);
    }

    @GetMapping("/match")
    public HttpEntity<MatchDTO> addMatchToLeague(@RequestParam UUID uuid) {
        Option<MatchDTO> matchDTOS = leagueFacade.getMatchByUUID(uuid);
        return matchDTOS
                .map(matchDTO -> new ResponseEntity<>(matchDTO, HttpStatus.OK))
                .getOrElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/matches")
    public HttpEntity<List<MatchDTO>> getAllMatchesFromRound(@RequestParam UUID leagueUUID, @RequestParam int round) {
        List<MatchDTO> matchDTOS = leagueFacade.getMatchesFromRound(leagueUUID, round).toJavaList();
        return new ResponseEntity<>(matchDTOS, HttpStatus.OK);
    }

    @GetMapping("league/{leagueUUID}/matches")
    public HttpEntity<LeagueDTO> getAllMatchesFromLeague(@PathVariable UUID leagueUUID) {
        Option<LeagueDTO> leagueDTO = leagueFacade.getLeagueByUUID(leagueUUID);
        return leagueDTO
                .map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .getOrElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));


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
