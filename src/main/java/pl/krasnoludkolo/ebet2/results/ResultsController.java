package pl.krasnoludkolo.ebet2.results;

import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import pl.krasnoludkolo.ebet2.infrastructure.ResponseResolver;
import pl.krasnoludkolo.ebet2.results.api.LeagueResultsDTO;
import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;

import java.util.UUID;

@RestController
@RequestMapping("api")
class ResultsController {

    private ResultFacade resultFacade;

    @Autowired
    ResultsController(ResultFacade resultFacade) {
        this.resultFacade = resultFacade;
    }

    @GetMapping("/league/{uuid}/results")
    public HttpEntity<LeagueResultsDTO> getResultsForLeague(@PathVariable UUID uuid) {
        Option<LeagueResultsDTO> leagueResultsDTOS = resultFacade.getResultsForLeague(uuid);
        return ResponseResolver.resolve(leagueResultsDTOS);
    }

    //TODO paths
    @GetMapping("/results")
    public HttpEntity<UserResultDTO> getResultsFromLeagueToUser(@RequestParam UUID leagueUUID, @RequestParam String user) {
        Option<UserResultDTO> results = resultFacade.getResultsFromLeagueToUser(leagueUUID, user);
        return ResponseResolver.resolve(results);
    }

}
