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

    public Either<String, UUID> addBetToMatch(NewBetDTO newBetDTO, String auth) {
        return userFacade
                .getUsername(auth)
                .flatMap(user -> betManager.addBetToMatch(newBetDTO.getMatchUUID(), newBetDTO, user));
    }

    public Option<BetDTO> findBetByUUID(UUID betUUID) {
        return betManager.findBetByUUID(betUUID);
    }

    public Either<String, UUID> updateBetToMatch(UUID betUUID, BetTyp betType, String auth) {
        Either<String, String> username = userFacade.getUsername(auth);
        if (username.isLeft()) {
            return Either.left(username.getLeft());
        }
        if (!betManager.correspondingUsername(betUUID, username.get())) {
            return Either.left("Bet with uuid:" + betUUID + " and username:" + username.get() + "not found");
        }
        return betManager.updateBetToMatch(betUUID, betType);
    }

    public void removeBet(UUID betUUID) {
        betManager.removeBet(betUUID);
    }

    public List<BetDTO> getAllBetsForMatch(UUID matchUUID) {
        return betManager.getAllBetsForMatch(matchUUID).map(Bet::toDto);
    }
}
