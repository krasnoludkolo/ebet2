package pl.krasnoludkolo.ebet2.bet;

import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.bet.api.BetError;
import pl.krasnoludkolo.ebet2.bet.api.NewBetDTO;
import pl.krasnoludkolo.ebet2.infrastructure.ResponseResolver;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api")
class BetController {

    private BetFacade betFacade;

    @Autowired
    BetController(BetFacade betFacade) {
        this.betFacade = betFacade;
    }

    @PostMapping("/bet")
    public HttpEntity addBetToMatch(@RequestHeader("Authorization") String auth, @RequestBody NewBetDTO newBetDTO) {
        Either<BetError, UUID> betUUID = betFacade.addBetToMatch(newBetDTO, auth);
        return ResponseResolver.resolve(betUUID);
    }

    @GetMapping("/bet")
    public HttpEntity<BetDTO> getBetByUuid(@RequestParam UUID uuid) {
        return betFacade.findBetByUUID(uuid)
                .map(betDTO -> new ResponseEntity<>(betDTO, HttpStatus.CREATED))
                .getOrElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/bets")
    public HttpEntity<List<BetDTO>> getAllBetsForMatch(@RequestParam UUID matchUUID) {
        List<BetDTO> betDTOS = betFacade.getAllBetsForMatch(matchUUID).toJavaList();
        return new ResponseEntity<>(betDTOS, HttpStatus.OK);
    }

    @DeleteMapping("/bet")
    public HttpStatus removeBet(@RequestParam UUID uuid) {
        betFacade.removeBet(uuid);
        return HttpStatus.OK;
    }

    @PutMapping("/bet")
    public HttpEntity updateBet(@RequestHeader("Authorization") String auth, @RequestBody BetDTO betDTO) {
        Either<BetError, UUID> updatedResult = betFacade.updateBetToMatch(betDTO.getUuid(), betDTO.getBetTyp(), auth);
        return ResponseResolver.resolve(updatedResult);
    }


}


