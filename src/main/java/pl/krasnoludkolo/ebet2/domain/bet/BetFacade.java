package pl.krasnoludkolo.ebet2.domain.bet;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.domain.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.domain.bet.api.BetTyp;
import pl.krasnoludkolo.ebet2.domain.bet.api.NewBetDTO;

import java.util.UUID;

public class BetFacade {

    private BetCRUDService betCRUDService;

    public BetFacade(BetCRUDService betCRUDService) {
        this.betCRUDService = betCRUDService;
    }

    public UUID addBetToMatch(UUID matchUUID, NewBetDTO newBetDTO) {
        return betCRUDService.addBetToMatch(matchUUID, newBetDTO);
    }

    public Option<BetDTO> findBetByUUID(UUID betUUID) {
        return betCRUDService.findBetByUUID(betUUID);
    }

    public void updateBetToMatch(UUID betUUID, BetTyp betType) {
        betCRUDService.updateBetToMatch(betUUID, betType);
    }

    public void removeBet(UUID betUUID) {
        betCRUDService.removeBet(betUUID);
    }

    public List<BetDTO> getAllBetsForMatch(UUID matchUUID) {
        return betCRUDService.getAllBetsForMatch(matchUUID).map(Bet::toDto);
    }
}
