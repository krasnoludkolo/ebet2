package pl.krasnoludkolo.ebet2.bet;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.bet.api.BetTyp;
import pl.krasnoludkolo.ebet2.bet.api.NewBetDTO;

import java.util.UUID;

public class BetFacade {

    private BetManager betManager;

    public BetFacade(BetManager betManager) {
        this.betManager = betManager;
    }

    public UUID addBetToMatch(NewBetDTO newBetDTO) {
        return betManager.addBetToMatch(newBetDTO.getMatchUUID(), newBetDTO);
    }

    public Option<BetDTO> findBetByUUID(UUID betUUID) {
        return betManager.findBetByUUID(betUUID);
    }

    public void updateBetToMatch(UUID betUUID, BetTyp betType) {
        betManager.updateBetToMatch(betUUID, betType);
    }

    public void removeBet(UUID betUUID) {
        betManager.removeBet(betUUID);
    }

    public List<BetDTO> getAllBetsForMatch(UUID matchUUID) {
        return betManager.getAllBetsForMatch(matchUUID).map(Bet::toDto);
    }
}
