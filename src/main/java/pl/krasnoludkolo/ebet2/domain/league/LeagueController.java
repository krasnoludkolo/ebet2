package pl.krasnoludkolo.ebet2.domain.league;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.krasnoludkolo.ebet2.domain.league.api.LeagueDTO;

import java.util.List;
import java.util.UUID;

@RestController
class LeagueController {

    private LeagueFacade leagueFacade;

    LeagueController(LeagueFacade leagueFacade) {
        this.leagueFacade = leagueFacade;
    }

    @PostMapping("/league")
    public HttpEntity<String> createNewLeague(@RequestBody String name) {
        UUID leagueUUID = leagueFacade.createLeague(name);
        return new ResponseEntity<>(leagueUUID.toString(), HttpStatus.CREATED);
    }

    @GetMapping("/leagues")
    public HttpEntity<List<LeagueDTO>> getAllLeagues() {
        List<LeagueDTO> allLeagues = leagueFacade.getAllLeagues().asJava();
        return new ResponseEntity<>(allLeagues, HttpStatus.OK);
    }
}
