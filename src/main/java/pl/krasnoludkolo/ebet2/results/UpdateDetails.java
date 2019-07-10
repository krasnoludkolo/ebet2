package pl.krasnoludkolo.ebet2.results;

import pl.krasnoludkolo.ebet2.league.api.dto.MatchDTO;

import java.time.LocalDateTime;
import java.util.UUID;

final class UpdateDetails {

    final UUID matchUUID;
    final LocalDateTime scheduledTime;
    final int attempt;

    static UpdateDetails fromEntity(UpdateDetailsEntity entity) {
        return new UpdateDetails(entity.getMatchUUID(), entity.getScheduledTime(), entity.getAttempt());
    }

    static UpdateDetails firstAttempt(MatchDTO matchDTO) {
        return new UpdateDetails(matchDTO.getUuid(), matchDTO.getMatchStartDate(), 0);
    }

    private UpdateDetails(UUID matchUUID, LocalDateTime scheduledTime, int attempt) {
        this.matchUUID = matchUUID;
        this.scheduledTime = scheduledTime;
        this.attempt = attempt;
    }

    UpdateDetails nextAttempt() {
        return new UpdateDetails(matchUUID, scheduledTime, attempt + 1);
    }

    UpdateDetailsEntity toEntity() {
        return new UpdateDetailsEntity(matchUUID, scheduledTime, attempt);
    }

}
