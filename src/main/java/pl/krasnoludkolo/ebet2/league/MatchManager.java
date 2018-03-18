package pl.krasnoludkolo.ebet2.league;

import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.api.MatchNotFound;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;
import pl.krasnoludkolo.ebet2.league.api.NewMatchDTO;

import java.util.UUID;

class MatchManager {


    private Repository<Match> matchRepository;

    MatchManager(Repository<Match> matchRepository) {
        this.matchRepository = matchRepository;
    }

    Match createNewMatch(NewMatchDTO newMatchDTO, League league) {
        Match match = Match.fromDTO(newMatchDTO, league);
        matchRepository.save(match.getUuid(), match);
        return match;
    }

    Option<Match> findByUUID(UUID uuid) {
        return matchRepository.findOne(uuid);
    }

    public void setMatchResult(UUID matchUUID, MatchResult result) {
        Match match = matchRepository.findOne(matchUUID).getOrElseThrow(MatchNotFound::new);
        match.setMatchResult(result);
        matchRepository.update(matchUUID, match);
    }

    public void removeMatchByUUID(UUID matchUUID) {
        matchRepository.delete(matchUUID);
    }
}
