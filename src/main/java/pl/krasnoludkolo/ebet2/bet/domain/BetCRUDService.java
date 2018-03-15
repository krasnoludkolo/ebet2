package pl.krasnoludkolo.ebet2.bet.domain;

import io.vavr.collection.List;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.bet.api.*;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;

import java.util.UUID;
import java.util.function.Predicate;

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
        return repository.findAll().filter(correspondMatch(matchUUID)).map(Bet::getUsername).contains(username);
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
        return repository.findAll().filter(correspondMatch(matchUUID));
    }

    private Predicate<Bet> correspondMatch(UUID matchUUID) {
        return bet -> bet.getMatchUuid().equals(matchUUID);
    }
}
