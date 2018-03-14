package pl.krasnoludkolo.ebet2.bet.domain;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.bet.api.BetTyp;
import pl.krasnoludkolo.ebet2.bet.api.NewBetDTO;

import java.util.UUID;

public class BetFacade {

    private BetCRUDService betCRUDService;

    public BetFacade(BetCRUDService betCRUDService) {
        this.betCRUDService = betCRUDService;
    }

    public UUID addBetToMatch(NewBetDTO newBetDTO) {
        return betCRUDService.addBetToMatch(newBetDTO.getMatchUUID(), newBetDTO);
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
