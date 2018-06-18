package pl.krasnoludkolo.ebet2.bet;

import io.vavr.control.Either;
import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.bet.api.NewBetDTO;

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
    public HttpEntity<String> addBetToMatch(@RequestHeader("Authorization") String auth, @RequestBody NewBetDTO newBetDTO) {
        Either<String, UUID> betUUID = betFacade.addBetToMatch(newBetDTO, auth);
        return betUUID
                .map(uuid -> new ResponseEntity<>(uuid.toString(), HttpStatus.CREATED))
                .getOrElse(() -> new ResponseEntity<>(betUUID.getLeft(), HttpStatus.NOT_FOUND));
    }

    @GetMapping("/bet")
    public HttpEntity<BetDTO> getBetByUuid(@RequestParam UUID uuid) {
        Option<BetDTO> betByUUID = betFacade.findBetByUUID(uuid);
        return betByUUID
                .map(betDTO -> new ResponseEntity<>(betDTO, HttpStatus.CREATED))
                .getOrElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/bets")
    public HttpEntity<List<BetDTO>> getAllBetsForMatch(@RequestParam UUID matchUUID) {
        List<BetDTO> betDTOS = betFacade.getAllBetsForMatch(matchUUID).toJavaList();
        return new ResponseEntity<>(betDTOS, HttpStatus.CREATED);
    }

    @DeleteMapping("/bet")
    public HttpStatus removeBet(@RequestParam UUID uuid) {
        betFacade.removeBet(uuid);
        return HttpStatus.OK;
    }

    @PutMapping("/bet")
    public HttpEntity<String> updateBet(@RequestHeader("Authorization") String auth, @RequestBody BetDTO betDTO) {
        Either<String, UUID> updatedResult = betFacade.updateBetToMatch(betDTO.getUuid(), betDTO.getBetTyp(), auth);
        return updatedResult
                .map(uuid -> new ResponseEntity<>(uuid.toString(), HttpStatus.OK))
                .getOrElse(() -> new ResponseEntity<>(updatedResult.getLeft(), HttpStatus.BAD_REQUEST));
    }

}


