package pl.krasnoludkolo.ebet2.results;

import io.haste.TimeSource;
import io.vavr.collection.List;
import pl.krasnoludkolo.ebet2.external.ExternalFacade;
import pl.krasnoludkolo.ebet2.external.api.MatchInfo;
import pl.krasnoludkolo.ebet2.infrastructure.Repository;
import pl.krasnoludkolo.ebet2.league.api.dto.MatchDTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

final class ResultsUpdater {

    private final ScheduledExecutorService executorService;
    private final Repository<UpdateDetails> repository;
    private final ExternalFacade externalFacade;
    private final TimeSource timeSource;
    private final ResultFacade resultFacade;

    private static final Duration RESCHEDULE_DURATION = Duration.ofMinutes(1);

    private static final Logger LOGGER = Logger.getLogger(ResultsUpdater.class.getName());


    ResultsUpdater(ScheduledExecutorService executorService, Repository<UpdateDetails> repository, ExternalFacade externalFacade, TimeSource timeSource, ResultFacade resultFacade) {
        this.executorService = executorService;
        this.repository = repository;
        this.externalFacade = externalFacade;
        this.timeSource = timeSource;
        this.resultFacade = resultFacade;
        scheduleStoredUpdates();
    }

    private void scheduleStoredUpdates() {
        repository
                .findAll()
                .map(this::reschedule);
    }

    MatchDTO schedule(MatchDTO match) {
        saveUpdateResult(match);
        Duration offset = Duration.between(match.getMatchStartDate(), timeSource.now()).plusHours(2);
        executorService.schedule(() -> tryUpdate(match.getUuid()), offset.getSeconds(), TimeUnit.SECONDS);
        return match;
    }

    private UpdateDetails reschedule(UpdateDetails updateDetails) {
        long attempt = updateDetails.attempt;
        LocalDateTime scheduledTime = updateDetails.scheduledTime;
        UUID matchUUID = updateDetails.matchUUID;
        Duration offset = Duration.between(timeSource.now(), scheduledTime).plusHours(2).plus(RESCHEDULE_DURATION.multipliedBy(attempt));
        executorService.schedule(() -> tryUpdate(matchUUID), offset.getSeconds(), TimeUnit.SECONDS);
        return updateDetails;
    }

    private void saveUpdateResult(MatchDTO match) {
        UpdateDetails details = UpdateDetails.firstAttempt(match);
        repository.save(details.matchUUID, details);
    }

    private void reschedule(MatchDTO match) {
        updateUpdateResult(match);
        executorService.schedule(() -> tryUpdate(match.getUuid()), RESCHEDULE_DURATION.getSeconds(), TimeUnit.SECONDS);
    }

    private void updateUpdateResult(MatchDTO match) {
        UpdateDetails details = repository.findOne(match.getUuid()).getOrElseThrow(IllegalStateException::new);
        repository.update(details.matchUUID, details.nextAttempt());
    }

    private void tryUpdate(UUID matchUUID) {
        LOGGER.log(Level.INFO, () -> "Trying to update match with uuid:" + matchUUID.toString());
        resultFacade
                .getMatchByUUID(matchUUID)
                .map(match -> downloadAndUpdate(matchUUID, match));
    }

    private MatchDTO downloadAndUpdate(UUID matchUUID, MatchDTO match) {
        externalFacade
                .downloadLeague(match.getLeagueUUID())
                .map(list -> {
                    MatchInfo info = findCorrespondingMatch(match, list);
                    updateMatch(matchUUID, match, info);
                    return list;
                })
                .orElseRun(error -> reschedule(match));
        return match;
    }

    private void updateMatch(UUID matchUUID, MatchDTO match, MatchInfo info) {
        if (info.isFinished()) {
            resultFacade.registerMatchResult(match.getUuid(), info.getResult());
            repository.delete(matchUUID);
        } else {
            reschedule(match);
        }
    }

    private MatchInfo findCorrespondingMatch(MatchDTO matchDTO, List<MatchInfo> matchInfoList) {
        return matchInfoList
                .find(matchInfo -> correspondingMatch(matchDTO, matchInfo))
                .getOrElseThrow(IllegalStateException::new);
    }

    private boolean correspondingMatch(MatchDTO matchDTO, MatchInfo matchInfo) {
        return isSameMatch(matchDTO, matchInfo);
    }

    private boolean isSameMatch(MatchDTO matchDTO, MatchInfo matchInfo) {
        return Objects.equals(matchDTO.getHost(), matchInfo.getHostName())
                && Objects.equals(matchDTO.getGuest(), matchInfo.getGuestName())
                && matchDTO.getRound() == matchInfo.getRound();
    }


}
