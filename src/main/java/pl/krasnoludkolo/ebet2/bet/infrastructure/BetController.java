package pl.krasnoludkolo.ebet2.bet.infrastructure;

import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.bet.api.NewBetDTO;
import pl.krasnoludkolo.ebet2.bet.domain.BetFacade;

import java.util.List;
import java.util.UUID;

@RestController
class BetController {

    private BetFacade betFacade;

    @Autowired
    BetController(BetFacade betFacade) {
        this.betFacade = betFacade;
    }

    @PostMapping("/bet")
    public HttpEntity<UUID> addBetToMatch(@RequestBody NewBetDTO newBetDTO) {
        UUID uuid = betFacade.addBetToMatch(newBetDTO);
        return new ResponseEntity<>(uuid, HttpStatus.CREATED);
    }

    @GetMapping("/bet")
    public HttpEntity<BetDTO> getBetByUuid(@RequestParam UUID uuid) {
        Option<BetDTO> betByUUID = betFacade.findBetByUUID(uuid);
        if (betByUUID.isEmpty()) {
            return new ResponseEntity<>(new BetDTO(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(betByUUID.get(), HttpStatus.CREATED);
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
    public HttpStatus updateBet(@RequestBody BetDTO betDTO) {
        betFacade.updateBetToMatch(betDTO.getUuid(), betDTO.getBetTyp());
        return HttpStatus.OK;
    }
}


