package pl.krasnoludkolo.ebet2.league.domain;

import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;
import pl.krasnoludkolo.ebet2.league.exceptions.MatchNotFound;

import java.util.UUID;

class MatchCRUDService {


    private Repository<Match> matchRepository;

    MatchCRUDService(Repository<Match> matchRepository) {
        this.matchRepository = matchRepository;
    }

    Match createNewMatch(NewMatchDTO newMatchDTO) {
        Match match = new Match(newMatchDTO);
        matchRepository.save(match.getUuid(), match);
        return match;
    }

    Option<Match> findByUUID(UUID uuid) {
        return matchRepository.findOne(uuid);
    }

    public void setMatchResult(UUID matchUUID, MatchResult result) {
        if (result.equals(MatchResult.NOT_SET)) {
            throw new IllegalArgumentException("Cannot set NOT_SET match result");
        }
        Match match = matchRepository.findOne(matchUUID).getOrElseThrow(MatchNotFound::new);
        match.setMatchResult(result);
        matchRepository.update(matchUUID, match);
    }

    public void removeMatchByUUID(UUID matchUUID) {
        matchRepository.delete(matchUUID);
    }
}
