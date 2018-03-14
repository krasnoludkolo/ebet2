package pl.krasnoludkolo.ebet2.results.infrastructure;

import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.krasnoludkolo.ebet2.results.api.LeagueResultsDTO;
import pl.krasnoludkolo.ebet2.results.api.UserResultDTO;
import pl.krasnoludkolo.ebet2.results.domain.ResultFacade;

import java.util.UUID;

@RestController
class ResultsController {

    private ResultFacade resultFacade;

    @Autowired
    ResultsController(ResultFacade resultFacade) {
        this.resultFacade = resultFacade;
    }

    @GetMapping("/results")
    public HttpEntity<LeagueResultsDTO> getResultsForLeague(@RequestParam UUID uuid) {
        Option<LeagueResultsDTO> leagueResultsDTOS = resultFacade.getResultsForLeague(uuid);
        if (leagueResultsDTOS.isEmpty()) {
            return new ResponseEntity<>(new LeagueResultsDTO(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(leagueResultsDTOS.get(), HttpStatus.CREATED);
    }

    @GetMapping("/results")
    public HttpEntity<UserResultDTO> getResultsFromLeagueToUser(@RequestParam UUID leagueUUID, @RequestParam String user) {
        Option<UserResultDTO> results = resultFacade.getResultsFromLeagueToUser(leagueUUID, user);
        if (results.isEmpty()) {
            return new ResponseEntity<>(new UserResultDTO(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(results.get(), HttpStatus.CREATED);

    }

}
