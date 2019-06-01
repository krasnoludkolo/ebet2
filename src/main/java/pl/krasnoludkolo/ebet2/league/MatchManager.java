package pl.krasnoludkolo.ebet2.league;

import io.vavr.control.Either;
import io.vavr.control.Option;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.api.LeagueError;
import pl.krasnoludkolo.ebet2.league.api.MatchDTO;
import pl.krasnoludkolo.ebet2.league.api.MatchResult;

import java.util.UUID;

class MatchManager {


    private Repository<Match> matchRepository;

    MatchManager(Repository<Match> matchRepository) {
        this.matchRepository = matchRepository;
    }

    Match createNewMatch(MatchDTO matchDTO, League league) {
        Match match = Match.createFromDTO(matchDTO, league);
        matchRepository.save(match.getUuid(), match);
        return match;
    }

    Option<Match> findByUUID(UUID uuid) {
        return matchRepository.findOne(uuid);
    }

    Either<LeagueError, Match> setMatchResult(UUID matchUUID, MatchResult result) {
        return matchRepository
                .findOne(matchUUID)
                .toEither(LeagueError.LEAGUE_NOT_FOUND)
                .flatMap(m -> setMatchResultAndSave(matchUUID, result, m));
    }

    private Either<LeagueError, Match> setMatchResultAndSave(UUID matchUUID, MatchResult result, Match m) {
        return m.setMatchResult(result)
                .peek(match -> matchRepository.update(matchUUID, match));
    }

    void removeMatchByUUID(UUID matchUUID) {
        matchRepository.delete(matchUUID);
    }
}
