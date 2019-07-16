package pl.krasnoludkolo.ebet2.results;

import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.krasnoludkolo.ebet2.infrastructure.error.ResponseResolver;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.dto.NewMatchDTO;
import pl.krasnoludkolo.ebet2.points.api.PointsError;

@RestController
@RequestMapping("api/register")
class MatchRegistrationController {

    private final ResultFacade resultFacade;

    @Autowired
    MatchRegistrationController(ResultFacade resultFacade) {
        this.resultFacade = resultFacade;
    }

    @PostMapping("/match")
    public ResponseEntity addMatchToLeague(@RequestBody NewMatchDTO newMatchDTO) {
        Either<PointsError, MatchDTO> matchDTO = resultFacade.registerMatch(newMatchDTO);
        return ResponseResolver.resolve(matchDTO);
    }

}
