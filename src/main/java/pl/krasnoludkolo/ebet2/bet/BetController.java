package pl.krasnoludkolo.ebet2.bet;

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
    public HttpEntity<UUID> addBetToMatch(@RequestHeader("Authorization") String auth, @RequestBody NewBetDTO newBetDTO) {
        UUID uuid = betFacade.addBetToMatch(newBetDTO, auth);
        return new ResponseEntity<>(uuid, HttpStatus.CREATED);
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

    @PutMapping
    public HttpStatus updateBet(@RequestHeader("Authorization") String auth, @RequestBody BetDTO betDTO) {
        betFacade.updateBetToMatch(betDTO.getUuid(), betDTO.getBetTyp(), auth);
        return HttpStatus.OK;
    }
}


