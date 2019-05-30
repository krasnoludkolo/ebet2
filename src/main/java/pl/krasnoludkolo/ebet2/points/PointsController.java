package pl.krasnoludkolo.ebet2.points;

import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import pl.krasnoludkolo.ebet2.infrastructure.error.ResponseResolver;
import pl.krasnoludkolo.ebet2.points.api.LeagueResultsDTO;
import pl.krasnoludkolo.ebet2.points.api.UserResultDTO;

import java.util.UUID;

@RestController
@RequestMapping("api")
class PointsController {

    private PointsFacade pointsFacade;

    @Autowired
    PointsController(PointsFacade pointsFacade) {
        this.pointsFacade = pointsFacade;
    }

    @GetMapping("/league/{uuid}/results")
    public HttpEntity<LeagueResultsDTO> getResultsForLeague(@PathVariable UUID uuid) {
        Option<LeagueResultsDTO> leagueResultsDTOS = pointsFacade.getResultsForLeague(uuid);
        return ResponseResolver.resolve(leagueResultsDTOS);
    }

    //TODO paths
    @GetMapping("/results")
    public HttpEntity<UserResultDTO> getResultsFromLeagueToUser(@RequestParam UUID leagueUUID, @RequestParam UUID userUUID) {
        Option<UserResultDTO> results = pointsFacade.getResultsFromLeagueToUser(leagueUUID, userUUID);
        return ResponseResolver.resolve(results);
    }

}
