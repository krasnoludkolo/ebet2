package pl.krasnoludkolo.ebet2.domain.bet;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.domain.Repository;
import pl.krasnoludkolo.ebet2.domain.bet.api.BetDTO;
import pl.krasnoludkolo.ebet2.domain.bet.api.BetTyp;
import pl.krasnoludkolo.ebet2.domain.bet.api.NewBetDTO;
import pl.krasnoludkolo.ebet2.domain.bet.exceptions.BetNotFound;
import pl.krasnoludkolo.ebet2.domain.bet.exceptions.DuplicateUsername;

import java.util.UUID;

class BetCRUDService {

    private Repository<Bet> repository;

    BetCRUDService(Repository<Bet> repository) {
        this.repository = repository;
    }

    public UUID addBetToMatch(UUID matchUUID, NewBetDTO newBetDTO) {
        if (matchHasBetWithUsername(matchUUID, newBetDTO.getUsername())) {
            throw new DuplicateUsername();
        }
        String username = newBetDTO.getUsername();
        BetTyp betType = newBetDTO.getBetTyp();
        Bet bet = new Bet(matchUUID, username, betType);
        UUID uuid = bet.getUuid();
        repository.save(uuid, bet);
        return uuid;
    }

    private boolean matchHasBetWithUsername(UUID matchUUID, String username) {
        return repository.findAll().filter(b -> b.getMatchUuid().equals(matchUUID)).map(Bet::getUsername).contains(username);
    }

    public Option<BetDTO> findBetByUUID(UUID betUUID) {
        return repository.findOne(betUUID).map(Bet::toDto);
    }

    public void updateBetToMatch(UUID betUUID, BetTyp betType) {
        Bet bet = repository.findOne(betUUID).getOrElseThrow(BetNotFound::new);
        bet.updateBetType(betType);
        repository.save(betUUID, bet);
    }

    public void removeBet(UUID betUUID) {
        repository.delete(betUUID);
    }

    public List<Bet> getAllBetsForMatch(UUID matchUUID) {
        return repository.findAll().filter(bet -> bet.getMatchUuid().equals(matchUUID));
    }
}
