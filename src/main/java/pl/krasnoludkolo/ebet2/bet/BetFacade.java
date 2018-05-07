package pl.krasnoludkolo.ebet2.bet;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.bet.api.BetTyp;
import pl.krasnoludkolo.ebet2.bet.api.NewBetDTO;
import pl.krasnoludkolo.ebet2.user.UserFacade;

import java.util.UUID;

public class BetFacade {

    private BetManager betManager;
    private UserFacade userFacade;

    public BetFacade(BetManager betManager, UserFacade userFacade) {
        this.betManager = betManager;
        this.userFacade = userFacade;
    }

    public UUID addBetToMatch(NewBetDTO newBetDTO, String auth) {
        Either<String, String> username = userFacade.getUsername(auth);
        if (username.isLeft()) {
            //TODO change method signature to better  error handling
            throw new IllegalArgumentException(username.getLeft());
        }
        return betManager.addBetToMatch(newBetDTO.getMatchUUID(), newBetDTO, username.get());
    }

    public Option<BetDTO> findBetByUUID(UUID betUUID) {
        return betManager.findBetByUUID(betUUID);
    }

    public Either<String, Void> updateBetToMatch(UUID betUUID, BetTyp betType, String auth) {
        Either<String, String> username = userFacade.getUsername(auth);
        if (username.isLeft()) {
            return Either.left(username.getLeft());
        }
        if (!betManager.correspondingUsername(betUUID, username.get())) {
            return Either.left("Wrong user");
        }
        betManager.updateBetToMatch(betUUID, betType);
        return Either.right(null);
    }

    public void removeBet(UUID betUUID) {
        betManager.removeBet(betUUID);
    }

    public List<BetDTO> getAllBetsForMatch(UUID matchUUID) {
        return betManager.getAllBetsForMatch(matchUUID).map(Bet::toDto);
    }
}
