package pl.krasnoludkolo.ebet2.bet;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.bet.query.BetForUserToMatchQuery;

import java.util.UUID;

@RestController
@RequestMapping("api")
class BetQueryController {

    @GetMapping("/match/bet")
    public ResponseEntity<BetDTO> findUserBetForMatch(@RequestParam String username, @RequestParam UUID matchUUID) {
        return new BetForUserToMatchQuery(matchUUID, username)
                .execute()
                .peekOption()
                .map(betDTO -> new ResponseEntity<>(betDTO, HttpStatus.OK))
                .getOrElse(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


}
